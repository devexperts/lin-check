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

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.Result;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTest;
import com.devexperts.dxlab.lincheck.verifier.quantitative.CostWithNextCostCounter;
import com.devexperts.dxlab.lincheck.verifier.quantitative.QuantitativeRelaxationVerifier;
import com.devexperts.dxlab.lincheck.verifier.quantitative.QuantitativeRelaxationVerifierConf;
import com.devexperts.dxlab.lincheck.verifier.quantitative.QuantitativeRelaxed;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static com.devexperts.dxlab.lincheck.verifier.quantitative.PathCostFunction.PHI_INTERVAL;

@Ignore
@StressCTest(verifier = QuantitativeRelaxationVerifier.class)
@QuantitativeRelaxationVerifierConf(factor = 3, pathCostFunc = PHI_INTERVAL,
    costCounter = StutteringCounterTest.CostCounter.class)
public class StutteringCounterTest {
    private StutteringCounterSimulation counter = new StutteringCounterSimulation(3, 0.9f);

    @QuantitativeRelaxed
    @Operation
    public int incAndGet() {
        return counter.incAndGet();
    }

    @Test
    public void test() {
        LinChecker.check(StutteringCounterTest.class);
    }

    // Predicate: counter is not incremented
    public static class CostCounter {
        private final int k;
        private final int value;

        public CostCounter(int k) {
            this(k, 0);
        }

        private CostCounter(int k, int value) {
            this.k = k;
            this.value = value;
        }

        public List<CostWithNextCostCounter<CostCounter>> incAndGet(Result result) {
            if (result.getValue().equals(value)) {
                // The counter is not incremented
                return Collections.singletonList(
                    new CostWithNextCostCounter<>(new CostCounter(k, value), true));
            } else if (result.getValue().equals(value + 1)) {
                // The counter is incremented
                return Collections.singletonList(
                    new CostWithNextCostCounter<>(new CostCounter(k, value + 1), false));
            } else {
                // Only incremented or the same values are possible
                return Collections.emptyList();
            }
        }
    }
}
