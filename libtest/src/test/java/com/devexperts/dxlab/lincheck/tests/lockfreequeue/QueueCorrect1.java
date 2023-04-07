/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.tests.lockfreequeue;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.paramgen.IntGen;
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTest;
import com.github.lock.free.queue.LockFreeQueue;

/**
 * https://github.com/yaitskov/lock-free-queue
 */
@StressCTest
public class QueueCorrect1 {
    private LockFreeQueue<Integer> q = new LockFreeQueue<>();

    @Operation
    public void add(@Param(gen = IntGen.class) int value) {
        q.add(value);
    }

    @Operation
    public Object takeOrNull() {
        return q.takeOrNull();
    }

    //    @Test TODO is it really correct?
    public void test() {
        LinChecker.check(QueueCorrect1.class);
    }
}
