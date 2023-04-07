/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.tests.juc.blocking_queue;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.paramgen.IntGen;
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTest;
import org.junit.Test;

import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

@StressCTest
public class BlockingQueueTest1 {
    private BlockingQueue<Integer> q = new ArrayBlockingQueue<>(10);

    @Operation
    public boolean add(@Param(gen = IntGen.class) Integer value) {
        return q.add(value);
    }

    @Operation(handleExceptionsAsResult = NoSuchElementException.class)
    public Integer element() {
        return q.element();
    }

    @Operation(handleExceptionsAsResult = NoSuchElementException.class)
    public Integer remove() {
        return q.remove();
    }

    @Operation(handleExceptionsAsResult = NoSuchElementException.class)
    public Integer poll() {
        return q.poll();
    }

    @Test
    public void test() {
        LinChecker.check(BlockingQueueTest1.class);
    }
}

