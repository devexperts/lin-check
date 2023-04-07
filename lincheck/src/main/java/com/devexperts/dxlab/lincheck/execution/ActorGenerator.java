/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.execution;

import com.devexperts.dxlab.lincheck.Actor;
import com.devexperts.dxlab.lincheck.paramgen.ParameterGenerator;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementations of this class generate {@link Actor actors}
 * using {@link ParameterGenerator parameter generators}.
 */
public class ActorGenerator {
    private final Method method;
    private final List<ParameterGenerator<?>> parameterGenerators;
    private final List<Class<? extends Throwable>> handledExceptions;
    private final boolean useOnce;

    public ActorGenerator(Method method, List<ParameterGenerator<?>> parameterGenerators,
        List<Class<? extends Throwable>> handledExceptions, boolean useOnce)
    {
        this.method = method;
        this.parameterGenerators = parameterGenerators;
        this.handledExceptions = handledExceptions;
        this.useOnce = useOnce;
    }

    public Actor generate() {
        return new Actor(method, parameterGenerators.stream()
            .map(ParameterGenerator::generate).collect(Collectors.toList()), handledExceptions);
    }

    public boolean useOnce() {
        return useOnce;
    }

    @Override
    public String toString() {
        return method.toString();
    }
}
