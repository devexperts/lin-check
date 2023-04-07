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
 * This class is used to define possible transitions in {@link QuantitativeRelaxationVerifier}.
 *
 * @param <COST_COUNTER> cost counter class for the testing data structure.
 *                       It have to be defined for cleaner code.
 */
public class CostWithNextCostCounter<COST_COUNTER> {
    /**
     * Instance of the next cost counter instance.
     */
    COST_COUNTER nextCostCounter;

    /**
     * The transition cost.
     */
    int cost;

    /**
     * It is {@code true} if the transition predicate is satisfied.
     * By default {@code cost != 0} predicate is used.
     */
    boolean predicate;

    /**
     * Create new cost counter transition with the specified state and cost.
     * This constructor defines {@link #predicate transition predicate} at {@code cost != 0}.
     */
    public CostWithNextCostCounter(COST_COUNTER nextCostCounter, int cost) {
        this.nextCostCounter = nextCostCounter;
        this.cost = cost;
        this.predicate = cost != 0;
    }

    /**
     * Create new cost counter transition with the specified state and cost.
     * This constructor defines {@link #predicate transition predicate} at {@code cost != 0}.
     */
    public CostWithNextCostCounter(COST_COUNTER nextCostCounter, boolean predicate) {
        this.nextCostCounter = nextCostCounter;
        this.cost = Integer.MAX_VALUE;
        this.predicate = predicate;
    }

    /**
     * Create new cost counter transition with the specified state, cost and predicate satisfaction.
     */
    public CostWithNextCostCounter(COST_COUNTER nextCostCounter, int cost, boolean predicate) {
        this.nextCostCounter = nextCostCounter;
        this.cost = cost;
        this.predicate = predicate;
    }
}
