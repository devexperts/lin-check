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

import java.util.Random;

public class StutteringCounterSimulation {
    private final Random r = new Random(0);
    private final int relaxationFactor;
    private final float stutteringProbability;
    private int value;
    private int stutteringCount;

    public StutteringCounterSimulation(int relaxationFactor, float stutteringProbability) {
        this.relaxationFactor = relaxationFactor;
        this.stutteringProbability = stutteringProbability;
    }

    synchronized int incAndGet() {
        if (stutteringCount + 1 < relaxationFactor && r.nextFloat() < stutteringProbability) {
            stutteringCount++;
            return value;
        } else {
            stutteringCount = 0;
            return ++value;
        }
    }
}
