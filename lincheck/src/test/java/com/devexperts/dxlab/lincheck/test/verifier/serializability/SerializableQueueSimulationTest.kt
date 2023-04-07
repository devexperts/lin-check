/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.test.verifier.serializability

import com.devexperts.dxlab.lincheck.LinChecker
import com.devexperts.dxlab.lincheck.annotations.Operation
import com.devexperts.dxlab.lincheck.annotations.Param
import com.devexperts.dxlab.lincheck.paramgen.IntGen
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTest
import com.devexperts.dxlab.lincheck.verifier.serializability.SerializabilityVerifier
import org.junit.Test

@StressCTest(actorsBefore = 2,
             threads = 2, actorsPerThread = 4,
             actorsAfter = 2,
             verifier = SerializabilityVerifier::class)
class SerializableQueueSimulationTest {
    val q = SerializableQueueSimulation<Int>()

    @Operation
    fun push(@Param(gen = IntGen::class) item: Int) = q.push(item)

    @Operation
    fun pop(): Int? = q.pop()

    @Test
    fun test() = LinChecker.check(SerializableQueueSimulationTest::class.java)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SerializableQueueSimulationTest

        if (q != other.q) return false

        return true
    }

    override fun hashCode(): Int {
        return q.hashCode()
    }


}
