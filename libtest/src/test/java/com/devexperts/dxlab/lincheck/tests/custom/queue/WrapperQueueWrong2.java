/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.tests.custom.queue;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.paramgen.IntGen;
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTest;
import org.junit.Test;
import tests.custom.queue.Queue;
import tests.custom.queue.QueueEmptyException;
import tests.custom.queue.QueueWrong2;

@StressCTest
public class WrapperQueueWrong2 {
    private Queue queue = new QueueWrong2(10);

    @Operation(handleExceptionsAsResult = QueueEmptyException.class)
    public void put(@Param(gen = IntGen.class)int args) throws Exception {
        queue.put(args);
    }

    @Operation(handleExceptionsAsResult = QueueEmptyException.class)
    public int get() throws Exception {
        return queue.get();
    }

    @Test(expected = AssertionError.class)
    public void test() throws Exception {
        LinChecker.check(WrapperQueueWrong2.class);
    }
}
