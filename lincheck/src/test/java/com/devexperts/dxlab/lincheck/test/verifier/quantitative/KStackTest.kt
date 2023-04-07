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
import com.devexperts.dxlab.lincheck.Result.Type.VOID
import com.devexperts.dxlab.lincheck.annotations.Operation
import com.devexperts.dxlab.lincheck.annotations.Param
import com.devexperts.dxlab.lincheck.paramgen.IntGen
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTest
import com.devexperts.dxlab.lincheck.verifier.quantitative.CostWithNextCostCounter
import com.devexperts.dxlab.lincheck.verifier.quantitative.PathCostFunction.MAX
import com.devexperts.dxlab.lincheck.verifier.quantitative.QuantitativeRelaxationVerifier
import com.devexperts.dxlab.lincheck.verifier.quantitative.QuantitativeRelaxationVerifierConf
import com.devexperts.dxlab.lincheck.verifier.quantitative.QuantitativeRelaxed
import org.junit.Test
import java.lang.AssertionError

private const val K = 2

@StressCTest(verifier = QuantitativeRelaxationVerifier::class)
@QuantitativeRelaxationVerifierConf(factor = K, pathCostFunc = MAX, costCounter = KStackRelaxedPushTest.CostCounter::class)
class KStackRelaxedPushTest {
    private val s = KStackSimulation<Int>(k = K)

    @QuantitativeRelaxed
    @Operation
    fun put(@Param(gen = IntGen::class) value: Int) = s.pushRelaxed(value)

    @Operation
    fun popOrNull() = s.popOrNull()

    @Test
    fun test() = LinChecker.check(KStackRelaxedPushTest::class.java)

    // Should have '(k: Int)' constructor
    data class CostCounter @JvmOverloads constructor(private val k: Int, private val s: List<Int> = emptyList()) {
        fun put(value: Int, result: Result): List<CostWithNextCostCounter<CostCounter>> {
            return (0..(k - 1).coerceAtMost(s.size)).map { i ->
                val sNew = ArrayList(s)
                sNew.add(i, value)
                CostWithNextCostCounter(CostCounter(k, sNew), i, i != 0)
            }
        }

        fun popOrNull(result: Result): CostCounter? {
            if (s.isEmpty()) {
                return if (result.value == null) CostCounter(k) else null
            }
            if (s[0] != result.value) return null
            val sNew = ArrayList(s)
            sNew.removeAt(0)
            return CostCounter(k, sNew)
        }
    }
}

@StressCTest(actorsPerThread = 10, verifier = QuantitativeRelaxationVerifier::class)
@QuantitativeRelaxationVerifierConf(factor = K, pathCostFunc = MAX, costCounter = KStackRelaxedPopIncorrectTest.CostCounter::class)
class KStackRelaxedPopIncorrectTest {
    private val s = KStackSimulation<Int>(k = K + 1)

    @Operation
    fun put(@Param(gen = IntGen::class) value: Int) = s.push(value)

    @QuantitativeRelaxed
    @Operation
    fun popOrNull() = s.popOrNullRelaxed()

    @Test(expected = AssertionError::class)
    fun test() = LinChecker.check(KStackRelaxedPopIncorrectTest::class.java)

    // Should have '(k: Int)' constructor
    data class CostCounter @JvmOverloads constructor(private val k: Int, private val s: List<Int> = emptyList()) {
        fun put(value: Int, result: Result): CostCounter {
            check(result.type == VOID)
            val sNew = ArrayList(s)
            sNew.add(0, value)
            return CostCounter(k, sNew)
        }

        fun popOrNull(result: Result): List<CostWithNextCostCounter<CostCounter>> {
            if (result.value == null) {
                return if (s.isEmpty())
                    listOf(CostWithNextCostCounter(this, 0, false))
                else emptyList()
            }
            return (0..(k - 1).coerceAtMost(s.size - 1)).filter { i -> s[i] == result.value }.map { i ->
                val sNew = ArrayList(s)
                sNew.removeAt(i)
                CostWithNextCostCounter(CostCounter(k, sNew), i)
            }
        }
    }
}

@StressCTest(verifier = QuantitativeRelaxationVerifier::class)
@QuantitativeRelaxationVerifierConf(factor = K, pathCostFunc = MAX, costCounter = KStackRelaxedPushAndPopTest.CostCounter::class)
class KStackRelaxedPushAndPopTest {
    private val s = KStackSimulation<Int>(k = K)

    @QuantitativeRelaxed
    @Operation
    fun put(@Param(gen = IntGen::class) value: Int) = s.pushRelaxed(value)

    @QuantitativeRelaxed
    @Operation
    fun popOrNull() = s.popOrNullRelaxed()

    @Test
    fun test() = LinChecker.check(KStackRelaxedPushAndPopTest::class.java)

    // Should have '(k: Int)' constructor
    data class CostCounter @JvmOverloads constructor(private val k: Int, private val s: List<Int> = emptyList()) {
        fun put(value: Int, result: Result): List<CostWithNextCostCounter<CostCounter>> {
            return (0..(k - 1).coerceAtMost(s.size)).map { i ->
                val sNew = ArrayList(s)
                sNew.add(i, value)
                CostWithNextCostCounter(CostCounter(k, sNew), i, i != 0)
            }
        }

        fun popOrNull(result: Result): List<CostWithNextCostCounter<CostCounter>> {
            if (result.value == null) {
                return if (s.isEmpty())
                    listOf(CostWithNextCostCounter(this, 0, false))
                else emptyList()
            }
            return (0..(k - 1).coerceAtMost(s.size - 1)).filter { i -> s[i] == result.value }.map { i ->
                val sNew = ArrayList(s)
                sNew.removeAt(i)
                CostWithNextCostCounter(CostCounter(k, sNew), i)
            }
        }
    }
}
