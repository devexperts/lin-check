/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.verifier.linearizability

import com.devexperts.dxlab.lincheck.execution.ExecutionResult
import com.devexperts.dxlab.lincheck.execution.ExecutionScenario
import com.devexperts.dxlab.lincheck.verifier.*

/**
 * This verifier checks that the specified results could be happen in linearizable execution,
 * for what it tries to find a possible linear execution which transitions does not violate both
 * regular LTS (see [LTS] and [RegularLTS]) transitions and the happens-before order. Essentially,
 * it just tries to execute the next actor in each thread and goes deeper until all actors are executed.
 *
 * This verifier is based on [AbstractLTSVerifier] and caches the already processed results
 * for performance improvement (see [CachedVerifier]).
 */
class LinearizabilityVerifier(scenario: ExecutionScenario, testClass : Class<*>) : AbstractLTSVerifier<RegularLTS.State>(scenario, testClass) {
    override fun createInitialContext(results: ExecutionResult): LTSContext<RegularLTS.State>
            = LinearizabilityContext(scenario, RegularLTS(testClass).initialState, results)
}

/**
 * Next possible states are determined lazily by trying to execute next actor in order for every thread
 *
 * Current state of scenario execution is represented with the number of actors executed in every thread
 */
private class LinearizabilityContext(scenario: ExecutionScenario,
                                     state: RegularLTS.State,
                                     executed: IntArray,
                                     val results: ExecutionResult
) : LTSContext<RegularLTS.State>(scenario, state, executed) {

    constructor(scenario: ExecutionScenario, state: RegularLTS.State, results: ExecutionResult)
            : this(scenario, state, IntArray(scenario.threads + 2), results)

    override fun nextContexts(threadId: Int): List<LinearizabilityContext> {
        // Check if there are unprocessed actors in the specified thread
        if (isCompleted(threadId)) return emptyList()
        // Check whether an actor from the specified thread can be executed
        // in accordance with the rule that all actors from init part should be
        // executed at first, after that all actors from parallel part, and
        // all actors from post part should be executed at last.
        val legal = when (threadId) {
            0 -> true // INIT: we already checked that there is an unprocessed actor
            in 1 .. scenario.threads -> initCompleted // PARALLEL
            else -> initCompleted && parallelCompleted // POST
        }
        if (!legal) return emptyList()
        // Check whether the transition is possible in LTS
        val i = executed[threadId]
        val nextState = state.next(scenario[threadId][i], results[threadId][i]) ?: return emptyList()
        // The transition is possible, create a new context
        val nextExecuted = executed.copyOf()
        nextExecuted[threadId]++
        return listOf(LinearizabilityContext(scenario, nextState, nextExecuted, results))
    }
}
