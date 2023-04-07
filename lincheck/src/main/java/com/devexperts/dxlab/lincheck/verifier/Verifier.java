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
import com.devexperts.dxlab.lincheck.verifier.linearizability.LinearizabilityVerifier;

/**
 * Implementation of this interface verifies that execution is correct with respect to the algorithm contract.
 * By default, {@link LinearizabilityVerifier} is used.
 * <p>
 * IMPORTANT!
 * All implementations should have {@code (ExecutionScenario scenario, Class<?> testClass)} constructor.
 */
public interface Verifier {
    /**
     * Verifies the specified results for correctness.
     * Returns {@code true} if results are possible, {@code false} otherwise.
     */
    boolean verifyResults(ExecutionResult results);
}
