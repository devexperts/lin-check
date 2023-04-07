/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.strategy;

/**
 * This utility class helps to hold the current strategy. In order to run several tests in parallel,
 * every iteration have to use its own class loader, thus this holder is not shared between them.
 */
public class ManagedStrategyHolder {
    public static ManagedStrategy strategy;

    /**
     * Sets the specified strategy in the specified class loader.
     */
    public static void setStrategy(ClassLoader loader, ManagedStrategy strategy) {
        try {
            Class<?> clazz = loader.loadClass(ManagedStrategyHolder.class.getCanonicalName());
            clazz.getField("strategy").set(null, strategy);
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchFieldException e) {
            throw new IllegalStateException("Cannot set strategy to ManagedStrategyHolder", e);
        }
    }
}
