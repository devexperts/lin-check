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
import com.devexperts.dxlab.lincheck.annotations.OpGroupConfig;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.paramgen.IntGen;
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTest;
import org.junit.Test;

@OpGroupConfig(name = "push_remove", nonParallel = true)
@StressCTest
public class ResultAsParameterTest {
    private Stack stack = new Stack();
    private Node lastPushNode = null;

    @Operation(runOnce = true, group = "push_remove")
    public synchronized void push(@Param(gen = IntGen.class, conf = "1:1") int x) {
        lastPushNode = stack.push(x);
    }

    @Operation
    public synchronized int pop() {
        return stack.pop();
    }

    @Operation(runOnce = true, group = "push_remove")
    public synchronized boolean remove() {
        Node node = lastPushNode; // read under potential race, unsafe
        if (node == null)
            return false;
        return stack.remove(node);
    }

    @Test
    public void test() {
        LinChecker.check(ResultAsParameterTest.class);
    }
}

class Stack {
    private final Node NIL = new Node(null, 0);
    private Node head = NIL;

    synchronized Node push(int x) { // x >= 0
        Node node = new Node(head, x);
        head.prev = node;
        head = node;
        return node;
    }

    synchronized int pop() {
        if (head == NIL) {
            return -1;
        }
        int res = head.val;
        head = head.next;
        return res;
    }

    synchronized boolean remove(Node node) {
        if (node == head) {
            head = head.next;
            return true;
        } else if (node.prev != null && node.prev.next == node) {
            node.prev.next = node.next;
            return true;
        }
        return false;
    }
}

class Node {
    Node next;
    Node prev;
    int val;

    public Node(Node next, int val) {
        this.next = next;
        this.val = val;
    }
}
