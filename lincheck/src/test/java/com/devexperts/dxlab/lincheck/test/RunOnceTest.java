/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.test;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTest;
import org.junit.Test;

@StressCTest(threads = 3, iterations = 100, invocationsPerIteration = 10)
public class RunOnceTest {
    private A a = new A();;

    @Operation(runOnce = true)
    public void a() {
        a.a();
    }

    @Operation(runOnce = true)
    public void b() {
        a.b();
    }

    @Test
    public void test() {
        LinChecker.check(RunOnceTest.class);
    }

    class A {
        private boolean a, b;
        synchronized void a() {
            if (a)
                throw new AssertionError();
            a = true;
        }

        synchronized void b() {
            if (b)
                throw new AssertionError();
            b = true;
        }
    }
}
