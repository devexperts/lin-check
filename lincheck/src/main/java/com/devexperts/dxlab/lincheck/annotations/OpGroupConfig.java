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
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Set some restrictions to the group with the specified name,
 * used during the scenario generation phase.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(OpGroupConfig.OpGroupConfigs.class)
@Inherited
public @interface OpGroupConfig {
    /**
     * Name of this group used by {@link Operation#group()}.
     */
    String name() default "";

    /**
     * Set it to {@code true} for executing all actors in this group
     * from one thread. This restriction allows to test single-reader
     * and/or single-writer data structures and similar solutions.
     */
    boolean nonParallel() default false;

    /**
     * Holder annotation for {@link OpGroupConfig}.
     * Not a public API.
     */
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Inherited
    @interface OpGroupConfigs {
        OpGroupConfig[] value();
    }
}
