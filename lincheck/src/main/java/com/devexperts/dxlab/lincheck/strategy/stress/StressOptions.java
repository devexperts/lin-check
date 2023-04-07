/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.strategy.stress;

import com.devexperts.dxlab.lincheck.Options;

/**
 * Options for {@link StressStrategy stress} strategy.
 */
public class StressOptions extends Options<StressOptions, StressCTestConfiguration> {
    protected int invocationsPerIteration = StressCTestConfiguration.DEFAULT_INVOCATIONS;
    protected boolean addWaits = true;

    /**
     * Run each test scenario {@code invocations} times.
     */
    public StressOptions invocationsPerIteration(int invocations) {
        this.invocationsPerIteration = invocations;
        return this;
    }

    /**
     * Set this to {@code false} to disable random waits between operations, enabled by default.
     */
    public StressOptions addWaits(boolean value) {
        addWaits = value;
        return this;
    }

    @Override
    public StressCTestConfiguration createTestConfigurations() {
        return new StressCTestConfiguration(iterations, threads, actorsPerThread, actorsBefore, actorsAfter,
            executionGenerator, verifier, invocationsPerIteration, addWaits);
    }
}
