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

import java.util.AbstractQueue;

abstract class CircularArrayQueue4PrePad<E> extends AbstractQueue<E> {
    protected long p00, p01, p02, p03, p04, p05, p06, p07;
	protected long p10, p11, p12, p13, p14, p15, p16, p17;
}
public abstract class CircularArrayQueue4<E> extends CircularArrayQueue4PrePad<E> {
	private static final int BUFFER_PAD = 32;
	protected static final long ARRAY_BASE;
	protected static final int ELEMENT_SHIFT;
	static {
        final int scale = UnsafeAccess.UNSAFE.arrayIndexScale(Object[].class);

        if (4 == scale) {
            ELEMENT_SHIFT = 2;
        } else if (8 == scale) {
            ELEMENT_SHIFT = 3;
        } else {
            throw new IllegalStateException("Unknown pointer size");
        }
        ARRAY_BASE = UnsafeAccess.UNSAFE.arrayBaseOffset(Object[].class) + (BUFFER_PAD << ELEMENT_SHIFT);
	}
	private final int capacity;
	private final long mask;
	private final E[] buffer;

	@SuppressWarnings("unchecked")
	public CircularArrayQueue4(int capacity) {
		this.capacity = Pow2.findNextPositivePowerOfTwo(capacity);
		mask = capacity() - 1;
		// padding + size + padding
        buffer = (E[]) new Object[this.capacity + BUFFER_PAD * 2];
	}

	protected final void spElement(final long offset, final E e) {
		UnsafeAccess.UNSAFE.putObject(buffer, offset, e);
	}

	@SuppressWarnings("unchecked")
	protected final E lpElement(final long offset) {
		return (E) UnsafeAccess.UNSAFE.getObject(buffer, offset);
	}
	protected final void soElement(final long offset, final E e) {
		UnsafeAccess.UNSAFE.putOrderedObject(buffer, offset, e);
	}

	@SuppressWarnings("unchecked")
	protected final E lvElement(final long offset) {
		return (E) UnsafeAccess.UNSAFE.getObjectVolatile(buffer, offset);
	}
	protected final long calcOffset(final long index) {
		// inclusive of padding:
		// array base + (padding * slot size) + ((index % capacity) * (slot size)) =
		// ARRAY_BASE pre-calculated: ARRAY_BASE + ((index % capacity) * (slot size)) =
		// capacity is power of 2: ARRAY_BASE + ((index & mask) * (slot size)) =
		// slot size is a power of 2, replace with a shift of pre-calculated ELEMENT_SHIFT
		return ARRAY_BASE + ((index & mask) << ELEMENT_SHIFT);
	}

	protected final int capacity() {
		return capacity;
	}
}
