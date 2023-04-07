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
import com.devexperts.dxlab.lincheck.annotations.Param
import com.devexperts.dxlab.lincheck.paramgen.IntGen
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTest
import com.devexperts.dxlab.lincheck.verifier.linearizability.LinearizabilityVerifier
import com.devexperts.dxlab.lincheck.verifier.quasi.QuasiLinearizabilityVerifier
import com.devexperts.dxlab.lincheck.verifier.quasi.QuasiLinearizabilityVerifierConf
import org.junit.Ignore
import org.junit.Test
import java.util.*

private const val K = 3

@Ignore
@StressCTest(verifier = QuasiLinearizabilityVerifier::class, iterations = 10, invocationsPerIteration = 2000, actorsBefore = 30, actorsAfter = 10, actorsPerThread = 5, threads = 2)
@QuasiLinearizabilityVerifierConf(factor = K, sequentialImplementation = KRelaxedPopStackTest.StackImpl::class)
class KRelaxedPopStackTest {
    private val s = KRelaxedPopStack<Int>(3)

    @Operation
    fun push(item: Int) = s.push(item)

    /**
     * Several fictitious push operations are added to increase the number of push operations in generated scenario
     * -> increase the stack size for k-relaxed pop() operation
     */
    @Operation
    fun push1(item: Int) = s.push(item)

    @Operation
    fun push2(item: Int) = s.push(item)

    @Operation
    fun push3(item: Int) = s.push(item)

    @Operation
    fun push4(item: Int) = s.push(item)

    @Operation
    fun pop(): Int? = s.pop()

    @Test
    fun test() = LinChecker.check(KRelaxedPopStackTest::class.java)

    data class StackImpl (val s: LinkedList<Int> = LinkedList()) {
        fun push(item: Int) = s.push(item)
        fun push1(item: Int) = s.push(item)
        fun push2(item: Int) = s.push(item)
        fun push3(item: Int) = s.push(item)
        fun push4(item: Int) = s.push(item)
        fun pop(): Int? = if (s.isEmpty()) null else s.pop()
    }
}
