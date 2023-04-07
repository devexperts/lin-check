/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.tests.custom.transfer;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.paramgen.IntGen;
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import tests.custom.transfer.Accounts;
import tests.custom.transfer.AccountsWrong1;
import tests.custom.transfer.AccountsWrong2;
import tests.custom.transfer.AccountsWrong3;
import tests.custom.transfer.AccountsWrong4;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

@RunWith(Parameterized.class)
public class AccountsTest {
    private static Supplier<Accounts> accountCreator;

    @Parameterized.Parameters(name = "{1}")
    public static List<Object[]> params() {
        return Arrays.<Object[]>asList(
            new Object[] {(Supplier<Accounts>) AccountsWrong1::new, "AccountsWrong1"},
            new Object[] {(Supplier<Accounts>) AccountsWrong2::new, "AccountsWrong2"},
            new Object[] {(Supplier<Accounts>) AccountsWrong3::new, "AccountsWrong3"},
            new Object[] {(Supplier<Accounts>) AccountsWrong4::new, "AccountsWrong4"}
        );
    }

    public AccountsTest(Supplier<Accounts> accountCreator, String desc) {
        AccountsTest.accountCreator = accountCreator;
    }

    @StressCTest(threads = 3)
    @Param(name = "id", gen = IntGen.class, conf = "1:4")
    @Param(name = "amount", gen = IntGen.class)
    public static class AccountsLinearizabilityTest {
        private Accounts acc = accountCreator.get();

        @Operation(params = {"id"})
        public int getAmount(int key) {
            return acc.getAmount(key);
        }

        @Operation(params = {"id", "amount"})
        public void setAmount(int key, int value) {
            acc.setAmount(key, value);
        }

        @Operation
        public void transfer(@Param(name = "id") int from, @Param(name = "id") int to,
            @Param(name = "amount") int amount)
        {
            acc.transfer(from, to, amount);
        }
    }

    @Test(expected = AssertionError.class)
    public void test() throws Exception {
        LinChecker.check(AccountsLinearizabilityTest.class);
    }
}
