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
import com.devexperts.dxlab.lincheck.Utils
import java.util.*


/**
 * The standard regular LTS is defined as [State x Op x State],
 * where `Op` is a pair of [Actor] and [Result] in our terms.
 * In order not to construct the full LTS (which is impossible
 * because it can be either infinite or just too big to build),
 * we construct it lazily during the requests and reuse it between runs.
 *
 * Taking into account that every transition from the specified element
 * by the specified actor determines the possible result and
 * the next state uniquely, we internally represent the regular LTS as
 * [State x Actor x ResultWithNextState], which reduces the number of
 * transitions to be stored.
 *
 * In order to perform sequential executions, [seqImplClass] should be
 * provided and has an empty constructor which creates the initial state
 * of the testing data structure.
 */
class RegularLTS(val seqImplClass: Class<*>) : LTS<RegularLTS.State> {
    override val initialState = State(emptyArray())
    // seqImpl -> State
    private val states: MutableMap<Any, State> = mutableMapOf()

    inner class State(private val actorsToCreate: Array<Actor>) {
        private val transitions: MutableMap<Actor, ResultWithNextState> = HashMap()

        fun next(actor: Actor, result: Result): State? {
            val resultWithNextState = transitions.computeIfAbsent(actor) {
                val newSeqImpl = copySeqImpl()
                val res = Utils.executeActor(newSeqImpl, actor)
                val actors = actorsToCreate + actor
                ResultWithNextState(res, getOrCreateState(newSeqImpl, actors))
            }
            return if (resultWithNextState.result == result) resultWithNextState.state else null
        }

        private fun copySeqImpl(): Any {
            val newSeqImpl = seqImplClass.newInstance()
            actorsToCreate.forEach { actor -> Utils.executeActor(newSeqImpl, actor) }
            return newSeqImpl
        }
    }

    private fun getOrCreateState(seqImpl: Any, actorsToCreate: Array<Actor>): State {
        return states.computeIfAbsent(seqImpl) { State(actorsToCreate) }
    }

    private class ResultWithNextState(val result: Result, val state: State)
}
