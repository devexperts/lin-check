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
import org.junit.Test;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@StressCTest
@Param(name = "key", gen = IntGen.class)
public class SetCorrect1 {
    private Set<Integer> q = Collections.newSetFromMap(new ConcurrentHashMap<Integer, Boolean>());;

    @Operation(params = {"key"})
    public boolean add(Integer params) {
        return q.add(params);
    }

    @Operation(params = {"key"})
    public boolean remove(Integer params) {
        return q.remove(params);
    }

    @Test
    public void test() throws Exception {
        LinChecker.check(SetCorrect1.class);
    }
}
