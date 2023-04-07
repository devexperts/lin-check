/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.test.verifier.quasi

import com.devexperts.dxlab.lincheck.LinChecker
import com.devexperts.dxlab.lincheck.annotations.Operation
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTest
import com.devexperts.dxlab.lincheck.verifier.quasi.QuasiLinearizabilityVerifier
import com.devexperts.dxlab.lincheck.verifier.quasi.QuasiLinearizabilityVerifierConf
import org.junit.Ignore
import org.junit.Test
import java.util.*

private const val K = 2

@Ignore
@StressCTest(verifier = QuasiLinearizabilityVerifier::class, iterations = 10, invocationsPerIteration = 10, actorsBefore = 5, actorsAfter = 5, actorsPerThread = 10, threads = 2)
@QuasiLinearizabilityVerifierConf(factor = K, sequentialImplementation = SegmentQueueTest.SeqImpl::class)
class SegmentQueueTest {
    private val q = SegmentQueue<Int>(2)

    @Operation
    fun enqueue(item: Int) = q.enqueue(item)

    @Operation
    fun dequeue(): Int? = q.dequeue()

    @Test
    fun test() = LinChecker.check(SegmentQueueTest::class.java)

    data class SeqImpl @JvmOverloads constructor(val q: Queue<Int> = ArrayDeque<Int>()) {
        fun enqueue(item: Int) = q.offer(item)
        fun dequeue(): Int? = q.poll()
    }
}
