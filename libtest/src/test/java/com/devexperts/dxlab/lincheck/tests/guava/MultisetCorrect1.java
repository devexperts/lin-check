/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.tests.guava;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.paramgen.IntGen;
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTest;
import com.google.common.collect.ConcurrentHashMultiset;
import org.junit.Test;

@StressCTest
@Param(name = "value", gen = IntGen.class)
@Param(name = "count", gen = IntGen.class, conf = "1:10")
public class MultisetCorrect1 {
    private ConcurrentHashMultiset<Integer> q = ConcurrentHashMultiset.create();

    @Operation(params = {"value", "count"})
    public int add(int value, int count) {
        return q.add(value, count);
    }

    @Operation(params = {"value", "count"})
    public int remove(int value, int count) {
        return q.remove(value, count);
    }

    @Test
    public void test()  {
        LinChecker.check(MultisetCorrect1.class);
    }
}
