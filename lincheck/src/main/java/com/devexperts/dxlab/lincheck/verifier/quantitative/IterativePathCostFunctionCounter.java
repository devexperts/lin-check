/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.verifier.quantitative;

/**
 * Implementation of this interface counts path cost incrementally.
 */
interface IterativePathCostFunctionCounter {
    
    /**
     * Returns next path cost counter with the required information for incremental counting
     * if the transition is possible, {@code null} if the transition is not satisfied.
     *
     * @param costWithNextCostCounter describes the transition
     */
    IterativePathCostFunctionCounter next(CostWithNextCostCounter<?> costWithNextCostCounter);
}
