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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that the method it is presented on is relaxed
 * in accordance with a contract for {@link QuantitativeRelaxationVerifier}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface QuantitativeRelaxed {
//    String group() default ""; todo: introduce relaxation groups in order to count path functions separately
}
