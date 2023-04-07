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

import com.devexperts.dxlab.lincheck.Options;

/**
 * Options for {@link RandomSwitchStrategy random-switch} strategy.
 */
public class RandomSwitchOptions extends Options<RandomSwitchOptions, RandomSwitchCTestConfiguration> {
    protected int invocationsPerIteration = RandomSwitchCTestConfiguration.DEFAULT_INVOCATIONS;

    /**
     * Run each test scenario {@code invocations} times.
     */
    public RandomSwitchOptions invocationsPerIteration(int invocations) {
        this.invocationsPerIteration = invocations;
        return this;
    }

    @Override
    public RandomSwitchCTestConfiguration createTestConfigurations() {
        return new RandomSwitchCTestConfiguration(iterations, threads, actorsPerThread, actorsBefore, actorsAfter,
            executionGenerator, verifier, invocationsPerIteration);
    }
}
