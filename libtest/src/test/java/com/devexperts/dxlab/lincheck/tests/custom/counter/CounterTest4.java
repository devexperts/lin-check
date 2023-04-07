/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.tests.custom.counter;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTest;
import org.junit.Test;
import tests.custom.counter.Counter;
import tests.custom.counter.CounterWrong2;

@StressCTest
public class CounterTest4 {
    private Counter counter = new CounterWrong2();

    @Operation
    public int incAndGet() {
        return counter.incrementAndGet();
    }

    @Test(expected = AssertionError.class)
    public void test() {
        LinChecker.check(CounterTest4.class);
    }
}
