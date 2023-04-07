/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package thesis_example;

public class CounterCorrect1 {
    private int c = 0;

    public synchronized int incrementAndGet() {
        c++;
        return c;
    }

    public static void main(String[] args) {
        System.out.println(new CounterCorrect1().incrementAndGet());
    }
}
