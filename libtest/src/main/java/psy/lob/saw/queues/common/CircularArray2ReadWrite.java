/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package psy.lob.saw.queues.common;

import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.GenerateMicroBenchmark;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

@OutputTimeUnit(TimeUnit.MICROSECONDS)
@BenchmarkMode({ Mode.Throughput })
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Thread)
public class CircularArray2ReadWrite {
	public static final int CAPACITY = 1 << 15;
	public static final Integer TOKEN = 1;

	private CircularArrayQueue2<Integer> caq = new CircularArrayQueue2<Integer>(CAPACITY) {
		@Override
		public boolean offer(Integer e) {
			return false;
		}

		@Override
		public Integer poll() {
			return null;
		}

		@Override
		public Integer peek() {
			return null;
		}

		@Override
		public Iterator<Integer> iterator() {
			return null;
		}

		@Override
		public int size() {
			return 0;
		}
	};

	long index;

	@GenerateMicroBenchmark
	public void offer() {
		int offset = caq.calcOffset(index++);
		caq.spElement(offset, TOKEN);
	}

	@GenerateMicroBenchmark
	public void poll() {
		int offset = caq.calcOffset(index++);
		if (caq.lpElement(offset) != null) {
			index--;
		}
	}
}
