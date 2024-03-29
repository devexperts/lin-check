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

import com.devexperts.dxlab.lincheck.paramgen.ParameterGenerator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to specify parameter generators.
 * @see ParameterGenerator
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER, ElementType.TYPE})
@Repeatable(Param.Params.class)
@Inherited
public @interface Param {
    /**
     * If the annotation is set on a class, creates a {@link ParameterGenerator parameter generator}
     * which can be used in {@link Operation operations} by this name. If is set on an operation,
     * uses the specified named parameter generator which is created as described before.
     */
    String name() default "";

    /**
     * Specifies the {@link ParameterGenerator} class which should be used for this parameter.
     */
    Class<? extends ParameterGenerator<?>> gen() default ParameterGenerator.Dummy.class;

    /**
     * Specifies the configuration for the {@link #gen() parameter generator}.
     */
    String conf() default "";

    /**
     * Holder annotation for {@link Param}.
     * Not a public API.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    @interface Params {
        Param[] value();
    }
}
