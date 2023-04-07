/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package tests.custom.counter;

public class CounterWrong2 implements Counter {
    private int c;

    public CounterWrong2() {
        c = 0;
    }

    @Override
    public int incrementAndGet() {
        return ++c;
    }
}
