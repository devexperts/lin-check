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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    private Utils() {}

    private static volatile int consumedCPU = (int) System.currentTimeMillis();

    /**
     * Busy wait, used by stress strategy.
     */
    public static void consumeCPU(int tokens) {
        int t = consumedCPU; // volatile read
        for (int i = tokens; i > 0; i--)
            t += (t * 0x5DEECE66DL + 0xBL + i) & (0xFFFFFFFFFFFFL);
        if (t == 42)
            consumedCPU += t;
    }

    /**
     * Creates test class instance using empty arguments constructor
     */
    public static Object createTestInstance(Class<?> testClass) {
        try {
            return testClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Test class should have empty public constructor", e);
        }
    }

    /**
     * Executes list of actors on the test instance sequentially (in one thread)
     * and returns their results.
     */
    public static List<Result> executeActors(Object testInstance, List<Actor> actors) {
        try {
            return actors.stream().map(a -> executeActor(testInstance, a))
                .collect(Collectors.toList());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Executes the specified actor on the test instance and returns its result.
     */
    public static Result executeActor(Object testInstance, Actor actor) {
        try {
            Method m = testInstance.getClass().getMethod(actor.method.getName(), actor.method.getParameterTypes());
            Object res = m.invoke(testInstance, actor.arguments);
            if (actor.method.getReturnType() == void.class)
                return Result.createVoidResult();
            else
                return Result.createValueResult(res);
        } catch (InvocationTargetException invE) {
            Class<? extends Throwable> eClass = invE.getCause().getClass();
            for (Class<? extends Throwable> ec : actor.handledExceptions) {
                if (ec.isAssignableFrom(eClass))
                    return Result.createExceptionResult(eClass);
            }
            throw new IllegalStateException("Invalid exception as a result", invE);
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new IllegalStateException("Cannot invoke method " + actor.method, e);
        }
    }
}
