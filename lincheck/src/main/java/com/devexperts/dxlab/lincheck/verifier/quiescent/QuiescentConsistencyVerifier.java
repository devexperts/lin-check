/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.verifier.quiescent;

import com.devexperts.dxlab.lincheck.Actor;
import com.devexperts.dxlab.lincheck.execution.ExecutionResult;
import com.devexperts.dxlab.lincheck.execution.ExecutionScenario;
import com.devexperts.dxlab.lincheck.verifier.CachedVerifier;
import com.devexperts.dxlab.lincheck.verifier.linearizability.LinearizabilityVerifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuiescentConsistencyVerifier extends CachedVerifier {
    private final ExecutionScenario originalScenario;
    private final LinearizabilityVerifier linearizabilityVerifier;

    public QuiescentConsistencyVerifier(ExecutionScenario scenario, Class<?> testClass) {
        this.originalScenario = scenario;
        this.linearizabilityVerifier = new LinearizabilityVerifier(convertScenario(scenario), testClass);
    }

    private static ExecutionScenario convertScenario(ExecutionScenario scenario) {
        List<List<Actor>> newParallelExecution = convertAccordingToScenario(scenario.parallelExecution, scenario.parallelExecution);
        return new ExecutionScenario(scenario.initExecution, newParallelExecution, scenario.postExecution);
    }

    private static <T> List<List<T>> convertAccordingToScenario(List<List<Actor>> parallelExecution, List<List<T>> toConvert) {
        List<List<T>> res = new ArrayList<>();
        for (int t = 0; t < parallelExecution.size(); t++)
            res.add(new ArrayList<>());
        for (int t = 0; t < parallelExecution.size(); t++) {
            int nActors = parallelExecution.get(t).size();
            for (int i = 0; i < nActors; i++) {
                Actor actor = parallelExecution.get(t).get(i);
                T val = toConvert.get(t).get(i);
                if (isQuiescentConsistent(actor) && nActors > 1)
                    res.add(Collections.singletonList(val));
                else
                    res.get(t).add(val);
            }
        }
        return res;
    }

    private static boolean isQuiescentConsistent(Actor actor) {
        return actor.method.isAnnotationPresent(QuiescentConsistent.class);
    }

    @Override
    public boolean verifyResultsImpl(ExecutionResult results) {
        return linearizabilityVerifier.verifyResultsImpl(new ExecutionResult(
            results.initResults,
            convertAccordingToScenario(originalScenario.parallelExecution, results.parallelResults),
            results.postResults)
        );
    }
}
