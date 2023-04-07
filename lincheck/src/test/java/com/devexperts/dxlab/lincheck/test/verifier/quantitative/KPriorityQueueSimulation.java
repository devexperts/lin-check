/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.test.verifier.quantitative;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class KPriorityQueueSimulation {
    private final Random r = new Random(0);
    private final int relaxationFactor;
    private final List<Integer> values = new ArrayList<>();

    private int countTopNotPopped;

    public KPriorityQueueSimulation(int relaxationFactor) {
        this.relaxationFactor = relaxationFactor;
    }

    synchronized void push(int value) {
        // Push is not relaxed
        values.add(value);
        Collections.sort(values);
    }

    synchronized Integer poll() {
        if (values.isEmpty()) {
            countTopNotPopped = 0;
            return null;
        }
        int index = r.nextInt(Math.min(relaxationFactor - countTopNotPopped, values.size()));
        if (index == 0)
            countTopNotPopped = 0;
        else
            countTopNotPopped++;
        return values.remove(index);
    }
}
