/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.strategy.randomswitch;

import com.devexperts.dxlab.lincheck.CTestConfiguration;
import com.devexperts.dxlab.lincheck.execution.ExecutionGenerator;
import com.devexperts.dxlab.lincheck.verifier.Verifier;

/**
 * Configuration for {@link RandomSwitchStrategy random-switch} strategy.
 */
public class RandomSwitchCTestConfiguration extends CTestConfiguration {
    public static final int DEFAULT_INVOCATIONS = 1_000;

    public final int invocationsPerIteration;

    public RandomSwitchCTestConfiguration(int iterations, int threads, int actorsPerThread, int actorsBefore,
        int actorsAfter, Class<? extends ExecutionGenerator> generatorClass, Class<? extends Verifier> verifierClass,
        int invocationsPerIteration)
    {
        super(iterations, threads, actorsPerThread, actorsBefore, actorsAfter, generatorClass, verifierClass);
        this.invocationsPerIteration = invocationsPerIteration;
    }
}
