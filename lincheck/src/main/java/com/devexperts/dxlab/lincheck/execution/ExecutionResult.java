/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.execution;

import com.devexperts.dxlab.lincheck.Result;

import java.util.List;
import java.util.Objects;

/**
 * This class represents a result corresponding to
 * the specified {@link ExecutionScenario scenario} execution.
 * <p>
 * All the result parts should have the same dimensions as the scenario.
 */
public class ExecutionResult {
    /**
     * Results of the initial sequential part of the execution.
     * @see ExecutionScenario#initExecution
     */
    public final List<Result> initResults;
    /**
     * Results of the parallel part of the execution.
     * @see ExecutionScenario#parallelExecution
     */
    public final List<List<Result>> parallelResults;
    /**
     * Results of the last sequential part of the execution.
     * @see ExecutionScenario#postExecution
     */
    public final List<Result> postResults;

    public ExecutionResult(List<Result> initResults, List<List<Result>> parallelResults, List<Result> postResults) {
        this.initResults = initResults;
        this.parallelResults = parallelResults;
        this.postResults = postResults;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ExecutionResult that = (ExecutionResult) o;
        return Objects.equals(initResults, that.initResults) &&
            Objects.equals(parallelResults, that.parallelResults) &&
            Objects.equals(postResults, that.postResults);
    }

    @Override
    public int hashCode() {
        return Objects.hash(initResults, parallelResults, postResults);
    }
}
