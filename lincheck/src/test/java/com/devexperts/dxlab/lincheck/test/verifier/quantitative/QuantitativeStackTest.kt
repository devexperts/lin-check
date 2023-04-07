/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.devexperts.dxlab.lincheck.test.verifier.quantitative

import com.devexperts.dxlab.lincheck.LinChecker
import com.devexperts.dxlab.lincheck.Result
import com.devexperts.dxlab.lincheck.annotations.Operation
import com.devexperts.dxlab.lincheck.annotations.Param
import com.devexperts.dxlab.lincheck.paramgen.IntGen
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTest
import com.devexperts.dxlab.lincheck.test.verifier.quasi.KRelaxedPopStack
import com.devexperts.dxlab.lincheck.verifier.quantitative.*
import org.junit.Test

@StressCTest(threads = 2, actorsPerThread = 10, actorsBefore = 5, actorsAfter = 5,  invocationsPerIteration = 1000, iterations = 10, verifier = QuantitativeRelaxationVerifier::class)
@QuantitativeRelaxationVerifierConf(
        factor = 3,
        pathCostFunc = PathCostFunction.MAX,
        costCounter = QuantitativeStackTest.CostCounter::class
)
@Param(name = "push", gen = IntGen::class, conf = "1:20")
class QuantitativeStackTest {
    private val s = KRelaxedPopStack<Int>(2)

    @Operation(params = ["push"])
    fun push(x: Int) = s.push(x)

    @Operation(params = ["push"])
    fun push1(x: Int) = s.push1(x)

    @Operation(params = ["push"])
    fun push2(x: Int) = s.push2(x)

    @QuantitativeRelaxed
    @Operation
    fun pop(): Int? = s.pop()

    @Test
    fun test() = LinChecker.check(QuantitativeStackTest::class.java)

    // Should have '(k: Int)' constructor
    data class CostCounter @JvmOverloads constructor(
            private val k: Int,
            private val s: List<Int> = emptyList()
    ) {
        fun push(value: Int, result: Result): CostCounter {
            check(result.type == Result.Type.VOID)
            val sNew = ArrayList(s)
            sNew.add(0, value)
            return CostCounter(k, sNew)
        }

        fun push1(value: Int, result: Result): CostCounter {
            check(result.type == Result.Type.VOID)
            val sNew = ArrayList(s)
            sNew.add(0, value)
            return CostCounter(k, sNew)
        }

        fun push2(value: Int, result: Result): CostCounter {
            check(result.type == Result.Type.VOID)
            val sNew = ArrayList(s)
            sNew.add(0, value)
            return CostCounter(k, sNew)
        }

        fun pop(result: Result): List<CostWithNextCostCounter<CostCounter>> {
            if (result.value == null) {
                return if (s.isEmpty())
                    listOf(CostWithNextCostCounter(this, 0))
                else emptyList()
            }
            return (0..(k - 1).coerceAtMost(s.size - 1))
                    .filter { i -> s[i] == result.value }
                    .map { i ->
                        val sNew = ArrayList(s)
                        sNew.removeAt(i)
                        CostWithNextCostCounter(CostCounter(k, sNew), i)
                    }
        }
    }
}
