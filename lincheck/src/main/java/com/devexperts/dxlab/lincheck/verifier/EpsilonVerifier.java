/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.verifier;

import com.devexperts.dxlab.lincheck.execution.ExecutionResult;
import com.devexperts.dxlab.lincheck.execution.ExecutionScenario;

/**
 * This verifier does nothing and could be used for performance benchmarking.
 */
public class EpsilonVerifier implements Verifier {

    public EpsilonVerifier(ExecutionScenario scenario, Class<?> testClass) {}

    @Override
    public boolean verifyResults(ExecutionResult results) {
        return true; // Always correct results :)
    }
}
