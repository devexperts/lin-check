/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.verifier

import com.devexperts.dxlab.lincheck.Actor
import com.devexperts.dxlab.lincheck.Result
import com.devexperts.dxlab.lincheck.execution.ExecutionResult
import com.devexperts.dxlab.lincheck.execution.ExecutionScenario
import com.devexperts.dxlab.lincheck.verifier.quantitative.ExtendedLTS


/**
 * An abstraction for verifiers which use the labeled transition system (LTS) under the hood.
 * The main idea of such verifiers is finding a path in LTS, which starts from the initial
 * LTS state (see [LTS.initialState]) and goes through all actors with the specified results.
 * To determine which transitions are possible from the current state, we store related
 * to the current path prefix information in the special [LTSContext], which determines
 * the next possible transitions using [LTSContext.nextContexts] function. This verifier
 * uses depth-first search to find a proper path.
 */
abstract class AbstractLTSVerifier<STATE>(val scenario: ExecutionScenario, val testClass: Class<*>) : CachedVerifier() {
    abstract fun createInitialContext(results: ExecutionResult): LTSContext<STATE>

    override fun verifyResultsImpl(results: ExecutionResult): Boolean {
        return verify(createInitialContext(results))
    }

    private fun verify(context: LTSContext<STATE>): Boolean {
        // Check if a possible path is found.
        if (context.completed) return true
        // Traverse through next possible transitions using depth-first search (DFS). Note that
        // initial and post parts are represented as threads with ids `0` and `threads + 1` respectively.
        for (threadId in 0..scenario.threads + 1) {
            for (c in context.nextContexts(threadId)) {
                if (verify(c)) return true
            }
        }
        return false
    }
}


/**
 * Common interface for different labeled transition systems, which several correctness formalisms use.
 * Lin-Check widely uses LTS-based formalisms for verification, see [Verifier] implementations as examples.
 * Essentially, LTS provides an information of the possibility to do a transition from one state to another
 * by the specified actor with the specified result. Nevertheless, it should be extended and provide any additional
 * information, like the transition penalty in [ExtendedLTS].
 */
interface LTS<STATE> {
    /**
     * Returns the state corresponding to the initial state of the data structure.
     */
    val initialState: STATE
}


/**
 *  Reflects the current path prefix information and stores the current LTS state
 *  (which essentially indicates the data structure state) for a single step of a legal path search
 *  in LTS-based verifiers. It counts next possible transitions via [nextContexts] function.
 */
abstract class LTSContext<STATE>(
        /**
         * Current execution scenario.
         */
        val scenario: ExecutionScenario,
        /**
         * LTS state of this context
         */
        val state: STATE,
        /**
         * Number of executed actors in each thread. Note that initial and post parts
         * are represented as threads with ids `0` and `threads + 1` respectively.
         */
        val executed: IntArray)
{
    /**
     * Counts next possible states and the corresponding contexts if the specified thread is executed.
     */
    abstract fun nextContexts(threadId: Int): List<LTSContext<STATE>>

    // The total number of actors in init part of the execution scenario.
    val initActors: Int = scenario[0].size
    // The number of executed actors in the init part.
    val initExecuted: Int = executed[0]
    // `true` if all actors in the init part are executed.
    val initCompleted: Boolean = initActors == initExecuted

    // The total number of actors in parallel part of the execution scenario.
    val parallelActors: Int = scenario.parallelExecution.fold(0) { sum, t -> sum + t.size }
    // The number of executed actors in the parallel part.
    val parallelExecuted: Int = executed.slice(1..scenario.threads).fold(0) { sum, e -> sum + e }
    // `true` if all actors in the init part are executed.
    val parallelCompleted: Boolean = parallelActors == parallelExecuted

    // The total number of actors in post part of the execution scenario.
    val postActors: Int = scenario[scenario.threads + 1].size
    // The number of executed actors in the post part.
    val postExecuted: Int = executed[scenario.threads + 1]
    // `true` if all actors in the post part are executed.
    val postCompleted: Boolean = postActors == postExecuted

    // The total number of actors in the execution scenario.
    val totalActors: Int = initActors + parallelActors + postActors
    // The total number of executed actors.
    val totalExecuted: Int = initExecuted + parallelExecuted + postExecuted
    // `true` if all actors are executed and a legal path is found therefore.
    val completed: Boolean = totalActors == totalExecuted

    // Returns `true` if all actors in the specified thread are executed.
    fun isCompleted(threadId: Int) = executed[threadId] == scenario[threadId].size
}


/**
 * Returns scenario for the specified thread. Note that initial and post parts
 * are represented as threads with ids `0` and `threads + 1` respectively.
 */
operator fun ExecutionScenario.get(threadId: Int): List<Actor> = when (threadId) {
    0 -> initExecution
    threads + 1 -> postExecution
    else -> parallelExecution[threadId - 1]
}

/**
 * Returns results for the specified thread. Note that initial and post parts
 * are represented as threads with ids `0` and `threads + 1` respectively.
 */
operator fun ExecutionResult.get(threadId: Int): List<Result> = when (threadId) {
    0 -> initResults
    parallelResults.size + 1 -> postResults
    else -> parallelResults[threadId - 1]
}
