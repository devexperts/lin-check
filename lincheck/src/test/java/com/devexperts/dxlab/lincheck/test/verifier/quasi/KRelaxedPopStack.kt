/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.test.verifier.quasi

import java.util.*

class KRelaxedPopStack<T>(private val k: Int) {

    private val list = LinkedList<T>()

    private val random = Random()

    @Synchronized
    fun push(value: T) {
        list.push(value)
    }

    @Synchronized
    fun push1(value: T) {
        list.push(value)
    }

    @Synchronized
    fun push2(value: T) {
        list.push(value)
    }

    @Synchronized
    fun pop(): T? {
        if (list.isEmpty()) {
            return null
        }
        val index = random.nextInt(k + 1).coerceAtMost(list.size - 1)
        return list.removeAt(index)
    }

}
