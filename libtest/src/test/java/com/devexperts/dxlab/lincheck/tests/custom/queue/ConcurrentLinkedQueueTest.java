package leti._3303.faber;

import com.devexperts.dxlab.lincheck.LinChecker;
import com.devexperts.dxlab.lincheck.annotations.Operation;
import com.devexperts.dxlab.lincheck.annotations.Param;
import com.devexperts.dxlab.lincheck.paramgen.IntGen;
import com.devexperts.dxlab.lincheck.strategy.stress.StressCTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

@RunWith(Parameterized.class)
public class ConcurrentLinkedQueueTest {
    private static ArrayList<Integer> initialValues;


    public ConcurrentLinkedQueueTest(ArrayList<Integer> aL, String desc) {
        ConcurrentLinkedQueueTest.initialValues = aL;
    }

    private static ArrayList<Integer> generateAL(int bound){
        Random random = new Random();
        ArrayList<Integer> newAL = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            newAL.add(random.nextInt(bound));
        }
        return newAL;
    }


    @Parameterized.Parameters(name = "{1}")
    public static Collection<Object[]> testConditions() {
        Object[][] data = new Object[][]{
                {generateAL(10), "tens"},
                {generateAL(100), "hundreds"},
                {generateAL(1000), "thousands"}
        };
        return Arrays.asList(data);

    }

    @StressCTest(threads = 3)
    @Param(name = "newValue", gen = IntGen.class, conf = "1:10")
    public static class ConcurrentLinkedQueueLinTest {
        private ConcurrentLinkedQueue<Integer> cLQ = new ConcurrentLinkedQueue<>(initialValues);;


        @Operation
        public void offer(@Param(name = "newValue") int newItem) {
            cLQ.offer(newItem);
        }

        @Operation
        public Integer poll() {
            return cLQ.poll();
        }

        @Operation
        public Integer peek() {
            return cLQ.peek();
        }

        @Operation
        public boolean isEmpty() {
            return cLQ.isEmpty();
        }

    }

    @Test
    public void runner() throws Exception {
        LinChecker.check(ConcurrentLinkedQueueLinTest.class);
    }

}
