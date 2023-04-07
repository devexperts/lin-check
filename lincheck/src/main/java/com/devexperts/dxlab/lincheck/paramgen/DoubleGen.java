/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.paramgen;

import java.util.Random;

public class DoubleGen implements ParameterGenerator<Double> {
    private static final float DEFAULT_BEGIN = -10;
    private static final float DEFAULT_END = 10;
    private static final float DEFAULT_STEP = 0.1f;

    private final Random random = new Random(0);
    private final double begin;
    private final double end;
    private final double step;

    public DoubleGen(String configuration) {
        if (configuration.isEmpty()) { // use default configuration
            begin = DEFAULT_BEGIN;
            end = DEFAULT_END;
            step = DEFAULT_STEP;
            return;
        }
        String[] args = configuration.replaceAll("\\s", "").split(":");
        switch (args.length) {
        case 2: // begin:end
            begin = Double.parseDouble(args[0]);
            end = Double.parseDouble(args[1]);
            step = DEFAULT_STEP;
            break;
        case 3: // begin:step:end
            begin = Double.parseDouble(args[0]);
            step = Double.parseDouble(args[1]);
            end = Double.parseDouble(args[2]);
            break;
        default:
            throw new IllegalArgumentException("Configuration should have two (begin and end) " +
                "or three (begin, step and end) arguments  separated by comma");
        }
        if ((end - begin) / step >= Integer.MAX_VALUE)
            throw new IllegalArgumentException("step is too small for specified range");
    }

    public Double generate() {
        double delta = end - begin;
        if (step == 0) // step is not defined
            return begin + delta * random.nextDouble();
        int maxSteps = (int) (delta / step);
        return begin + delta * random.nextInt(maxSteps + 1);
    }
}
