/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.verifier.serializability;

import com.devexperts.dxlab.lincheck.execution.ExecutionResult;
import com.devexperts.dxlab.lincheck.execution.ExecutionScenario;
import com.devexperts.dxlab.lincheck.verifier.CachedVerifier;
import com.devexperts.dxlab.lincheck.verifier.linearizability.LinearizabilityVerifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This verifier checks that the specified results could be happen in serializable execution.
 * It just tries to find any operatons sequence which execution produces the same results.
 */
public class SerializabilityVerifier extends CachedVerifier {
    private final LinearizabilityVerifier linearizabilityVerifier;

    public SerializabilityVerifier(ExecutionScenario scenario, Class<?> testClass) {
        this.linearizabilityVerifier = new LinearizabilityVerifier(convertScenario(scenario), testClass);
    }

    private static <T> List<List<T>> convert(List<T> initPart, List<List<T>> parallelPart, List<T> postPart) {
        List<T> allActors = new ArrayList<>(initPart);
        parallelPart.forEach(allActors::addAll);
        allActors.addAll(postPart);
        return allActors.stream().map(Collections::singletonList).collect(Collectors.toList());
    }

    private static ExecutionScenario convertScenario(ExecutionScenario scenario) {
        return new ExecutionScenario(
            Collections.emptyList(),
            convert(scenario.initExecution, scenario.parallelExecution, scenario.postExecution),
            Collections.emptyList()
        );
    }

    private static ExecutionResult convertResult(ExecutionResult scenario) {
        return new ExecutionResult(
            Collections.emptyList(),
            convert(scenario.initResults, scenario.parallelResults, scenario.postResults),
            Collections.emptyList()
        );
    }

    @Override
    public boolean verifyResultsImpl(ExecutionResult results) {
        return linearizabilityVerifier.verifyResultsImpl(convertResult(results));
    }
}
