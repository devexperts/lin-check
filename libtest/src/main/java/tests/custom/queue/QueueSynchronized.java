/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package tests.custom.queue;

import java.util.Arrays;

public class QueueSynchronized implements Queue {
    private int indGet;
    private int indPut;
    private int countElements;

    private int[] items;

    private int inc(int i) {
        return (++i == items.length ? 0 : i);
    }

    public QueueSynchronized(int capacity) {
        items = new int[capacity];

        indPut = 0;
        indGet = 0;
        countElements = 0;

    }

    @Override
    public synchronized void put(int x) throws QueueFullException {
        if (countElements == items.length) {
            throw new QueueFullException();
        }
        items[indPut] = x;
        indPut = inc(indPut);
        countElements++;
    }

    @Override
    public synchronized int get() throws QueueEmptyException {
        if (countElements == 0) {
            throw new QueueEmptyException();
        }
        int ret = items[indGet];
        indGet = inc(indGet);
        countElements--;
        return ret;
    }

    @Override
    public String toString() {
        return "QueueWithoutAnySync{" +
                "indGet=" + indGet +
                ", indPut=" + indPut +
                ", countElements=" + countElements +
                ", items=" + Arrays.toString(items) +
                '}';
    }
}
