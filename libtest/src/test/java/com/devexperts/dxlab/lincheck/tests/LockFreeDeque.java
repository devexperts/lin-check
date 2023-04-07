/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.tests;

import java.util.concurrent.atomic.AtomicMarkableReference;

// Double-linked list by Sundell
public class LockFreeDeque<T> {

    private final Node head;
    private final Node tail;

    public LockFreeDeque() {
        this.head = new Node(null, null, null);
        this.tail = new Node(null, null, null);

        head.next.compareAndSet(null, tail, false, false);
        tail.prev.compareAndSet(null, head, false, false);
    }

    private class Node {
        AtomicMarkableReference<Node> prev, next;
        T val;

        Node(T val, Node prev, Node next) {
            this.val = val;
            this.prev = new AtomicMarkableReference<>(prev, false);
            this.next = new AtomicMarkableReference<>(next, false);
        }
    }

    public void pushRight(T x) {
        Node node = new Node(x, null, null);
        Node next = tail;
        Node prev = next.prev.getReference();
        while (true) {
            if (!prev.next.compareAndSet(next, next, false, false)) {
                // concurrent push inserted -> get new prev
                prev = helpInsert(prev, next, "concurrentPushRight");
                continue;
            }
            // 0 push step
            node.prev = new AtomicMarkableReference<>(prev, false);
            node.next = new AtomicMarkableReference<>(next, false);
            // 1 push step
            if (prev.next.compareAndSet(next, node, false, false)) {
                break;
            }
        }
        // 2 push step
        pushCommon(node, next);
    }

    public void pushLeft(T x) {
        Node node = new Node(x, null, null);
        Node prev = head;
        Node next = prev.next.getReference();
        while (true) {
            if (!prev.next.compareAndSet(next, next, false, false)) {
                next = prev.next.getReference();
                continue;
            }
            node.prev = new AtomicMarkableReference<>(prev, false);
            node.next = new AtomicMarkableReference<>(next, false);

            if (prev.next.compareAndSet(next, node, false, false)) {
                break;
            }
        }
        pushCommon(node, next);
    }

    private void pushCommon(Node node, Node next) {
        while (true) {
            AtomicMarkableReference<Node> link1 = next.prev;
            if (link1.isMarked() || !node.next.compareAndSet(next, next, false, false)) {
                break;
            }
            if (next.prev.compareAndSet(link1.getReference(), node, false, false)) {
                if (node.prev.isMarked()) {
                    helpInsert(node, next, "pushCommon");
                }
                break;
            }
        }
    }

    public T popLeft() {
        T value;
        Node prev = head;
        while (true) {
            Node node = prev.next.getReference();
            // deque is empty
            if (node == tail) {
                return null;
            }
            boolean[] removed = new boolean[1];
            Node nodeNext = node.next.get(removed);
            // concurrent pop started to delete this node, help it, then continue
            if (removed[0]) {
                helpDelete(node, "help concurrent");
                continue;
            }
            // 1 pop step
            if (node.next.compareAndSet(nodeNext, nodeNext, false, true)) {
                // 2, 3 step
                helpDelete(node, "1st step");
                Node next = node.next.getReference();
                // 4 step
                helpInsert(prev, next, "popLeft New");
                value = node.val;
                return value;
            }
        }
    }

    public T popRight() {
        Node next = tail;
        Node node = next.prev.getReference();
        while (true) {
            if (!node.next.compareAndSet(next, next, false, false)) {
                node = helpInsert(node, next, "popRight");
                continue;
            }
            if (node == head) {
                return null;
            }
            if (node.next.compareAndSet(next, next, false, true)) {
                helpDelete(node, "");
                Node prev = node.prev.getReference();
                helpInsert(prev, next, "popRight");
                return node.val;
            }
        }
    }

    /**
     * Correct node.prev to the closest previous node
     * helpInsert is very weak - does not reset node.prev to the actual prev.next
     * but just tries to set node.prev to the given suggestion of a prev node
     * (for 2 push step, 4 pop step)
     */
    private Node helpInsert(Node prev, Node node, String method) {
        // last = is the last node : last.next == prev and it is not marked as removed
        Node last = null;
        while (true) {

            boolean[] removed = new boolean[1];
            Node prevNext = prev.next.get(removed);

            if (removed[0]) {
                if (last != null) {
                    markPrev(prev);
                    Node next2 = prev.next.getReference();
                    boolean b1 = last.next.compareAndSet(prev, next2, false, false);
                    prev = last;
                    last = null;
                } else {
                    prevNext = prev.prev.getReference();
                    prev = prevNext;
                }
                continue;
            }

            Node nodePrev = node.prev.get(removed);
            if (removed[0]) {
                break;
            }

            // prev is not the previous node of node
            if (prevNext != node) {
                last = prev;
                prev = prevNext;
                continue;
            }

            if (nodePrev == prev) break;
            if (prev.next.getReference() == node && node.prev.compareAndSet(nodePrev, prev, false, false)) {
                if (prev.prev.isMarked()) {
                    continue;
                }
                break;
            }
        }
        return prev;
    }

    // 2 and 3 pop steps
    private void helpDelete(Node node, String place) {
        markPrev(node);
        Node prev = node.prev.getReference();
        Node next = node.next.getReference();
        Node last = null;
        while (true) {
            if (prev == next) break;
            if (next.next.isMarked()) {
                markPrev(next);
                next = next.next.getReference();
                continue;
            }
            boolean removed[] = new boolean[1];
            Node prevNext = prev.next.get(removed);
            if (removed[0]) {
                if (last != null) {
                    markPrev(prev);
                    Node next2 = prev.next.getReference();
                    last.next.compareAndSet(prev, next2, false, false);
                    prev = last;
                    last = null;
                } else {
                    prevNext = prev.prev.getReference();
                    prev = prevNext;
                    assert (prev != null);
                }
                continue;
            }
            if (prevNext != node) {
                last = prev;
                prev = prevNext;
                continue;
            }
            if (prev.next.compareAndSet(node, next, false, false)) {
                break;
            }
        }
    }

    private void markPrev(Node node) {
        while (true) {
            AtomicMarkableReference<Node> link1 = node.prev;
            if (link1.isMarked() || node.prev.compareAndSet(link1.getReference(), link1.getReference(), false, true)) {
                break;
            }
        }
    }

}
