/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.runner;

import com.devexperts.dxlab.lincheck.Actor;
import com.devexperts.dxlab.lincheck.Result;
import com.devexperts.dxlab.lincheck.Utils;
import com.devexperts.dxlab.lincheck.execution.ExecutionResult;
import com.devexperts.dxlab.lincheck.execution.ExecutionScenario;
import com.devexperts.dxlab.lincheck.strategy.Strategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * This runner executes parallel scenario' part in different threads.
 * It is pretty useful for stress testing or if you do not care about context switches.
 */
public class ParallelThreadsRunner extends Runner {
    private final List<TestThreadExecution> testThreadExecutions;
    private final ExecutorService executor;

    public ParallelThreadsRunner(ExecutionScenario scenario, Strategy strategy, Class<?> testClass, List<int[]> waits)
    {
        super(scenario, strategy, testClass);
        int nThreads = scenario.parallelExecution.size();
        // Create TestThreadExecution's
        boolean waitsEnabled = waits != null;
        testThreadExecutions = new ArrayList<>(nThreads);
        for (int t = 0; t < nThreads; t++) {
            List<Actor> actors = scenario.parallelExecution.get(t);
            testThreadExecutions.add(TestThreadExecutionGenerator.create(this, t, actors, waitsEnabled));
        }
        // Set waits if needed
        if (waitsEnabled) {
            for (int t = 0; t < nThreads; t++) {
                testThreadExecutions.get(t).waits = waits.get(t);
            }
        }
        // Fixed thread pool executor to run TestThreadExecution
        executor = Executors.newFixedThreadPool(nThreads, TestThread::new);
    }

    @Override
    public ExecutionResult run() throws InterruptedException {
        Object testInstance = Utils.createTestInstance(testClass);
        testThreadExecutions.forEach(ex -> ex.testInstance = testInstance);
        // Run init part
        List<Result> initResults = Utils.executeActors(testInstance, scenario.initExecution);
        // Run parallel part
        List<List<Result>> parallelResults = executor.invokeAll(testThreadExecutions).stream() // get futures
            .map(f -> {
                try {
                    return Arrays.asList(f.get()); // wait and get results
                } catch (InterruptedException | ExecutionException e) {
                    throw new IllegalStateException(e);
                }
            }).collect(Collectors.toList());
        // Run post part
        List<Result> postResults = Utils.executeActors(testInstance, scenario.postExecution);
        // Return the execution result
        return new ExecutionResult(initResults, parallelResults, postResults);
    }

    @Override
    public void onStart(int iThread) {
        super.onStart(iThread);
        ((TestThread) Thread.currentThread()).iThread = iThread;
    }

    @Override
    public void close() {
        executor.shutdown();
    }

    /**
     * All {@link TestThreadExecution}s are executing in this threads.
     */
    public class TestThread extends Thread {
        public int iThread;

        private TestThread(Runnable r) {
            super(r);
        }
    }
}
