/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package com.devexperts.dxlab.lincheck.strategy;

import com.devexperts.jagent.ClassInfo;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.JSRInlinerAdapter;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.commons.TryCatchBlockSorter;

import java.util.ArrayList;
import java.util.List;

/**
 * This transformer inserts {@link ManagedStrategy}' methods invocations.
 */
class ManagedStrategyTransformer extends ClassVisitor {
    private static final int ASM_API = Opcodes.ASM5;

    private static final Type[] NO_ARGS = new Type[]{};

    private static final Type OBJECT_TYPE = Type.getType(Object.class);
    private static final Type MANAGED_STRATEGY_HOLDER_TYPE = Type.getType(ManagedStrategyHolder.class);
    private static final Type MANAGED_STRATEGY_TYPE = Type.getType(ManagedStrategy.class);

    private static final Method CURRENT_THREAD_NUMBER_METHOD = new Method("currentThreadNumber", Type.INT_TYPE, NO_ARGS);
    private static final Method BEFORE_SHARED_VARIABLE_READ_METHOD = new Method("beforeSharedVariableRead", Type.VOID_TYPE, new Type[]{Type.INT_TYPE, Type.INT_TYPE});
    private static final Method BEFORE_SHARED_VARIABLE_WRITE_METHOD = new Method("beforeSharedVariableWrite", Type.VOID_TYPE, new Type[]{Type.INT_TYPE, Type.INT_TYPE});
    private static final Method BEFORE_LOCK_ACQUIRE_METHOD = new Method("beforeLockAcquire", Type.VOID_TYPE, new Type[]{Type.INT_TYPE, Type.INT_TYPE, OBJECT_TYPE});
    private static final Method AFTER_LOCK_RELEASE_METHOD = new Method("afterLockRelease", Type.VOID_TYPE, new Type[]{Type.INT_TYPE, Type.INT_TYPE, OBJECT_TYPE});

    private String className;
    private String fileName;
    private final List<StackTraceElement> codeLocations = new ArrayList<>();

    public ManagedStrategyTransformer(ClassVisitor cv, ClassInfo ci) {
        super(ASM_API, cv);
        this.className = ci.getClassName();
        this.fileName = ci.getSourceFile();
    }

    public List<StackTraceElement> getCodeLocations() {
        return codeLocations;
    }

    @Override
    public MethodVisitor visitMethod(int access, String mname, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, mname, desc, signature, exceptions);
        mv = new JSRInlinerAdapter(mv, access, mname, desc, signature, exceptions);
        mv = new SharedVariableAccessMethodTransformer(mname, new GeneratorAdapter(mv, access, mname, desc));
        // TODO add other transformers
        mv = new TryCatchBlockSorter(mv, access, mname, desc, signature, exceptions);
        return mv;
    }

    class SharedVariableAccessMethodTransformer extends ManagedStrategyMethodVisitor {
        public SharedVariableAccessMethodTransformer(String methodName, GeneratorAdapter mv) {
            super(methodName, mv);
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            switch (opcode) {
            case Opcodes.GETSTATIC:
            case Opcodes.GETFIELD:
                invokeBeforeSharedVariableRead();
                break;
            case Opcodes.PUTSTATIC:
            case Opcodes.PUTFIELD:
                invokeBeforeSharedVariableWrite();
                break;
            }
            super.visitFieldInsn(opcode, owner, name, desc);
        }

        @Override
        public void visitInsn(int opcode) {
            switch (opcode) {
            case Opcodes.AALOAD:
            case Opcodes.LALOAD:
            case Opcodes.FALOAD:
            case Opcodes.DALOAD:
            case Opcodes.IALOAD:
                invokeBeforeSharedVariableRead();
                break;
            case Opcodes.AASTORE:
            case Opcodes.IASTORE:
            case Opcodes.LASTORE:
            case Opcodes.FASTORE:
            case Opcodes.DASTORE:
                invokeBeforeSharedVariableWrite();
                break;
            }
            super.visitInsn(opcode);
        }
    }

    class ManagedStrategyMethodVisitor extends MethodVisitor {
        private final String methodName;
        protected final GeneratorAdapter mv;

        private int lineNumber;

        ManagedStrategyMethodVisitor(String methodName, GeneratorAdapter mv) {
            super(ASM_API, mv);
            this.methodName = methodName;
            this.mv = mv;
        }

        void invokeBeforeSharedVariableRead() {
            invokeOnSharedVariableAccess(BEFORE_SHARED_VARIABLE_READ_METHOD);
        }

        void invokeBeforeSharedVariableWrite() {
            invokeOnSharedVariableAccess(BEFORE_SHARED_VARIABLE_WRITE_METHOD);
        }

        private void invokeOnSharedVariableAccess(Method method) {
            loadStrategy();
            loadCurrentThreadNumber();
            loadNewCodeLocation();
            mv.invokeVirtual(MANAGED_STRATEGY_TYPE, method);
        }

        // STACK: monitor
        void invokeBeforeLockAcquire() {
            invokeOnLockAcquireOrRelease(BEFORE_LOCK_ACQUIRE_METHOD);
        }

        // STACK: monitor
        void invokeAfterLockRelease() {
            invokeOnLockAcquireOrRelease(AFTER_LOCK_RELEASE_METHOD);
        }

        // STACK: monitor
        private void invokeOnLockAcquireOrRelease(Method method) {
            int monitorLocal = mv.newLocal(OBJECT_TYPE);
            mv.dup();
            mv.storeLocal(monitorLocal);
            loadCurrentThreadNumber();
            loadNewCodeLocation();
            mv.loadLocal(monitorLocal);
            mv.invokeVirtual(MANAGED_STRATEGY_TYPE, method);
        }

        void loadStrategy() {
            mv.getStatic(MANAGED_STRATEGY_HOLDER_TYPE, "strategy", MANAGED_STRATEGY_TYPE);
        }

        void loadCurrentThreadNumber() {
            loadStrategy();
            mv.invokeVirtual(MANAGED_STRATEGY_TYPE, CURRENT_THREAD_NUMBER_METHOD);
        }

        void loadNewCodeLocation() {
            int codeLocation = codeLocations.size();
            codeLocations.add(new StackTraceElement(className, methodName, fileName, lineNumber));
            mv.push(codeLocation);
        }

        @Override
        public void visitLineNumber(int line, Label start) {
            this.lineNumber = line;
            super.visitLineNumber(line, start);
        }
    }
}
