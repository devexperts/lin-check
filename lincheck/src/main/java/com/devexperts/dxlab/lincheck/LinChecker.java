/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck;

import com.devexperts.dxlab.lincheck.annotations.LogLevel;
import com.devexperts.dxlab.lincheck.execution.ExecutionGenerator;
import com.devexperts.dxlab.lincheck.execution.ExecutionScenario;
import com.devexperts.dxlab.lincheck.strategy.Strategy;
import com.devexperts.dxlab.lincheck.verifier.Verifier;

import java.util.Collections;
import java.util.List;

import static com.devexperts.dxlab.lincheck.ReporterKt.DEFAULT_LOG_LEVEL;


/**
 * This class runs concurrent tests.
 * See {@link #check(Class)} and {@link #check(Class, Options)} methods for details.
 */
public class LinChecker {
    private final Class<?> testClass;
    private final List<? extends CTestConfiguration> testConfigurations;
    private final CTestStructure testStructure;
    private final Reporter reporter;

    private LinChecker(Class<?> testClass, Options options) {
        this.testClass = testClass;
        this.testStructure = CTestStructure.getFromTestClass(testClass);
        LoggingLevel logLevel;
        if (options != null) {
            logLevel= options.logLevel;
            this.testConfigurations = Collections.singletonList(options.createTestConfigurations());
        } else {
            logLevel = getLogLevelFromAnnotation();
            this.testConfigurations = CTestConfiguration.createFromTestClass(testClass);
        }
        this.reporter = new Reporter(logLevel);
    }

    /**
     * Runs all concurrent tests described with {@code @<XXX>CTest} annotations on the specified test class.
     *
     * @throws AssertionError if algorithm or data structure is not correct.
     */
    public static void check(Class<?> testClass) {
        check(testClass, null);
    }

    /**
     * Runs concurrent test on specified class with the specified by options environment.
     * <p>
     * NOTE: this method ignores {@code @<XXX>CTest} annotations on the test class.
     *
     * @throws AssertionError if algorithm or data structure is not correct.
     */
    public static void check(Class<?> testClass, Options options) {
        new LinChecker(testClass, options).check();
    }

    /**
     * @throws AssertionError if algorithm or data structure is not correct
     */
    private void check() throws AssertionError {
        if (testConfigurations.isEmpty()) {
            throw new IllegalStateException("No Lin-Check test configuration to run");
        }
        testConfigurations.forEach(testConfiguration -> {
            try {
                checkImpl(testConfiguration);
            } catch (RuntimeException | AssertionError e) {
                throw e;
            } catch (Exception e) {
                throw new IllegalStateException(e);
            }
        });
    }

    private void checkImpl(CTestConfiguration testCfg) throws AssertionError, Exception {
        ExecutionGenerator exGen = createExecutionGenerator(testCfg.generatorClass, testCfg);
        // Run iterations
        for (int iteration = 1; iteration <= testCfg.iterations; iteration++) {
            ExecutionScenario scenario = exGen.nextExecution();
            reporter.logIteration(iteration, testCfg.iterations, scenario);
            Verifier verifier = createVerifier(testCfg.verifierClass, scenario, testClass);
            Strategy strategy = Strategy.createStrategy(testCfg, testClass, scenario, verifier, reporter);
            strategy.run();
        }
    }

    private Verifier createVerifier(Class<? extends Verifier> verifierClass, ExecutionScenario scenario,
        Class<?> testClass) throws Exception
    {
        return verifierClass.getConstructor(ExecutionScenario.class, Class.class)
            .newInstance(scenario, testClass);
    }

    private ExecutionGenerator createExecutionGenerator(Class<? extends ExecutionGenerator> generatorClass,
        CTestConfiguration testConfiguration) throws Exception
    {
        return generatorClass.getConstructor(CTestConfiguration.class, CTestStructure.class)
            .newInstance(testConfiguration, testStructure);
    }

    private LoggingLevel getLogLevelFromAnnotation() {
        LogLevel logLevelAnn = testClass.getAnnotation(LogLevel.class);
        if (logLevelAnn == null)
            return DEFAULT_LOG_LEVEL;
        return logLevelAnn.value();
    }
}
