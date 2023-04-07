/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.github.lock.free.queue;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 */
public class BlockingQueueAdapter<T> implements SimpleQueue<T> {

    private final BlockingQueue<T> queue;

    public BlockingQueueAdapter() {
        queue = new LinkedBlockingQueue<T>();
    }

    @Override
    public void add(T x) {
        queue.add(x);
    }

    @Override
    public T takeOrNull() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
