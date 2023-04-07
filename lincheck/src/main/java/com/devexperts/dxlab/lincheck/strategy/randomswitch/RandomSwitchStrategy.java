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

import com.devexperts.dxlab.lincheck.Reporter;
import com.devexperts.dxlab.lincheck.execution.ExecutionScenario;
import com.devexperts.dxlab.lincheck.strategy.ManagedStrategy;
import com.devexperts.dxlab.lincheck.verifier.Verifier;

/**
 * This managed strategy switches current thread to a random one with the specified probability.
 * In addition it tries to avoid both communication and resource deadlocks and to check for livelocks.
 * <p>
 * TODO: not developed yet, dummy implementation only
 */
public class RandomSwitchStrategy extends ManagedStrategy {
    private final int invocations;

    public RandomSwitchStrategy(Class<?> testClass, ExecutionScenario scenario,
        Verifier verifier, RandomSwitchCTestConfiguration testCfg, Reporter reporter)
    {
        super(testClass, scenario, verifier, reporter);
        this.invocations = testCfg.invocationsPerIteration;
    }

    @Override
    protected void runImpl() throws Exception {
        for (int i = 0; i < invocations; i++)
            verifyResults(runInvocation());
    }

    @Override
    public void onStart(int iThread) {
        super.onStart(iThread);
    }

    @Override
    public void beforeSharedVariableRead(int iThread, int codeLocation) {
        Thread.yield();
    }

    @Override
    public void beforeSharedVariableWrite(int iThread, int codeLocation) {
        Thread.yield();
    }
}
