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
import org.cliffc.high_scale_lib.NonBlockingHashSet;
import org.junit.Test;

@StressCTest
@Param(name = "key", gen = IntGen.class)
public class SetCorrect1 {
    private NonBlockingHashSet<Integer> q = new NonBlockingHashSet<>();

    @Operation(params = {"key"})
    public boolean add(int key) {
        return q.add(key);
    }

    @Operation(params = {"key"})
    public boolean remove(int key) {
        return q.remove(key);
    }

    @Test
    public void test() {
        LinChecker.check(SetCorrect1.class);
    }
}
