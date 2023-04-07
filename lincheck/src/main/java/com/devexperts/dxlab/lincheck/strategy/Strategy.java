/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.strategy;

import com.devexperts.dxlab.lincheck.CTestConfiguration;
import com.devexperts.dxlab.lincheck.Reporter;
import com.devexperts.dxlab.lincheck.execution.ExecutionResult;
import com.devexperts.dxlab.lincheck.execution.ExecutionScenario;
import com.devexperts.dxlab.lincheck.strategy.randomswitch.RandomSwitchCTestConfiguration;
import com.devexperts.dxlab.lincheck.strategy.randomswitch.RandomSwitchStrategy;
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTestConfiguration;
import com.devexperts.dxlab.lincheck.strategy.stress.StressStrategy;
import com.devexperts.dxlab.lincheck.verifier.Verifier;
import com.devexperts.jagent.ClassInfo;
import org.objectweb.asm.ClassVisitor;

/**
 * Implementation of this class describes how to run the generated execution.
 * <p>
 * Note that strategy could run execution several times. For strategy creating
 * {@link #createStrategy} method is used. It is impossible to add new strategy
 * without any code change.
 */
public abstract class Strategy {
    protected final ExecutionScenario scenario;
    protected final Reporter reporter;
    private final Verifier verifier;

    protected Strategy(ExecutionScenario scenario, Verifier verifier, Reporter reporter) {
        this.scenario = scenario;
        this.verifier = verifier;
        this.reporter = reporter;
    }

    protected void verifyResults(ExecutionResult results) {
        if (!verifier.verifyResults(results)) {
            reporter.logIncorrectResults(scenario, results);
            throw new AssertionError("Invalid interleaving found");
        }
    }

    public ClassVisitor createTransformer(ClassVisitor cv, ClassInfo classInfo) {
        throw new UnsupportedOperationException(getClass() + " runner does not transform classes");
    }

    public boolean needsTransformation() {
        return false;
    }

    /**
     * Creates {@link Strategy} based on {@code testCfg} type.
     */
    public static Strategy createStrategy(CTestConfiguration testCfg, Class<?> testClass,
        ExecutionScenario scenario, Verifier verifier, Reporter reporter)
    {
        if (testCfg instanceof StressCTestConfiguration) {
            return new StressStrategy(testClass, scenario, verifier,
                (StressCTestConfiguration) testCfg, reporter);
        } else if (testCfg instanceof RandomSwitchCTestConfiguration) {
            return new RandomSwitchStrategy(testClass, scenario, verifier,
                (RandomSwitchCTestConfiguration) testCfg, reporter);
        }
        throw new IllegalArgumentException("Unknown strategy configuration type: " + testCfg.getClass());
    }

    public abstract void run() throws Exception;
}
