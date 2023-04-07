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

import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.execution.ExecutionGenerator;
import com.devexperts.dxlab.lincheck.execution.ExecutionScenario;
import com.devexperts.dxlab.lincheck.verifier.Verifier;

import static com.devexperts.dxlab.lincheck.ReporterKt.DEFAULT_LOG_LEVEL;

/**
 * Abstract class for test options.
 */
public abstract class Options<OPT extends Options, CTEST extends CTestConfiguration> {
    protected LoggingLevel logLevel = DEFAULT_LOG_LEVEL;
    protected int iterations = CTestConfiguration.DEFAULT_ITERATIONS;
    protected int threads = CTestConfiguration.DEFAULT_THREADS;
    protected int actorsPerThread = CTestConfiguration.DEFAULT_ACTORS_PER_THREAD;
    protected int actorsBefore = CTestConfiguration.DEFAULT_ACTORS_BEFORE;
    protected int actorsAfter = CTestConfiguration.DEFAULT_ACTORS_AFTER;
    protected Class<? extends ExecutionGenerator> executionGenerator = CTestConfiguration.DEFAULT_EXECUTION_GENERATOR;
    protected Class<? extends Verifier> verifier = CTestConfiguration.DEFAULT_VERIFIER;

    /**
     * Number of different test scenarios to be executed
     */
    public OPT iterations(int iterations) {
        this.iterations = iterations;
        return (OPT) this;
    }

    /**
     * Use the specified number of threads for the parallel part of an execution.
     * <p>
     * Note, that the the actual number of threads can be less due to some restrictions
     * like {@link Operation#runOnce()}.
     *
     * @see ExecutionScenario#parallelExecution
     */
    public OPT threads(int threads) {
        this.threads = threads;
        return (OPT) this;
    }

    /**
     * Generate the specified number of operations for each thread of the parallel part of an execution.
     * <p>
     * Note, that the the actual number of operations can be less due to some restrictions
     * like {@link Operation#runOnce()}.
     *
     * @see ExecutionScenario#parallelExecution
     */
    public OPT actorsPerThread(int actorsPerThread) {
        this.actorsPerThread = actorsPerThread;
        return (OPT) this;
    }

    /**
     * Generate the specified number of operation for the initial sequential part of an execution.
     * <p>
     * Note, that the the actual number of operations can be less due to some restrictions
     * like {@link Operation#runOnce()}.
     *
     * @see ExecutionScenario#initExecution
     */
    public OPT actorsBefore(int actorsBefore) {
        this.actorsBefore = actorsBefore;
        return (OPT) this;
    }

    /**
     * Generate the specified number of operation for the last sequential part of an execution.
     * <p>
     * Note, that the the actual number of operations can be less due to some restrictions
     * like {@link Operation#runOnce()}.
     *
     * @see ExecutionScenario#postExecution
     */
    public OPT actorsAfter(int actorsAfter) {
        this.actorsAfter = actorsAfter;
        return (OPT) this;
    }

    /**
     * Use the specified execution generator
     */
    public OPT executionGenerator(Class<? extends ExecutionGenerator> executionGenerator) {
        this.executionGenerator = executionGenerator;
        return (OPT) this;
    }

    /**
     * Use the specified verifier
     */
    public OPT verifier(Class<? extends Verifier> verifier) {
        this.verifier = verifier;
        return (OPT) this;
    }

    public abstract CTEST createTestConfigurations();

    /**
     * Set logging level, {@link DEFAULT_LOG_LEVEL} is used by default.
     */
    public OPT logLevel(LoggingLevel logLevel) {
        this.logLevel = logLevel;
        return (OPT) this;
    }
}
