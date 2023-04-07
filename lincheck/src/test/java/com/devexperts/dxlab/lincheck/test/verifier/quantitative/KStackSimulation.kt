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

import java.util.*
import kotlin.collections.ArrayList

class KStackSimulation<T>(val k: Int) {
    private val list = ArrayList<T>()

    private val random = Random(0)

    fun push(value: T) = synchronized(this) {
        list.add(0, value)
    }

    fun pushRelaxed(value: T) = synchronized(this) {
        val index = random.nextInt(k).coerceAtMost(list.size)
        list.add(index, value)
    }


    fun popOrNull(): T? = synchronized(this) {
        return if (list.isEmpty()) null else list.removeAt(0)
    }

    fun popOrNullRelaxed(): T? = synchronized(this) {
        if (list.isEmpty())
            return null
        val index = random.nextInt(k).coerceAtMost(list.size - 1)
        return list.removeAt(index)
    }
}
