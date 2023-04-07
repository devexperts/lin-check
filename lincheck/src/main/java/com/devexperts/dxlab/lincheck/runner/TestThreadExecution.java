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

import com.devexperts.dxlab.lincheck.Result;

import java.util.concurrent.Callable;

/**
 * Instance of this class represents the test execution for ONE thread. Several instances should be ran in parallel.
 * All implementations of this class should be generated via {@link TestThreadExecutionGenerator}.
 *
 * <p> This class should be public for having access from generated classes.
 */
public abstract class TestThreadExecution implements Callable<Result[]> {
    // The following fields are assigned in TestThreadExecutionGenerator
    protected Runner runner;
    public Object testInstance;
    protected Object[] objArgs;
    public int[] waits; // for StressStrategy

    // It is better to return List<Result>,
    // but such implementation requires to have a synthetic
    // method to support generics and the byte-code generation
    // is more bug-prone. If you need to use
    // List<Result>, see Arrays.asList(..) method.
    public abstract Result[] call();
}
