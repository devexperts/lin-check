/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.test.runner;

import com.devexperts.dxlab.lincheck.Actor;
import com.devexperts.dxlab.lincheck.Result;
import com.devexperts.dxlab.lincheck.execution.ExecutionResult;
import com.devexperts.dxlab.lincheck.runner.Runner;
import com.devexperts.dxlab.lincheck.runner.TestThreadExecution;
import com.devexperts.dxlab.lincheck.runner.TestThreadExecutionGenerator;
import com.devexperts.dxlab.lincheck.strategy.Strategy;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Queue;

public class TestThreadExecutionHelperTest {
    private Runner runner;

    @Before
    public void setUp() {
        Strategy mockStrategy = new Strategy(null, null, null) {
            @Override
            public void run(){
                throw new UnsupportedOperationException();
            }
        };
        runner = new Runner(null, mockStrategy, ArrayDeque.class) {
            @Override
            public ExecutionResult run() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Test
    public void testBase() throws Exception {
        TestThreadExecution ex = TestThreadExecutionGenerator.create(runner, 0,
            Arrays.asList(
                new Actor(Queue.class.getMethod("add", Object.class), Arrays.asList(1), Collections.emptyList()),
                new Actor(Queue.class.getMethod("add", Object.class), Arrays.asList(2), Collections.emptyList()),
                new Actor(Queue.class.getMethod("remove"), Collections.emptyList(), Collections.emptyList()),
                new Actor(Queue.class.getMethod("element"), Collections.emptyList(), Collections.emptyList()),
                new Actor(Queue.class.getMethod("peek"), Collections.emptyList(), Collections.emptyList())
            ), false);
        ex.testInstance = new ArrayDeque<>();
        Assert.assertArrayEquals(new Result[] {
            Result.createValueResult(true),
            Result.createValueResult(true),
            Result.createValueResult(1),
            Result.createValueResult(2),
            Result.createValueResult(2)
        }, ex.call());
    }

    @Test(expected = NoSuchElementException.class)
    public void testGlobalException() throws Exception {
        TestThreadExecution ex = TestThreadExecutionGenerator.create(runner, 0,
            Arrays.asList(
                new Actor(Queue.class.getMethod("add", Object.class), Arrays.asList(1), Collections.emptyList()),
                new Actor(Queue.class.getMethod("remove"), Collections.emptyList(), Collections.emptyList()),
                new Actor(Queue.class.getMethod("remove"), Collections.emptyList(), Collections.emptyList()),
                new Actor(Queue.class.getMethod("add", Object.class), Arrays.asList(2), Collections.emptyList())
            ), false);
        ex.testInstance = new ArrayDeque<>();
        ex.call();
    }

    @Test
    public void testActorExceptionHandling() throws Exception {
        TestThreadExecution ex = TestThreadExecutionGenerator.create(runner, 0,
            Arrays.asList(
                new Actor(ArrayDeque.class.getMethod("addLast", Object.class), Arrays.asList(1), Collections.emptyList()),
                new Actor(Queue.class.getMethod("remove"), Collections.emptyList(), Collections.emptyList()),
                new Actor(Queue.class.getMethod("remove"), Collections.emptyList(), Arrays.asList(NoSuchElementException.class)),
                new Actor(Queue.class.getMethod("remove"), Collections.emptyList(), Arrays.asList(Exception.class, NoSuchElementException.class))
            ), false);
        ex.testInstance = new ArrayDeque<>();
        Assert.assertArrayEquals(new Result[] {
            Result.createVoidResult(),
            Result.createValueResult(1),
            Result.createExceptionResult(NoSuchElementException.class),
            Result.createExceptionResult(NoSuchElementException.class)
        }, ex.call());
    }

    @Test
    public void testWaits() throws Exception {
        TestThreadExecution ex = TestThreadExecutionGenerator.create(runner, 0,
            Arrays.asList(
                new Actor(Queue.class.getMethod("add", Object.class), Arrays.asList(1), Collections.emptyList()),
                new Actor(Queue.class.getMethod("remove"), Collections.emptyList(), Collections.emptyList()),
                new Actor(Queue.class.getMethod("remove"), Collections.emptyList(), Arrays.asList(NoSuchElementException.class))
            ), true);
        ex.testInstance = new ArrayDeque<>();
        ex.waits = new int[] {15, 100};
        Assert.assertArrayEquals(new Result[] {
            Result.createValueResult(true),
            Result.createValueResult(1),
            Result.createExceptionResult(NoSuchElementException.class)
        }, ex.call());
    }
}
