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

import com.devexperts.dxlab.lincheck.execution.ExecutionGenerator;
import com.devexperts.dxlab.lincheck.execution.RandomExecutionGenerator;
import com.devexperts.dxlab.lincheck.strategy.randomswitch.RandomSwitchCTest;
import com.devexperts.dxlab.lincheck.strategy.randomswitch.RandomSwitchCTestConfiguration;
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTest;
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTestConfiguration;
import com.devexperts.dxlab.lincheck.verifier.Verifier;
import com.devexperts.dxlab.lincheck.verifier.linearizability.LinearizabilityVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Configuration of an abstract concurrent test.
 * Should be overridden for every strategy.
 */
public abstract class CTestConfiguration {
    public static final int DEFAULT_ITERATIONS = 200;
    public static final int DEFAULT_THREADS = 2;
    public static final int DEFAULT_ACTORS_PER_THREAD = 5;
    public static final int DEFAULT_ACTORS_BEFORE = 5;
    public static final int DEFAULT_ACTORS_AFTER = 5;
    public static final Class<? extends ExecutionGenerator> DEFAULT_EXECUTION_GENERATOR = RandomExecutionGenerator.class;
    public static final Class<? extends Verifier> DEFAULT_VERIFIER = LinearizabilityVerifier.class;

    public final int iterations;
    public final int threads;
    public final int actorsPerThread;
    public final int actorsBefore;
    public final int actorsAfter;
    public final Class<? extends ExecutionGenerator> generatorClass;
    public final Class<? extends Verifier> verifierClass;

    protected CTestConfiguration(int iterations, int threads, int actorsPerThread, int actorsBefore, int actorsAfter,
        Class<? extends ExecutionGenerator> generatorClass, Class<? extends Verifier> verifierClass)
    {
        this.iterations = iterations;
        this.threads = threads;
        this.actorsPerThread = actorsPerThread;
        this.actorsBefore = actorsBefore;
        this.actorsAfter = actorsAfter;
        this.generatorClass = generatorClass;
        this.verifierClass = verifierClass;
    }

    static List<CTestConfiguration> createFromTestClass(Class<?> testClass) {
        Stream<StressCTestConfiguration> stressConfigurations = Arrays.stream(testClass.getAnnotationsByType(StressCTest.class))
            .map(ann -> new StressCTestConfiguration(ann.iterations(),
                ann.threads(), ann.actorsPerThread(), ann.actorsBefore(), ann.actorsAfter(),
                ann.generator(), ann.verifier(), ann.invocationsPerIteration(), true));
        Stream<RandomSwitchCTestConfiguration> randomSwitchConfigurations = Arrays.stream(testClass.getAnnotationsByType(RandomSwitchCTest.class))
            .map(ann -> new RandomSwitchCTestConfiguration(ann.iterations(),
                ann.threads(), ann.actorsPerThread(), ann.actorsBefore(), ann.actorsAfter(),
                ann.generator(), ann.verifier(), ann.invocationsPerIteration()));
        return Stream.concat(stressConfigurations, randomSwitchConfigurations).collect(Collectors.toList());
    }
}
