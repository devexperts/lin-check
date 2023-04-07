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
import com.devexperts.dxlab.lincheck.LoggingLevel;
import com.devexperts.dxlab.lincheck.annotations.LogLevel;
import com.devexperts.dxlab.lincheck.annotations.OpGroupConfig;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.paramgen.IntGen;
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTest;
import org.jctools.queues.atomic.SpscLinkedAtomicQueue;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

@OpGroupConfig(name = "producer", nonParallel = true)
@OpGroupConfig(name = "consumer", nonParallel = true)
@StressCTest
@LogLevel(LoggingLevel.DEBUG)
public class NonParallelOpGroupTest {
    private SpscLinkedAtomicQueue<Integer> queue = new SpscLinkedAtomicQueue<>();
    private AtomicInteger i = new AtomicInteger();

    @Operation(group = "producer")
    public void offer(@Param(gen = IntGen.class) Integer x) {
        queue.offer(x);
    }

    @Operation(group = "consumer")
    public Integer poll() {
        return queue.poll();
    }

    @Operation
    public int incAndGet() {
        return i.incrementAndGet();
    }

    @Test
    public void test() {
        LinChecker.check(NonParallelOpGroupTest.class);
    }
}
