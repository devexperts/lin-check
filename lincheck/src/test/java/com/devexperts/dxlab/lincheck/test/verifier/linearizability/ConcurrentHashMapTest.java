/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.test.verifier.linearizability;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.paramgen.IntGen;
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTest;
import com.devexperts.dxlab.lincheck.verifier.linearizability.LinearizabilityVerifier;
import org.junit.Test;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Param(name = "key", gen = IntGen.class, conf = "1:5")
@StressCTest(actorsPerThread = 50)
public class ConcurrentHashMapTest {
    private ConcurrentHashMap<Integer, Integer> map = new ConcurrentHashMap<>();

    @Operation
    public Integer put(@Param(name = "key") Integer key, Integer value) {
        return map.put(key, value);
    }

    @Operation
    public Integer get(@Param(name = "key") Integer key) {
        return map.get(key);
    }

    @Test
    public void test() {
        LinChecker.check(ConcurrentHashMapTest.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConcurrentHashMapTest that = (ConcurrentHashMapTest) o;
        return map.equals(that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(map);
    }
}
