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
import com.devexperts.dxlab.lincheck.Result
import com.devexperts.dxlab.lincheck.verifier.LTS
import com.devexperts.dxlab.lincheck.verifier.RegularLTS
import com.devexperts.dxlab.lincheck.verifier.quantitative.ExtendedLTS.State
import java.lang.reflect.Method

/**
 * This LTS is based on the presented in the "Quantitative relaxation of concurrent data structures"
 * paper by Henzinger et al. Unlike their formalism, we use a mix of regular and extended labeled
 * transition systems. Usually, data structures have both relaxed and non-relaxed methods, which
 * should be verified differently. Essentially, we store both regular transitions ([State x Op x State],
 * where `Op` is a pair of [Actor] and [Result] in our terms) and relaxed transitions with the cost
 * ([State x Op_Ext x State], where `Op_Ext` is a tuple of [Actor], [Result], and cost ([Int]) in our terms).
 *
 * Like in [RegularLTS], there is a similar [State.nextRegular] method for non-relaxed transitions. However,
 * there is also [State.nextRelaxed] method which returns a list of next `CostCounter` instances with
 * the transition costs, which is represented via [CostWithNextCostCounter] class. In order to get next states,
 * a special [getStateForCostCounter] method should be used.
 */
class ExtendedLTS(private val costCounterClass: Class<*>, relaxationFactor: Int) : LTS<ExtendedLTS.State> {
    // costCounter -> State
    private val states: MutableMap<Any, State> = HashMap()

    override val initialState = getStateForCostCounter(costCounterClass.getConstructor(Int::class.java).newInstance(relaxationFactor))

    inner class State(private val costCounter: Any) {
        // [actor, result] -> List<CostWithNextCostCounter> | nextCostCounter | NULL (for null)
        private val transitions: MutableMap<ActorWithResult, Any> = HashMap()


        fun nextRegular(actor: Actor, result: Result): State? {
            val next = next(actor, result) ?: return null
            check(next.javaClass == costCounterClass) {
                "Non-relaxed $actor should store transitions within CostCounter instances, but $next is found"
            }
            return getStateForCostCounter(next)
        }

        fun nextRelaxed(actor: Actor, result: Result): List<CostWithNextCostCounter<*>> {
            val next = next(actor, result)
            check(next is List<*>) {
                "Relaxed $actor should store transitions within a list of CostWithNextCostCounter, but $next is found"
            }
            return next as List<CostWithNextCostCounter<*>>
        }

        private fun next(actor: Actor, result: Result): Any? {
            val next = transitions.computeIfAbsent(ActorWithResult(actor, result)) {
                val actorRelatedMethod = getCostCounterMethod(actor.method)
                val params = actor.arguments + result
                actorRelatedMethod.invoke(costCounter, *params) ?: NULL
            }
            return if (next == NULL) null else next
        }
    }

    fun getStateForCostCounter(costCounter: Any) = states.computeIfAbsent(costCounter) { State(costCounter) }

    private data class ActorWithResult(val actor: Actor, val result: Result)

    // test class method -> cost counter method
    private val methodsMapping: MutableMap<Method, Method> = HashMap()

    private fun getCostCounterMethod(testClassMethod: Method): Method {
        return methodsMapping.computeIfAbsent(testClassMethod) {
            val params = testClassMethod.parameterTypes + Result::class.java
            costCounterClass.getMethod(testClassMethod.name, *params)
        }
    }
}

private val NULL = Any()
