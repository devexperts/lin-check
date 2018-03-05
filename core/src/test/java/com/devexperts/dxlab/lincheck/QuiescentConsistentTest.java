package com.devexperts.dxlab.lincheck;

import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.annotations.QuiescentConsistent;
import com.devexperts.dxlab.lincheck.annotations.Reset;

import com.devexperts.dxlab.lincheck.paramgen.IntGen;
import com.devexperts.dxlab.lincheck.stress.StressCTest;
import com.devexperts.dxlab.lincheck.verifier.LongExLinearizabilityVerifier;
import org.jctools.queues.MpscLinkedQueue8;
import org.junit.Test;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

@StressCTest(iterations = 1, invocationsPerIteration = 1,
        actorsPerThread = {"50:50", "50:50"})
@Param(name = "value", gen = IntGen.class)
public class QuiescentConsistentTest {
    private Queue<Integer> m;

    @Reset
    public void reload() {
        m = new MpscLinkedQueue8<>();
    }

    @Operation(params = {"value"})
    public boolean put(Integer value) {
        return m.offer(value);
    }

    @Operation
    public Integer get() {
        return m.element();
    }

    @Test
    public void test() throws Exception {
        LinChecker.check(QuiescentConsistentTest.class);
    }
}