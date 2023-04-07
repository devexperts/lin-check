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

import com.devexperts.dxlab.lincheck.Actor;
import com.devexperts.dxlab.lincheck.Reporter;
import com.devexperts.dxlab.lincheck.execution.ExecutionScenario;
import com.devexperts.dxlab.lincheck.runner.ParallelThreadsRunner;
import com.devexperts.dxlab.lincheck.runner.Runner;
import com.devexperts.dxlab.lincheck.strategy.Strategy;
import com.devexperts.dxlab.lincheck.verifier.Verifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Phaser;

/**
 * This strategy
 */
public class StressStrategy extends Strategy {
    private static final int MAX_WAIT = 1000;
    private final Random random = new Random(0);

    private final int invocations;
    private final Runner runner;

    private final List<int[]> waits;

    public StressStrategy(Class<?> testClass, ExecutionScenario scenario,
        Verifier verifier, StressCTestConfiguration testCfg, Reporter reporter)
    {
        super(scenario, verifier, reporter);
        this.invocations = testCfg.invocationsPerIteration;
        // Create waits if needed
        waits = testCfg.addWaits ? new ArrayList<>() : null;
        if (testCfg.addWaits) {
            for (List<Actor> actorsForThread : scenario.parallelExecution) {
                waits.add(new int[actorsForThread.size()]);
            }
        }
        // Create runner
        Phaser phaser = new Phaser(testCfg.threads);
        runner = new ParallelThreadsRunner(scenario, this, testClass, null) {
            @Override
            public void onStart(int iThread) {
                super.onStart(iThread);
                phaser.arriveAndAwaitAdvance();
            }
        };
    }

    @Override
    public void run() throws InterruptedException {
        try {
            // Run invocations
            for (int invocation = 0; invocation < invocations; invocation++) {
                // Specify waits if needed
                if (waits != null) {
                    int maxWait = (int) ((float) invocation * MAX_WAIT / invocations) + 1;
                    for (int[] waitsForThread : waits) {
                        for (int i = 0; i < waitsForThread.length; i++) {
                            waitsForThread[i] = random.nextInt(maxWait);
                        }
                    }
                }
                verifyResults(runner.run());
            }
        } finally {
            runner.close();
        }
    }
}
