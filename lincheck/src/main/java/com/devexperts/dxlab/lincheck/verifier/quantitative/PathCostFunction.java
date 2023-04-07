/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.verifier.quantitative;

/**
 * Defines strategy to count a path cost according to the
 * "Quantitative relaxation of concurrent data structures"
 * paper by Henzinger, T. A., Kirsch, C. M., Payer, H.,
 * Sezgin, A., & Sokolova, A. (paragraph 4.3)
 */
public enum PathCostFunction {
    /**
     * Maximal cost strategy checks that the maximal transition cost
     * is less than the relaxation factor, ignores predicates.
     * <p>
     * More formally, <tt>pcost = max{cost_i | 1 <= i <= n}</tt>,
     * where <tt>cost_i</tt> is the cost of the <tt>i</tt>-th transition.
     */
    MAX {
        @Override
        IterativePathCostFunctionCounter createIterativePathCostFunctionCounter(int relaxationFactor) {
            return new MaxIterativePathCostFunctionCounter(relaxationFactor);
        }
    },
    /**
     * Phi-interval strategy checks that the maximal subtrace length where the predicate is satisfied
     * is less than the relaxation factor, ignores costs.
     * <p>
     * More formally, <tt>pcost = max{j - i + 1 | phi(i, j) and 1 <= i <= j <= n}</tt>,
     * where <tt>phi(i, j)</tt> means that predicate is satisfied on <tt>[i, j]</tt> subtrace.
     */
    PHI_INTERVAL {
        @Override
        IterativePathCostFunctionCounter createIterativePathCostFunctionCounter(int relaxationFactor) {
            return new PhiIntervalPathCostFunction(relaxationFactor);
        }
    },
    /**
     * Phi-interval restricted maximal cost strategy combines both {@link #MAX maximal}
     * and {@link #PHI_INTERVAL phi-interval} ones.
     * <p>
     * <tt>pcost = max{l(i, j) | phi(i, j) and 1 <= i <= j <= n}</tt>,
     * where <tt>l(i, j) = max{cost_r + (r - i + 1) | i <= r <= j}</tt>.
     */
    PHI_INTERVAL_RESTRICTED_MAX {
        @Override
        IterativePathCostFunctionCounter createIterativePathCostFunctionCounter(int relaxationFactor) {
            return new PhiIntervalRestrictedMaxPathCostFunction(relaxationFactor);
        }
    };

    abstract IterativePathCostFunctionCounter createIterativePathCostFunctionCounter(int relaxationFactor);

    private class MaxIterativePathCostFunctionCounter implements IterativePathCostFunctionCounter {
        private final int relaxationFactor;

        MaxIterativePathCostFunctionCounter(int relaxationFactor) {
            this.relaxationFactor = relaxationFactor;
        }

        @Override
        public IterativePathCostFunctionCounter next(CostWithNextCostCounter costWithNextCostCounter) {
            return costWithNextCostCounter.cost < relaxationFactor ? this : null;
        }
    }

    private class PhiIntervalPathCostFunction implements IterativePathCostFunctionCounter {
        private final int relaxationFactor;
        private final int predicateSatisfactionCount;
        private final PhiIntervalPathCostFunction[] cache;

        PhiIntervalPathCostFunction(int relaxationFactor) {
            this(relaxationFactor, 0, new PhiIntervalPathCostFunction[relaxationFactor]);
            this.cache[0] = this;
        }

        private PhiIntervalPathCostFunction(int relaxationFactor, int predicateSatisfactionCount, PhiIntervalPathCostFunction[] cache) {
            this.relaxationFactor = relaxationFactor;
            this.predicateSatisfactionCount = predicateSatisfactionCount;
            this.cache = cache;
        }

        @Override
        public IterativePathCostFunctionCounter next(CostWithNextCostCounter costWithNextCostCounter) {
            int newPredicateSatisfactionCount = costWithNextCostCounter.predicate ? predicateSatisfactionCount + 1 : 0;
            // Check that the transition is possible
            if (newPredicateSatisfactionCount >= relaxationFactor)
                return null;
            // Get cached function counter
            IterativePathCostFunctionCounter res = cache[newPredicateSatisfactionCount];
            if (res == null)
                res = cache[newPredicateSatisfactionCount] = new PhiIntervalPathCostFunction(relaxationFactor, newPredicateSatisfactionCount, cache);
            return res;
        }
    }

    private class PhiIntervalRestrictedMaxPathCostFunction implements IterativePathCostFunctionCounter {
        final int relaxationFactor;
        final int predicateSatisfactionCount;
        private final PhiIntervalRestrictedMaxPathCostFunction[] cache;

        PhiIntervalRestrictedMaxPathCostFunction(int relaxationFactor) {
            this(relaxationFactor, 0, new PhiIntervalRestrictedMaxPathCostFunction[relaxationFactor]);
        }

        private PhiIntervalRestrictedMaxPathCostFunction(int relaxationFactor, int predicateSatisfactionCount, PhiIntervalRestrictedMaxPathCostFunction[] cache) {
            this.relaxationFactor = relaxationFactor;
            this.predicateSatisfactionCount = predicateSatisfactionCount;
            this.cache = cache;
        }

        @Override
        public PhiIntervalRestrictedMaxPathCostFunction next(CostWithNextCostCounter costWithNextCostCounter) {
            // Check that the transition is possible
            if (predicateSatisfactionCount + costWithNextCostCounter.cost >= relaxationFactor)
                return null;
            int newPredicateSatisfactionCount = costWithNextCostCounter.predicate ? predicateSatisfactionCount + 1 : 0;
            // Get cached function counter
            PhiIntervalRestrictedMaxPathCostFunction res = cache[newPredicateSatisfactionCount];
            if (res == null)
                res = cache[newPredicateSatisfactionCount] = new PhiIntervalRestrictedMaxPathCostFunction(relaxationFactor, newPredicateSatisfactionCount, cache);
            return res;
        }
    }
}
