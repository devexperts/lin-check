/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck;

import com.devexperts.dxlab.lincheck.runner.Runner;
import com.devexperts.dxlab.lincheck.strategy.Strategy;
import com.devexperts.jagent.ClassInfo;
import com.devexperts.jagent.ClassInfoCache;
import com.devexperts.jagent.ClassInfoVisitor;
import com.devexperts.jagent.FrameClassWriter;
import com.devexperts.jagent.Log;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This transformer applies required for {@link Strategy} and {@link Runner}
 * class transformations and hines them from others.
 */
public class TransformationClassLoader extends ExecutionClassLoader {
    // Strategy and runner provide class transformers
    private final Strategy strategy;
    private final Runner runner;
    // Cache for classloading and frames computing during the transformation
    private final Map<String, Class<?>> cache = new ConcurrentHashMap<>();
    private final ClassInfoCache ciCache = new ClassInfoCache(new Log("lin-check", Log.Level.DEBUG, null));

    public TransformationClassLoader(Strategy strategy, Runner runner) {
        this.strategy = strategy;
        this.runner = runner;
    }

    /**
     * Check if class should not be transformed
     *
     * @param className checking class name
     * @return result of checking class
     */
    private static boolean doNotTransform(String className) {
        return className == null ||
            (className.startsWith("com.devexperts.dxlab.lincheck.") &&
                !className.startsWith("com.devexperts.dxlab.lincheck.test.") &&
                !className.equals("com.devexperts.dxlab.lincheck.strategy.ManagedStrategyHolder")) ||
            className.startsWith("sun.") ||
            className.startsWith("java.");
        // TODO let's transform java.util.concurrent
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            Class<?> result = cache.get(name);
            if (result != null) {
                return result;
            }
            if (doNotTransform(name)) {
                result = super.loadClass(name);
                cache.put(name, result);
                return result;
            }
            try {
                byte[] bytes = instrument(name);
                result = defineClass(name, bytes, 0, bytes.length);
                cache.put(name, result);
                return result;
            } catch (Exception e) {
                throw new IllegalStateException("Cannot transform class " + name, e);
            }
        }
    }

    /**
     * Reads class as resource, instruments it (applies {@link Strategy}'s transformer at first,
     * then {@link Runner}'s) and returns the resulting byte-code.
     *
     * @param className name of the class to be transformed.
     * @return the byte-code of the transformed class.
     * @throws IOException if class could not be read as a resource.
     */
    private byte[] instrument(String className) throws IOException {
        // Create ClassReader
        ClassReader cr = new ClassReader(className);
        // Build class info
        ClassInfoVisitor ciVisitor = new ClassInfoVisitor();
        cr.accept(ciVisitor, 0);
        ClassInfo ci = ciVisitor.buildClassInfo();
        // Construct transformation pipeline:
        // apply the strategy's transformer at first,
        // then the runner's one.
        ClassWriter cw = new FrameClassWriter(this, ciCache, ci.getVersion());
        ClassVisitor cv = new CheckClassAdapter(cw, false); // for debug
        if (runner.needsTransformation()) {
            cv = runner.createTransformer(cv, ci);
        }
        if (strategy.needsTransformation()) {
            cv = strategy.createTransformer(cv, ci);
        }
        // Get transformed bytecode
        cr.accept(cv, ClassReader.EXPAND_FRAMES);
        return cw.toByteArray();
    }

    @Override
    public URL getResource(String name) {
        return super.getResource(name);
    }
}
