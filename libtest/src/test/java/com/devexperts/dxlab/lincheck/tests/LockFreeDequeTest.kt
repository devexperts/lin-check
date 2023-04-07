/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.tests

import com.devexperts.dxlab.lincheck.LinChecker
import com.devexperts.dxlab.lincheck.annotations.Operation
import com.devexperts.dxlab.lincheck.strategy.randomswitch.RandomSwitchCTest
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTest
import org.junit.Test

@StressCTest
class DequeLinearizabilityTest {
    private val deque = LockFreeDeque<Int>();

    @Operation
    fun pushLeft(value: Int) = deque.pushLeft(value)

    @Operation
    fun pushRight(value: Int) = deque.pushRight(value)

    @Operation
    fun popLeft(): Int? = deque.popLeft()

    @Operation
    fun popRight(): Int? = deque.popRight()

    @Test
    fun test() = LinChecker.check(DequeLinearizabilityTest::class.java)
}
