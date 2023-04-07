/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.execution;

import com.devexperts.dxlab.lincheck.CTestConfiguration;
import com.devexperts.dxlab.lincheck.CTestStructure;

/**
 * Implementation of this interface generates execution scenarios.
 * By default, {@link RandomExecutionGenerator} is used.
 * <p>
 * IMPORTANT!
 * All implementations should have the same constructor as {@link ExecutionGenerator} has.
 */
public abstract class ExecutionGenerator {
    protected final CTestConfiguration testConfiguration;
    protected final CTestStructure testStructure;

    protected ExecutionGenerator(CTestConfiguration testConfiguration, CTestStructure testStructure) {
        this.testConfiguration = testConfiguration;
        this.testStructure = testStructure;
    }

    public abstract ExecutionScenario nextExecution();
}
