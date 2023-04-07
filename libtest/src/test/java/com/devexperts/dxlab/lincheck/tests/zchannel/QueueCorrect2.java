/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.tests.zchannel;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.paramgen.IntGen;
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTest;
import z.channel.GenericMPMCQueue;

/**
 * http://landz.github.io/
 */
@StressCTest
public class QueueCorrect2 {
    private GenericMPMCQueue<Integer> q = new GenericMPMCQueue<>(16);

    @Operation
    public boolean offer(@Param(gen = IntGen.class) int value) {
        return q.offer(value);
    }

    @Operation
    public Integer poll() {
        return q.poll();
    }

    //    @Test TODO is it really correct?
    public void test() throws Exception {
        LinChecker.check(QueueCorrect2.class);
    }
}
