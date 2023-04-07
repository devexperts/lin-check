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

public class IntGen implements ParameterGenerator<Integer> {
    private static final int DEFAULT_BEGIN = -10;
    private static final int DEFAULT_END = 10;

    private final Random random = new Random(0);
    private final int begin;
    private final int end;

    public IntGen(String configuration) {
        if (configuration.isEmpty()) { // use default configuration
            begin = DEFAULT_BEGIN;
            end = DEFAULT_END;
            return;
        }
        String[] args = configuration.replaceAll("\\s", "").split(":");
        switch (args.length) {
        case 2: // begin:end
            begin = Integer.parseInt(args[0]);
            end = Integer.parseInt(args[1]);
            break;
        default:
            throw new IllegalArgumentException("Configuration should have " +
                "two arguments (begin and end) separated by comma");
        }
    }

    public Integer generate() {
        return begin + random.nextInt(end - begin + 1);
    }

    void checkRange(int min, int max, String type) {
        if (this.begin < min || this.end - 1 > max) {
            throw new IllegalArgumentException("Illegal range for "
                + type + " type: [" + begin + "; " + end + ")");
        }
    }
}
