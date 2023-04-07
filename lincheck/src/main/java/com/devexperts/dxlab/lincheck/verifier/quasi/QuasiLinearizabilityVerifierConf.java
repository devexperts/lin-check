/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.verifier.quasi;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation with quasi-linearizability verifier
 * parameters should be presented on a test class
 * if the {@link QuasiLinearizabilityVerifier} is used.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface QuasiLinearizabilityVerifierConf {
    /**
     * Relaxation factor.
     */
    int factor();

    /**
     * The sequential implementation of the
     * testing data structure  with the same
     * methods as operations in the test class
     * (same name, signature), but with a
     * correct sequential implementations.
     * It also should have an empty constructor
     * which creates the initial state
     * (similar to the test class).
     */
    Class<?> sequentialImplementation();
}
