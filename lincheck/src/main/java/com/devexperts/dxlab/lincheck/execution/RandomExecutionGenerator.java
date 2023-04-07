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

import com.devexperts.dxlab.lincheck.Actor;
import com.devexperts.dxlab.lincheck.CTestConfiguration;
import com.devexperts.dxlab.lincheck.CTestStructure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class RandomExecutionGenerator extends ExecutionGenerator {
    private final Random random = new Random(0);

    public RandomExecutionGenerator(CTestConfiguration testConfiguration, CTestStructure testStructure) {
        super(testConfiguration, testStructure);
    }

    @Override
    public ExecutionScenario nextExecution() {
        // Create init execution part
        List<ActorGenerator> notUseOnce = testStructure.actorGenerators.stream()
            .filter(ag -> !ag.useOnce()).collect(Collectors.toList());
        List<Actor> initExecution = new ArrayList<>();
        for (int i = 0; i < testConfiguration.actorsBefore && !notUseOnce.isEmpty(); i++) {
            ActorGenerator ag = notUseOnce.get(random.nextInt(notUseOnce.size()));
            initExecution.add(ag.generate());
        }
        // Create parallel execution part
        // Construct non-parallel groups and parallel one
        List<CTestStructure.OperationGroup> nonParallelGroups = testStructure.operationGroups.stream()
            .filter(g -> g.nonParallel)
            .collect(Collectors.toList());
        Collections.shuffle(nonParallelGroups);
        List<ActorGenerator> parallelGroup = new ArrayList<>(testStructure.actorGenerators);
        nonParallelGroups.forEach(g -> parallelGroup.removeAll(g.actors));

        List<List<Actor>> parallelExecution = new ArrayList<>();
        List<ThreadGen> threadGens = new ArrayList<>();
        for (int i = 0; i < testConfiguration.threads; i++) {
            parallelExecution.add(new ArrayList<>());
            threadGens.add(new ThreadGen(i, testConfiguration.actorsPerThread));
        }
        for (int i = 0; i < nonParallelGroups.size(); i++) {
            threadGens.get(i % threadGens.size()).nonParallelActorGenerators
                .addAll(nonParallelGroups.get(i).actors);
        }
        List<ThreadGen> tgs2 = new ArrayList<>(threadGens);
        while (!threadGens.isEmpty()) {
            for (Iterator<ThreadGen> it = threadGens.iterator(); it.hasNext(); ) {
                ThreadGen threadGen = it.next();
                int aGenIndexBound = threadGen.nonParallelActorGenerators.size() + parallelGroup.size();
                if (aGenIndexBound == 0) {
                    it.remove();
                    continue;
                }
                int aGenIndex = random.nextInt(aGenIndexBound);
                ActorGenerator agen;
                if (aGenIndex < threadGen.nonParallelActorGenerators.size()) {
                    agen = getActorGenFromGroup(threadGen.nonParallelActorGenerators, aGenIndex);
                } else {
                    agen = getActorGenFromGroup(parallelGroup,
                        aGenIndex - threadGen.nonParallelActorGenerators.size());
                }
                parallelExecution.get(threadGen.threadNumber).add(agen.generate());
                if (--threadGen.left == 0)
                    it.remove();
            }
        }
        // Create post execution part
        List<ActorGenerator> leftActorGenerators = new ArrayList<>(parallelGroup);
        for (ThreadGen threadGen : tgs2)
            leftActorGenerators.addAll(threadGen.nonParallelActorGenerators);
        List<Actor> postExecution = new ArrayList<>();
        for (int i = 0; i < testConfiguration.actorsAfter && !leftActorGenerators.isEmpty(); i++) {
            ActorGenerator agen = getActorGenFromGroup(leftActorGenerators, random.nextInt(leftActorGenerators.size()));
            postExecution.add(agen.generate());
        }
        return new ExecutionScenario(initExecution, parallelExecution, postExecution);
    }

    private ActorGenerator getActorGenFromGroup(List<ActorGenerator> aGens, int index) {
        ActorGenerator aGen = aGens.get(index);
        if (aGen.useOnce())
            aGens.remove(index);
        return aGen;
    }

    private class ThreadGen {
        final List<ActorGenerator> nonParallelActorGenerators = new ArrayList<>();
        int threadNumber;
        int left;

        ThreadGen(int threadNumber, int nActors) {
            this.threadNumber = threadNumber;
            this.left = nActors;
        }
    }
}
