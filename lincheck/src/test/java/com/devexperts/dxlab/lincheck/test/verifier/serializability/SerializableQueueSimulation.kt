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

import java.util.Collections.shuffle

data class SerializableQueueSimulation<T>(
        private val queue: ArrayList<T> = ArrayList(),
        private val pushQueue: ArrayList<T> = ArrayList()
) {
    fun push(item: T) = synchronized(this) {
        pushQueue += item
    }

    fun pop(): T? = synchronized(this) {
        if (queue.isEmpty()) {
            shuffle(pushQueue)
            queue.addAll(pushQueue)
            pushQueue.clear()
        }
        return if (queue.isEmpty()) null else queue.removeAt(0)
    }
}
