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

import com.devexperts.dxlab.lincheck.LoggingLevel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation should be added to a test class
 * to specify logging level. By default {@link LoggingLevel#ERROR} is used.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface LogLevel {
    LoggingLevel value();
}
