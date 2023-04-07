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

import com.devexperts.dxlab.lincheck.runner.TestThreadExecution;

/**
 * This classloader is mostly used by runner in order to separate parallel iterations,
 * and define generated {@link TestThreadExecution test executions}.
 */
public class ExecutionClassLoader extends ClassLoader {
    public Class<? extends TestThreadExecution> defineClass(String className, byte[] bytecode) {
        return (Class<? extends TestThreadExecution>) super.defineClass(className, bytecode, 0, bytecode.length);
    }
}
