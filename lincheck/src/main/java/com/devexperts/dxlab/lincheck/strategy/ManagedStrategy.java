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

import com.devexperts.dxlab.lincheck.Reporter;
import com.devexperts.dxlab.lincheck.execution.ExecutionResult;
import com.devexperts.dxlab.lincheck.execution.ExecutionScenario;
import com.devexperts.dxlab.lincheck.runner.ParallelThreadsRunner;
import com.devexperts.dxlab.lincheck.runner.Runner;
import com.devexperts.dxlab.lincheck.verifier.Verifier;
import com.devexperts.jagent.ClassInfo;
import org.objectweb.asm.ClassVisitor;

/**
 * This is an abstract class for all managed strategies.
 * This abstraction helps to choose a proper {@link Runner},
 * to transform byte-code in order to insert required for managing instructions,
 * and to hide class loading problems from the strategy algorithm.
 */
public abstract class ManagedStrategy extends Strategy {
    /**
     * Number of threads
     */
    protected final int nThreads;

    private final Runner runner;
    private ManagedStrategyTransformer transformer;

    protected ManagedStrategy(Class<?> testClass, ExecutionScenario scenario, Verifier verifier, Reporter reporter) {
        super(scenario, verifier, reporter);
        nThreads = scenario.parallelExecution.size();
        runner = new ParallelThreadsRunner(scenario, this, testClass, null) {
            @Override
            public void onStart(int iThread) {
                super.onStart(iThread);
                ManagedStrategy.this.onStart(iThread);
            }

            @Override
            public void onFinish(int iThread) {
                super.onFinish(iThread);
                ManagedStrategy.this.onFinish(iThread);
            }
        };
        ManagedStrategyHolder.setStrategy(runner.classLoader, this);
    }

    @Override
    public ClassVisitor createTransformer(ClassVisitor cv, ClassInfo classInfo) {
        return transformer = new ManagedStrategyTransformer(cv, classInfo);
    }

    @Override
    public boolean needsTransformation() {
        return true;
    }

    @Override
    public final void run() throws Exception {
        try {
            runImpl();
        } finally {
            runner.close();
        }
    }

    /**
     * Runs next invocation with the same {@link ExecutionScenario scenario}.
     *
     * @return invocation results for each executed actor.
     */
    protected final ExecutionResult runInvocation() throws InterruptedException {
        return runner.run();
    }

    /**
     * Returns a {@link StackTraceElement} described the specified code location
     *
     * @param codeLocation code location identifier which is inserted by transformer
     */
    protected final StackTraceElement getLocationDescription(int codeLocation) {
        return transformer.getCodeLocations().get(codeLocation);
    }

    /**
     * This method implements the strategy logic
     *
     * @throws Exception an occurred exception (at least by {@link Verifier}) during the testing
     */
    protected abstract void runImpl() throws Exception;

    // == LISTENING EVENTS ==

    /**
     * This method is executed as the first thread action.
     * @param iThread the number of the executed thread according to the {@link ExecutionScenario scenario}.
     */
    public void onStart(int iThread) {}

    /**
     * This method is executed as the last thread action if no exception has been thrown.
     * @param iThread the number of the executed thread according to the {@link ExecutionScenario scenario}.
     */
    public void onFinish(int iThread) {}

    /**
     * This method is executed before a shared variable read operation.
     * @param iThread the number of the executed thread according to the {@link ExecutionScenario scenario}.
     * @param codeLocation the byte-code location identifier of this operation.
     */
    public void beforeSharedVariableRead(int iThread, int codeLocation) {}

    /**
     * This method is executed before a shared variable write operation (including CAS).
     * @param iThread the number of the executed thread according to the {@link ExecutionScenario scenario}.
     * @param codeLocation the byte-code location identifier of this operation.
     */
    public void beforeSharedVariableWrite(int iThread, int codeLocation) {}

    /**
     *
     * @param iThread the number of the executed thread according to the {@link ExecutionScenario scenario}.
     * @param codeLocation the byte-code location identifier of this operation.
     * @param monitor
     */
    public void beforeLockAcquire(int iThread, int codeLocation, Object monitor) {}

    /**
     *
     * @param iThread the number of the executed thread according to the {@link ExecutionScenario scenario}.
     * @param codeLocation the byte-code location identifier of this operation.
     * @param monitor
     */
    public void afterLockRelease(int iThread, int codeLocation, Object monitor) {}

    /**
     *
     * @param iThread the number of the executed thread according to the {@link ExecutionScenario scenario}.
     * @param codeLocation the byte-code location identifier of this operation.
     * @param withTimeout {@code true} if is invoked with timeout, {@code false} otherwise.
     */
    public void beforePark(int iThread, int codeLocation, boolean withTimeout) {}

    /**
     *
     * @param iThread the number of the executed thread according to the {@link ExecutionScenario scenario}.
     * @param codeLocation the byte-code location identifier of this operation.
     * @param thread
     */
    public void afterUnpark(int iThread, int codeLocation, Object thread) {}

    /**
     *
     * @param iThread the number of the executed thread according to the {@link ExecutionScenario scenario}.
     * @param codeLocation the byte-code location identifier of this operation.
     * @param monitor
     * @param withTimeout {@code true} if is invoked with timeout, {@code false} otherwise.
     */
    public void beforeWait(int iThread, int codeLocation, Object monitor, boolean withTimeout) {}

    /**
     *
     * @param iThread the number of the executed thread according to the {@link ExecutionScenario scenario}.
     * @param codeLocation the byte-code location identifier of this operation.
     * @param monitor
     * @param notifyAll
     */
    public void afterNotify(int iThread, int codeLocation, Object monitor, boolean notifyAll) {}

    /**
     *
     * @param iThread the number of the executed thread according to the {@link ExecutionScenario scenario}.
     * @param codeLocation the byte-code location identifier of this operation.
     * @param iInterruptedThread
     */
    public void afterThreadInterrupt(int iThread, int codeLocation, int iInterruptedThread) {}

    // == UTILITY METHODS

    /**
     * This method is invoked by transformed via {@link ManagedStrategyTransformer} code,
     * it helps to determine the number of thread we are executing on.
     *
     * @return the number of the current thread according to the {@link ExecutionScenario execution scenario}.
     */
    public int currentThreadNumber() {
        Thread t = Thread.currentThread();
        if (t instanceof ParallelThreadsRunner.TestThread) {
            return ((ParallelThreadsRunner.TestThread) t).iThread;
        } else {
            return nThreads;
        }
    }
}
