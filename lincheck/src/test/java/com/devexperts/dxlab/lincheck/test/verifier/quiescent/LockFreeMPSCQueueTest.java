/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.test.verifier.quiescent;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.OpGroupConfig;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.paramgen.IntGen;
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTest;
import com.devexperts.dxlab.lincheck.verifier.quiescent.QuiescentConsistencyVerifier;
import com.devexperts.dxlab.lincheck.verifier.quiescent.QuiescentConsistent;
import org.junit.Test;

@StressCTest(verifier = QuiescentConsistencyVerifier.class)
@OpGroupConfig(name = "consumer", nonParallel = true)
public class LockFreeMPSCQueueTest {
    private LockFreeMPSCQueue<Integer> q = new LockFreeMPSCQueue<>();

    @Operation(group = "consumer")
    @QuiescentConsistent
    public Integer removeFirstOrNull() {
        return q.removeFirstOrNull();
    }

    @Operation
    public boolean addLast(@Param(gen = IntGen.class) Integer val) {
        return q.addLast(val);
    }

    @Operation(runOnce = true)
    @QuiescentConsistent
    public void close() {
        q.close();
    }

    @Test
    public void test() {
        LinChecker.check(LockFreeMPSCQueueTest.class);
    }
}
