/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package psy.lob.saw.queues.benchmarks.jmh;

import java.util.Queue;

import psy.lob.saw.queues.common.SPSCQueueFactory;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public abstract class QueueBenchmark {
	@Param(value={"11","12","21","22","23","24","25","31","32","33","41","42"})
	protected int queueType;
	@Param(value={"17"})
	protected int queueScale;
    protected static Queue<Integer> q;
    
    @Setup(Level.Trial)
    public void createQueue()
    {
    	q = SPSCQueueFactory.createQueue(queueType, queueScale);
    }
}
