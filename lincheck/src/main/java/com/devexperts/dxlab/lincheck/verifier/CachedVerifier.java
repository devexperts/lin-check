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

import java.util.HashSet;
import java.util.Set;

/**
 * This verifier cached the already verified results in a hash table,
 * and look into this hash table at first. In case of many invocations
 * with the same scenario, this optimization improves the verification
 * phase significantly.
 */
public abstract class CachedVerifier implements Verifier {
    private final Set<ExecutionResult> previousResults = new HashSet<>();

    @Override
    public final boolean verifyResults(ExecutionResult results) {
        if (!previousResults.add(results))
            return true;
        return verifyResultsImpl(results);
    }

    public abstract boolean verifyResultsImpl(ExecutionResult results);
}
