/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package psy.lob.saw.queues.common;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

import psy.lob.saw.queues.lamport.LamportQueue1;
import psy.lob.saw.queues.ff.FastFlowQueue1;
import psy.lob.saw.queues.ff.FastFlowQueue2;
import psy.lob.saw.queues.lamport.LamportQueue2;
import psy.lob.saw.queues.lamport.LamportQueue3;
import psy.lob.saw.queues.lamport.LamportQueue4;
import psy.lob.saw.queues.thompson.ThompsonQueue1;
import psy.lob.saw.queues.thompson.ThompsonQueue2;
import psy.lob.saw.queues.thompson.ThompsonQueue3;
import psy.lob.saw.queues.lamport.LamportQueue5;

public final class SPSCQueueFactory {

    public static Queue<Integer> createQueue(int qId, int qScale) {
        int qCapacity = 1 << qScale;
        switch (qId) {
        case 11:
            return new ArrayBlockingQueue<Integer>(qCapacity);
        case 12:
            return new ConcurrentLinkedQueue<Integer>();
        case 21:
            return new LamportQueue1<Integer>(qCapacity);
        case 22:
            return new LamportQueue2<Integer>(qCapacity);
        case 23:
            return new LamportQueue3<Integer>(qCapacity);
        case 24:
            return new LamportQueue4<Integer>(qCapacity);
        case 25:
            return new LamportQueue5<Integer>(qCapacity);
        case 31:
            return new ThompsonQueue1<Integer>(qCapacity);
        case 32:
            return new ThompsonQueue2<Integer>(qCapacity);
        case 33:
            return new ThompsonQueue3<Integer>(qCapacity);
        case 41:
            return new FastFlowQueue1<Integer>(qCapacity);
        case 42:
            return new FastFlowQueue2<Integer>(qCapacity);
        default:
            throw new IllegalArgumentException("Invalid option: " + qId);
        }
    }

}
