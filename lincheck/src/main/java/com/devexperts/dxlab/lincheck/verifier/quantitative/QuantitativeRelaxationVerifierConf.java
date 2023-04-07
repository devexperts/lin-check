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

import com.devexperts.dxlab.lincheck.Result;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configuration for {@link QuantitativeRelaxationVerifier}
 * which should be added to a test class.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface QuantitativeRelaxationVerifierConf {
    /**
     * Relaxation factor
     */
    int factor();

    /**
     * Path cost function
     */
    PathCostFunction pathCostFunc();

    /**
     * Cost counter class.
     * <p>
     * This class represents a current data structure state
     * and has the same methods as testing operations,
     * but with an additional {@link Result} parameter
     * and another return type.
     * <p>
     * If an operation is not relaxed this cost counter
     * should check that the operation result is correct
     * and return the next state (which is a cost counter too)
     * or {@code null} in case the result is incorrect.
     * <p>
     * Otherwise, if a corresponding operation is relaxed
     * (annotated with {@link QuantitativeRelaxed}),
     * the method should return a list of all possible next states
     * with their transition cost. For this purpose,
     * a special {@link CostWithNextCostCounter} class should be used.
     * This class contains the next state and the transition cost
     * with the predicate value, which are defined in accordance
     * with the original paper. Thus, {@code List<CostWithNextCostCounter>}
     * should be returned by these methods and an empty list should
     * be returned in case no transitions are possible.
     * In order to restrict the number of possible transitions,
     * the relaxation factor should be used. It is provided via
     * a constructor, so {@code Lin-Check} uses the
     * {@code (int relaxationFactor)} constructor for the first
     * instance creation.
     */
    Class<?> costCounter();
}
