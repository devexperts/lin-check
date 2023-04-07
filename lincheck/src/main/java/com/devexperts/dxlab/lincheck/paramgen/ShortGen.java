/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.paramgen;

public class ShortGen implements ParameterGenerator<Short> {
    private final IntGen intGen;

    public ShortGen(String configuration) {
        intGen = new IntGen(configuration);
        intGen.checkRange(Short.MIN_VALUE, Short.MAX_VALUE, "short");
    }

    public Short generate() {
        return (short) (int) intGen.generate();
    }
}
