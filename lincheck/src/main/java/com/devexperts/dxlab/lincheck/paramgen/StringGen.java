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

public class StringGen implements ParameterGenerator<String> {
    private static final int DEFAULT_MAX_WORD_LENGTH = 15;
    private static final String DEFAULT_ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_ ";

    private final Random random = new Random(0);
    private final int maxWordLength;
    private final String alphabet;

    public StringGen(String configuration) {
        if (configuration.isEmpty()) { // use default configuration
            maxWordLength = DEFAULT_MAX_WORD_LENGTH;
            alphabet = DEFAULT_ALPHABET;
            return;
        }
        int firstCommaIndex = configuration.indexOf(':');
        if (firstCommaIndex < 0) { // maxWordLength only
            maxWordLength = Integer.parseInt(configuration);
            alphabet = DEFAULT_ALPHABET;
        } else { // maxWordLength:alphabet
            maxWordLength = Integer.parseInt(configuration.substring(0, firstCommaIndex));
            alphabet = configuration.substring(firstCommaIndex + 1);
        }
    }

    public String generate() {
        char[] cs = new char[random.nextInt(maxWordLength)];
        for (int i = 0; i < cs.length; i++)
            cs[i] = alphabet.charAt(random.nextInt(alphabet.length()));
        return new String(cs);
    }
}
