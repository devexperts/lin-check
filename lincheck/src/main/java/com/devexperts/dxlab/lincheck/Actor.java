/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck;

import com.devexperts.dxlab.lincheck.annotations.Operation;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The actor entity describe the operation with its parameters
 * which is executed during the testing.
 *
 * @see Operation
 */
public class Actor {
    public final Method method;
    public final Object[] arguments;
    public final List<Class<? extends Throwable>> handledExceptions;

    public Actor(Method method, List<Object> arguments, List<Class<? extends Throwable>> handledExceptions) {
        this(method, arguments.toArray(), handledExceptions);
    }

    public Actor(Method method, Object[] arguments, List<Class<? extends Throwable>> handledExceptions) {
        this.method = method;
        this.arguments = arguments;
        this.handledExceptions = handledExceptions;
    }

    @Override
    public String toString() {
        return method.getName() + "(" + Stream.of(arguments).map(Object::toString).collect(Collectors.joining(", ")) + ")";
    }

    public boolean handlesExceptions() {
        return !handledExceptions.isEmpty();
    }
}
