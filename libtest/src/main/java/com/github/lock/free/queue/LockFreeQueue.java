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

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Daneel Yaitskov
 */
public class LockFreeQueue<T> implements SimpleQueue<T> {

    // never empty
    private final AtomicLong length = new AtomicLong(1L);
    private final Node stub = new Node(null);
    private final AtomicReference<Node<T>> head = new AtomicReference<Node<T>>(stub);
    private final AtomicReference<Node<T>> tail = new AtomicReference<Node<T>>(stub);

    @Override
    public void add(T x) {
        addNode(new Node<T>(x));
        length.incrementAndGet();
    }

    @Override
    public T takeOrNull() {
        while (true) {
            long l = length.get();
            if (l == 1) {
                return null;
            }
            if (length.compareAndSet(l, l - 1)) {
                break;
            }
        }
        while (true) {
            Node<T> r = head.get();
            if (r == null) {
                throw new IllegalStateException("null head");
            }
            if (r.next.get() == null) {
                length.incrementAndGet();
                return null;
            }
            if (head.compareAndSet(r, r.next.get())) {
                if (r == stub) {
                    stub.next.set(null);
                    addNode(stub);
                } else {
                    return r.ref;
                }
            }
        }
    }

    private void addNode(Node<T> n) {
        Node<T> t;
        while (true) {
            t = tail.get();
            if (tail.compareAndSet(t, n)) {
                break;
            }
        }
        if (t.next.compareAndSet(null, n)) {
            return;
        }
        throw new IllegalStateException("bad tail next");
    }
}
