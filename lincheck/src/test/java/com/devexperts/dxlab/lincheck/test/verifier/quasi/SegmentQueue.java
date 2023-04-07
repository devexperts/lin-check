/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.test.verifier.quasi;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

/**
 * k-quasi linearizable queue, that scatters the contention both for @dequeue and @enqueue operations
 *
 * SegmentedQueue maintains a linked list of segments, each segment is an array of nodes in the size of QF
 * Each enqueuer(dequeuer) iterates over the last(first) segment in the linked list, attempting to find an empty(non-empty) cell
 */
public class SegmentQueue<T> {

    private int k;
    private final AtomicReference<KSegment> head = new AtomicReference<>();
    private final AtomicReference<KSegment> tail = new AtomicReference<>();

    private final Random random = new Random();

    public SegmentQueue(int k) {
        this.k = k;
        KSegment empty = new KSegment(null);
        head.set(empty);
        tail.set(empty);
    }

    private class KSegment {
        final AtomicReference<T>[] segment;
        AtomicReference<KSegment> next = new AtomicReference<>();

        private KSegment(KSegment next) {
            this.segment = new AtomicReference[k + 1];
            for (int i = 0; i <= k; i++) {
                segment[i] = new AtomicReference<>(null);
            }
            this.next.set(next);
        }
    }

    public void enqueue(T x) {
        while (true) {
            KSegment curTail = tail.get();
            // iterate through the tail segment to find an empty place
            int startIndex = Math.abs(ThreadLocalRandom.current().nextInt()) % k;
            for (int i = 0; i <= k; i++) {
                if (curTail.segment[(i + startIndex) % k].compareAndSet(null, x)) {
                    return;
                }
            }
            // no empty place was found -> add new segment
            if (curTail == tail.get()) {
                KSegment newTail = new KSegment(null);
                if (curTail.next.compareAndSet(null, newTail)) {
                    tail.compareAndSet(curTail, newTail);
                } else {
                    tail.compareAndSet(curTail, curTail.next.get());
                }
            }
        }
    }

    public T dequeue() {
        while (true) {
            KSegment curHead = head.get();
            //iterate through the head segment to find a non-null element
            int startIndex = Math.abs(ThreadLocalRandom.current().nextInt()) % k;
            for (int i = 0; i <= k; i++) {
                T old_value = curHead.segment[(i + startIndex) % k].get();
                if (old_value == null) {
                    continue;
                }
                if (curHead.segment[(i + startIndex) % k].compareAndSet(old_value, null)) {
                    return old_value;
                }
            }
            // all elements were dequeued -> we can remove this segment, if it's not a single one
            KSegment curTail = tail.get();
            if (curHead != curTail) {
                head.compareAndSet(curHead, curHead.next.get());
            } else {
                if (curTail.next.get() != null) {
                    tail.compareAndSet(curTail, curTail.next.get());
                } else {
                    return null;
                }
            }
        }
    }
}
