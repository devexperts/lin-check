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

public class ByteGen implements ParameterGenerator<Byte> {
    private final IntGen intGen;

    public ByteGen(String configuration) {
        intGen = new IntGen(configuration);
        intGen.checkRange(Byte.MIN_VALUE, Byte.MAX_VALUE, "byte");
    }

    public Byte generate() {
        return (byte) (int) intGen.generate();
    }
}
