/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package com.devexperts.dxlab.lincheck.verifier.quantitative

import com.devexperts.dxlab.lincheck.Actor
import com.devexperts.dxlab.lincheck.execution.ExecutionResult
import com.devexperts.dxlab.lincheck.execution.ExecutionScenario
import com.devexperts.dxlab.lincheck.verifier.AbstractLTSVerifier
import com.devexperts.dxlab.lincheck.verifier.LTSContext
import com.devexperts.dxlab.lincheck.verifier.get


/**
 * This verifier checks for quantitative relaxation contracts, which are introduced
 * in the "Quantitative relaxation of concurrent data structures" paper by Henzinger et al.
 *
 * Requires [QuantitativeRelaxationVerifierConf] annotation on the testing class.
 */
class QuantitativeRelaxationVerifier(scenario: ExecutionScenario, testClass: Class<*>) : AbstractLTSVerifier<ExtendedLTS.State>(scenario, testClass) {
    private val relaxationFactor: Int
    private val pathCostFunc: PathCostFunction
    private val lts: ExtendedLTS

    init {
        val conf = testClass.getAnnotation(QuantitativeRelaxationVerifierConf::class.java)
        requireNotNull(conf) { "No configuration for QuasiLinearizabilityVerifier found" }
        relaxationFactor = conf.factor
        pathCostFunc = conf.pathCostFunc
        lts = ExtendedLTS(conf.costCounter.java, relaxationFactor)
    }

    override fun createInitialContext(results: ExecutionResult): LTSContext<ExtendedLTS.State> =
            QuantitativeRelaxationContext(scenario, lts.initialState, results)

    private inner class QuantitativeRelaxationContext(
            scenario: ExecutionScenario,
            state: ExtendedLTS.State,
            executed: IntArray,
            val results: ExecutionResult,
            val iterativePathCostFunctionCounter: IterativePathCostFunctionCounter
    ) : LTSContext<ExtendedLTS.State>(scenario, state, executed) {

        constructor(scenario: ExecutionScenario, state: ExtendedLTS.State, results: ExecutionResult) :
                this(scenario, state, IntArray(scenario.threads + 2), results, pathCostFunc.createIterativePathCostFunctionCounter(relaxationFactor))

        override fun nextContexts(threadId: Int): List<QuantitativeRelaxationContext> {
            // Check if there are unprocessed actors in the specified thread
            if (isCompleted(threadId)) return emptyList()
            // Check whether an actor from the specified thread can be executed
            // in accordance with the rule that all actors from init part should be
            // executed at first, after that all actors from parallel part, and
            // all actors from post part should be executed at last.
            val legal = when (threadId) {
                0 -> true // INIT: we already checked that there is an unprocessed actor
                in 1..scenario.threads -> initCompleted // PARALLEL
                else -> initCompleted && parallelCompleted // POST
            }
            if (!legal) return emptyList()
            // Check whether the transition is possible in LTS.
            val i = executed[threadId]
            val actor = scenario[threadId][i]
            val result = results[threadId][i]
            if (actor.isRelaxed) {
                // Get list of possible transitions with their penalty costs.
                // Create a new context for each of them with an updated path cost function counters.
                val costWithNextCostCounterList = state.nextRelaxed(actor, result)
                return costWithNextCostCounterList.mapNotNull {
                    val nextPathCostFuncCounter = iterativePathCostFunctionCounter.next(it) ?: return@mapNotNull null
                    nextContext(threadId, lts.getStateForCostCounter(it.nextCostCounter), nextPathCostFuncCounter)
                }
            } else {
                // Get next state similarly to `LinearizabilityVerifier` and
                // create a new context with it and the same path cost function counter.
                val nextState = state.nextRegular(actor, result) ?: return emptyList()
                return listOf(nextContext(threadId, nextState, iterativePathCostFunctionCounter))
            }
        }

        private fun nextContext(threadId: Int, nextState: ExtendedLTS.State, nextIterativePathCostFuncCounter: IterativePathCostFunctionCounter): QuantitativeRelaxationContext {
            val nextExecuted = executed.copyOf()
            nextExecuted[threadId]++
            return QuantitativeRelaxationContext(scenario, nextState, nextExecuted, results, nextIterativePathCostFuncCounter)
        }

        private val Actor.isRelaxed get() = method.isAnnotationPresent(QuantitativeRelaxed::class.java)
    }
}
