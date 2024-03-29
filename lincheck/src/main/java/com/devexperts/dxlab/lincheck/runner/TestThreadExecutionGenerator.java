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

import com.devexperts.dxlab.lincheck.Actor;
import com.devexperts.dxlab.lincheck.Result;
import com.devexperts.dxlab.lincheck.Utils;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.commons.TryCatchBlockSorter;
import org.objectweb.asm.util.CheckClassAdapter;

import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

/**
 * This class is used to generate {@link TestThreadExecution thread executions}.
 */
public class TestThreadExecutionGenerator {
    private static final Type[] NO_ARGS = new Type[] {};

    private static final Type CLASS_TYPE = Type.getType(Class.class);
    private static final Type OBJECT_TYPE = Type.getType(Object.class);
    private static final Method OBJECT_GET_CLASS = new Method("getClass", CLASS_TYPE, NO_ARGS);
    private static final Type OBJECT_ARRAY_TYPE = Type.getType(Object[].class);
    private static final Type THROWABLE_TYPE = Type.getType(Throwable.class);
    private static final Type INT_ARRAY_TYPE = Type.getType(int[].class);
    private static final Method EMPTY_CONSTRUCTOR = new Method("<init>", Type.VOID_TYPE, NO_ARGS);

    private static final Type RUNNER_TYPE = Type.getType(Runner.class);
    private static final Method RUNNER_ON_START_METHOD = new Method("onStart", Type.VOID_TYPE, new Type[]{Type.INT_TYPE});
    private static final Method RUNNER_ON_FINISH_METHOD = new Method("onFinish", Type.VOID_TYPE, new Type[]{Type.INT_TYPE});

    private static final Type TEST_THREAD_EXECUTION_TYPE = Type.getType(TestThreadExecution.class);
    private static final Method TEST_THREAD_EXECUTION_CONSTRUCTOR;

    private static final Type UTILS_TYPE = Type.getType(Utils.class);
    private static final Method UTILS_CONSUME_CPU = new Method("consumeCPU", Type.VOID_TYPE, new Type[] {Type.INT_TYPE});

    private static final Type RESULT_TYPE = Type.getType(Result.class);
    private static final Type RESULT_ARRAY_TYPE = Type.getType(Result[].class);
    private static final Method RESULT_CREATE_VOID_RESULT = new Method("createVoidResult", RESULT_TYPE, NO_ARGS);
    private static final Method RESULT_CREATE_VALUE_RESULT = new Method("createValueResult", RESULT_TYPE, new Type[] {OBJECT_TYPE});
    private static final Method RESULT_CREATE_EXCEPTION_RESULT = new Method("createExceptionResult", RESULT_TYPE, new Type[] {CLASS_TYPE});

    private static int generatedClassNumber = 0;

    static {
        try {
            TEST_THREAD_EXECUTION_CONSTRUCTOR = Method.getMethod(TestThreadExecution.class.getDeclaredConstructor());
        } catch (NoSuchMethodException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Creates a {@link TestThreadExecution} instance with specified {@link TestThreadExecution#call()} implementation.
     */
    public static TestThreadExecution create(Runner runner, int iThread, List<Actor> actors, boolean waitsEnabled) {
        String className = TestThreadExecution.class.getCanonicalName() + generatedClassNumber++;
        String internalClassName = className.replace('.', '/');
        List<Object> objArgs = new ArrayList<>();
        Class<? extends TestThreadExecution> clz = runner.classLoader.defineClass(className,
            generateClass(internalClassName, Type.getType(runner.testClass), iThread, actors, objArgs, waitsEnabled));
        try {
            TestThreadExecution execution = clz.newInstance();
            execution.runner = runner;
            execution.objArgs = objArgs.toArray();
            return execution;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException("Cannot initialize generated execution class", e);
        }
    }

    private static byte[] generateClass(String internalClassName, Type testClassType, int iThread, List<Actor> actors,
        List<Object> objArgs, boolean waitsEnabled)
    {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        CheckClassAdapter cca = new CheckClassAdapter(cw, false);
        cca.visit(52, ACC_PUBLIC + ACC_SUPER, internalClassName, null, TEST_THREAD_EXECUTION_TYPE.getInternalName(), null);
        generateConstructor(cca);
        generateRun(cca, testClassType, iThread, actors, objArgs, waitsEnabled);
        cca.visitEnd();
        return cw.toByteArray();
    }

    private static void generateConstructor(ClassVisitor cv) {
        GeneratorAdapter mv = new GeneratorAdapter(ACC_PUBLIC, EMPTY_CONSTRUCTOR, null, null, cv);
        mv.visitCode();
        mv.loadThis();
        mv.invokeConstructor(TEST_THREAD_EXECUTION_TYPE, TEST_THREAD_EXECUTION_CONSTRUCTOR);
        mv.visitInsn(RETURN);
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    private static void generateRun(ClassVisitor cv, Type testType, int iThread, List<Actor> actors, List<Object> objArgs, boolean waitsEnabled) {
        int access = ACC_PUBLIC;
        Method m = new Method("call", RESULT_ARRAY_TYPE, NO_ARGS);
        GeneratorAdapter mv = new GeneratorAdapter(access, m,
            // Try-catch blocks sorting is required
            new TryCatchBlockSorter(cv.visitMethod(access, m.getName(), m.getDescriptor(), null, null),
                access, m.getName(), m.getDescriptor(), null, null)
        );
        mv.visitCode();
        // Create Result[] array and store it to a local variable
        int resLocal = createResultArray(mv, actors.size());
        // Call runner's onStart(iThread) method
        mv.loadThis();
        mv.getField(TEST_THREAD_EXECUTION_TYPE, "runner", RUNNER_TYPE);
        mv.push(iThread);
        mv.invokeVirtual(RUNNER_TYPE, RUNNER_ON_START_METHOD);
        // Number of current operation (starts with 0)
        int iLocal = mv.newLocal(Type.INT_TYPE);
        mv.push(0);
        mv.storeLocal(iLocal);
        // Invoke actors
        for (int i = 0; i < actors.size(); i++) {
            Actor actor = actors.get(i);
            // Add busy-wait before operation execution (for non-first operations only)
            if (waitsEnabled && i > 0) {
                mv.loadThis();
                mv.getField(TEST_THREAD_EXECUTION_TYPE, "waits", INT_ARRAY_TYPE);
                mv.push(i - 1);
                mv.arrayLoad(Type.INT_TYPE);
                mv.invokeStatic(UTILS_TYPE, UTILS_CONSUME_CPU);
            }
            // Start of try-catch block for exceptions which this actor should handle
            Label start, end = null, handler = null, handlerEnd = null;
            if (actor.handlesExceptions()) {
                start = mv.newLabel();
                end = mv.newLabel();
                handler = mv.newLabel();
                handlerEnd = mv.newLabel();
                for (Class<? extends Throwable> ec : actor.handledExceptions)
                    mv.visitTryCatchBlock(start, end, handler, Type.getType(ec).getInternalName());
                mv.visitLabel(start);
            }
            // Load result array and index to store the current result
            mv.loadLocal(resLocal);
            mv.push(i);
            // Load test instance
            mv.loadThis();
            mv.getField(TEST_THREAD_EXECUTION_TYPE, "testInstance", OBJECT_TYPE);
            mv.checkCast(testType);
            // Load arguments for operation
            for (int j = 0; j < actor.arguments.length; j++) {
                pushArgumentOnStack(mv, objArgs, actor.arguments[j], actor.method.getParameterTypes()[j]);
            }
            // Invoke operation
            Method actorMethod = Method.getMethod(actor.method);
            mv.invokeVirtual(testType, actorMethod);
            // Create result
            mv.box(actorMethod.getReturnType()); // box if needed
            if (actor.method.getReturnType() == void.class) {
                mv.pop();
                mv.invokeStatic(RESULT_TYPE, RESULT_CREATE_VOID_RESULT);
            } else {
                mv.invokeStatic(RESULT_TYPE, RESULT_CREATE_VALUE_RESULT);
            }
            // Store result to array
            mv.arrayStore(RESULT_TYPE);
            // End of try-catch block
            if (actor.handlesExceptions()) {
                mv.visitLabel(end);
                mv.goTo(handlerEnd);
                mv.visitLabel(handler);
                storeExceptionResultFromThrowable(mv, resLocal, iLocal);
                mv.visitLabel(handlerEnd);
            }
            // Increment number of current operation
            mv.iinc(iLocal, 1);
        }
        // Call runner's onFinish(iThread) method
        mv.loadThis();
        mv.getField(TEST_THREAD_EXECUTION_TYPE, "runner", RUNNER_TYPE);
        mv.push(iThread);
        mv.invokeVirtual(RUNNER_TYPE, RUNNER_ON_FINISH_METHOD);
        // Return results
        mv.loadThis();
        mv.loadLocal(resLocal);
        mv.returnValue();
        mv.visitMaxs(1, 1);
        mv.visitEnd();
    }

    private static void storeExceptionResultFromThrowable(GeneratorAdapter mv, int resLocal, int iLocal) {
        int eLocal = mv.newLocal(THROWABLE_TYPE);
        mv.storeLocal(eLocal);
        mv.loadLocal(resLocal);
        mv.loadLocal(iLocal);
        mv.loadLocal(eLocal);
        mv.invokeVirtual(OBJECT_TYPE, OBJECT_GET_CLASS);
        mv.invokeStatic(RESULT_TYPE, RESULT_CREATE_EXCEPTION_RESULT);
        mv.arrayStore(RESULT_TYPE);
    }

    private static int createResultArray(GeneratorAdapter mv, int size) {
        int resLocal = mv.newLocal(RESULT_ARRAY_TYPE);
        mv.push(size);
        mv.newArray(RESULT_TYPE);
        mv.storeLocal(resLocal);
        return resLocal;
    }

    private static void pushArgumentOnStack(GeneratorAdapter mv, List<Object> objArgs, Object arg, Class<?> argClass) {
        if (argClass == boolean.class) {
            mv.push((boolean) arg);
        } else if (argClass == byte.class) {
            mv.push((byte) arg);
        } else if (argClass == char.class) {
            mv.push((char) arg);
        } else if (argClass == short.class) {
            mv.push((short) arg);
        } else if (argClass == int.class) {
            mv.push((int) arg);
        } else if (argClass == long.class) {
            mv.push((long) arg);
        } else if (argClass == float.class) {
            mv.push((float) arg);
        } else if (argClass == double.class) {
            mv.push((double) arg);
        } else if (argClass == String.class) {
            mv.push((String) arg);
        } else { // Object type
            mv.loadThis(); // -> this
            mv.getField(TEST_THREAD_EXECUTION_TYPE, "objArgs", OBJECT_ARRAY_TYPE); // this -> objArgs
            mv.push(objArgs.size()); // objArgs -> objArgs, index
            mv.arrayLoad(OBJECT_TYPE); // objArgs, index -> arg
            mv.checkCast(Type.getType(argClass)); // cast object to argument type
            objArgs.add(arg);
        }
    }
}
