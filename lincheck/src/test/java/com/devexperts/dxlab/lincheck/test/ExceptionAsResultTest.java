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

import java.io.IOException;

@StressCTest(iterations = 1)
public class ExceptionAsResultTest {
    @Operation(handleExceptionsAsResult = NullPointerException.class)
    public void npeIsOk() {
        ((String) null).charAt(0);
    }

    @Operation(handleExceptionsAsResult = Exception.class)
    public void subclassExceptionIsOk() throws Exception {
        throw new IOException();
    }

    @Test
    public void test() {
        LinChecker.check(ExceptionAsResultTest.class);
    }
}
