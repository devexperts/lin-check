/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package psy.lob.saw.queues.thompson;

abstract class LongCellP0{long p0,p1,p2,p3,p4,p5,p6;}
abstract class LongCellValue extends LongCellP0 {
    protected long value;
}
public final class LongCell extends LongCellValue {
    long p10,p11,p12,p13,p14,p15,p16;
    public void set(long v){ this.value = v; }
    public long get(){ return this.value; }
}
