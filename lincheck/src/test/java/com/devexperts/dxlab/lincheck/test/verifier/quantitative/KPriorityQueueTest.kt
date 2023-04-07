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
import com.devexperts.dxlab.lincheck.verifier.quantitative.*
import com.devexperts.dxlab.lincheck.verifier.quantitative.PathCostFunction.*
import org.junit.Test

@StressCTest(threads = 2, actorsPerThread = 10, actorsBefore = 5, actorsAfter = 5,  invocationsPerIteration = 10, iterations = 1000, verifier = QuantitativeRelaxationVerifier::class)
@QuantitativeRelaxationVerifierConf(
        factor = 2,
        pathCostFunc = PHI_INTERVAL_RESTRICTED_MAX,
        costCounter = KPriorityQueueTest.CostCounter::class
)
@Param(name = "push-value", gen = IntGen::class, conf = "1:20")
class KPriorityQueueTest {
    private val pq = KPriorityQueueSimulation(2)

    @Operation(params = ["push-value"])
    fun push(x: Int) = pq.push(x)

    @QuantitativeRelaxed
    @Operation
    fun poll(): Int? = pq.poll()

    @Test
    fun test() = LinChecker.check(KPriorityQueueTest::class.java)

    data class CostCounter @JvmOverloads constructor(
            private val k: Int,
            private val pq: List<Int> = emptyList()
    ) {
        fun push(value: Int, result: Result): CostCounter {
            check(result.type == Result.Type.VOID)
            val pqNew = ArrayList(pq)
            pqNew.add(0, value)
            pqNew.sort()
            return CostCounter(k, pqNew)
        }

        fun poll(result: Result): List<CostWithNextCostCounter<CostCounter>> {
            if (pq.isEmpty()) {
                return if (result.value == null) listOf(CostWithNextCostCounter(this, 0, false))
                       else emptyList()
            } else {
                val edges: MutableList<CostWithNextCostCounter<CostCounter>> = mutableListOf()
                val predicate = result.value != pq[0]
                val it = pq.iterator()
                var cost = 0
                while (it.hasNext() && cost < k) {
                    val value = it.next()
                    if (value == result.value) {
                        val pqNew = ArrayList(pq)
                        pqNew.remove(value)
                        edges.add(CostWithNextCostCounter(CostCounter(k, pqNew), cost, predicate))
                    }
                    cost++
                }
                return edges
            }
        }
    }
}
