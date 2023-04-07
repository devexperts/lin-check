/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.tests.boundary;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.paramgen.IntGen;
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTest;
import org.cliffc.high_scale_lib.NonBlockingHashMap;
import org.junit.Test;

import java.util.Map;

@StressCTest
@Param(name = "key", gen = IntGen.class)
@Param(name = "value", gen = IntGen.class)
public class MapCorrect1 {
    private Map<Integer, Integer> map = new NonBlockingHashMap<>();

    @Operation
    public Integer put(Integer key, Integer value) {
        return map.put(key, value);
    }

    @Operation
    public Integer get(Integer key) {
        return map.get(key);
    }

    @Operation(handleExceptionsAsResult = NullPointerException.class)
    public int putIfAbsent(int key, int value) {
        return map.putIfAbsent(key, value);
    }

    @Test
    public void test() {
        LinChecker.check(MapCorrect1.class);
    }
}
