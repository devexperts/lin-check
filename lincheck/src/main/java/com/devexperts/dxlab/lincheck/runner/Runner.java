/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.runner;

import com.devexperts.dxlab.lincheck.ExecutionClassLoader;
import com.devexperts.dxlab.lincheck.TransformationClassLoader;
import com.devexperts.dxlab.lincheck.execution.ExecutionResult;
import com.devexperts.dxlab.lincheck.execution.ExecutionScenario;
import com.devexperts.dxlab.lincheck.strategy.Strategy;
import com.devexperts.jagent.ClassInfo;
import org.objectweb.asm.ClassVisitor;

/**
 * Runner determines how to run your concurrent test. In order to support techniques
 * like fibers, it may require code transformation, so {@link #needsTransformation()}
 * method has to return {@code true} and {@link #createTransformer(ClassVisitor, ClassInfo)}
 * one has to be implemented.
 */
public abstract class Runner {
    protected final ExecutionScenario scenario;
    protected final Class<?> testClass;
    public final ExecutionClassLoader classLoader;

    protected Runner(ExecutionScenario scenario, Strategy strategy, Class<?> testClass) {
        this.scenario = scenario;
        classLoader = (this.needsTransformation() || strategy.needsTransformation()) ?
            new TransformationClassLoader(strategy, this) : new ExecutionClassLoader();
        this.testClass = loadClass(testClass.getTypeName());
    }

    /**
     * Loads class using runner's class loader
     */
    private Class<?> loadClass(String className) {
        try {
            return classLoader.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Cannot load class " + className, e);
        }
    }

    /**
     * Creates required for this runner transformer.
     * Throws {@link UnsupportedOperationException} by default.
     *
     * @return class visitor which transform the code due to support this runner.
     */
    public ClassVisitor createTransformer(ClassVisitor cv, ClassInfo classInfo) {
        throw new UnsupportedOperationException(getClass() + " runner does not transform classes");
    }

    /**
     * This method has to return {@code true} if code transformation is required for runner.
     * Returns {@code false} by default.
     */
    public boolean needsTransformation() {
        return false;
    }

    /**
     * Runs next invocation
     * @return the obtained results
     */
    public abstract ExecutionResult run() throws InterruptedException;

    /**
     * This method is invoked by every test thread as the first operation.
     * @param iThread number of invoking thread
     */
    public void onStart(int iThread) {}

    /**
     * This method is invoked by every test thread as the last operation
     * if no exception has been thrown.
     * @param iThread number of invoking thread
     */
    public void onFinish(int iThread) {}

    /**
     * Closes used for this runner resources.
     */
    public void close() {}
}
