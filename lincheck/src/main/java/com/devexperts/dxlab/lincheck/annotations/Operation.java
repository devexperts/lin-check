/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark your method with this annotation in order
 * to use it in concurrent testing as an operation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Operation {
    /**
     * Binds the arguments of this operations with the specified {@link Param parameter configurations}
     * by theirs {@link Param#name()} names
     */
    String[] params() default {};

    /**
     * Set it to {@code true} if you this operation should be called
     * at most once during the test invocation; {@code false} by default.
     */
    boolean runOnce() default false;

    /**
     * Specifies the operation group which can add some execution restriction.
     * @see OpGroupConfig#name()
     */
    String group() default "";

    /**
     * Handle the specified exceptions as a result of this operation invocation.
     */
    Class<? extends Throwable>[] handleExceptionsAsResult() default {};
}
