/*
 * Copyright 2012 Real Logic Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package psy.lob.saw.queues.thompson;

import java.util.Iterator;

import psy.lob.saw.queues.lamport.VolatileLongCell;
import psy.lob.saw.queues.common.CircularArrayQueue3;

/**
 * <ul>
 * <li>Lock free, observing single writer principal (except for buffer).
 * <li>Replacing the long fields with AtomicLong and using lazySet instead of
 * volatile assignment.
 * <li>Using the power of 2 mask, forcing the capacity to next power of 2.
 * <li>Using a fully padded 'AtomicLong' like variable
 * <li>Fully padded circular array
 * <li>Use fully padded index cache fields
 * </ul>
 */
abstract class ThompsonQueue1Fields<E> extends CircularArrayQueue3<E> {
	protected final VolatileLongCell producerIndex = new VolatileLongCell();
	protected final VolatileLongCell consumerIndex = new VolatileLongCell();
	protected final LongCell producerIndexCache = new LongCell();
	protected final LongCell consumerIndexCache = new LongCell();

	public ThompsonQueue1Fields(int capacity) {
	    super(capacity);
    }
	
}
public final class ThompsonQueue1<E>  extends ThompsonQueue1Fields<E> {
	protected long p00, p01, p02, p03, p04, p05, p06, p07;
	protected long p10, p11, p12, p13, p14, p15, p16, p17;
	public ThompsonQueue1(final int capacity) {
		super(capacity);
	}
	private long lvProducerIndex() {
		return producerIndex.get();
	}

	private void soProducerIndex(long index) {
		producerIndex.lazySet(index);
	}

	private long lvConsumerIndex() {
		return consumerIndex.get();
	}

	private void soConsumerIndex(long index) {
		consumerIndex.lazySet(index);
	}

	private long lpConsumerIndexCache() {
		return consumerIndexCache.get();
	}

	private void spConsumerIndexCache(long index) {
		consumerIndexCache.set(index);
	}

	private long lpProducerIndexCache() {
		return producerIndexCache.get();
	}

	private void spProducerIndexCache(long index) {
		producerIndexCache.set(index);
	}

	@Override
	public boolean offer(final E e) {
		if (null == e) {
			throw new NullPointerException("Null is not a valid element");
		}

		final long currentProducerIndex = lvProducerIndex(); // LoadLoad
		final long wrapPoint = currentProducerIndex - capacity();
		if (lpConsumerIndexCache() <= wrapPoint) {
			spConsumerIndexCache(lvConsumerIndex()); // LoadLoad
			if (lpConsumerIndexCache() <= wrapPoint) {
				return false;
			}
		}

		final int offset = calcOffset(currentProducerIndex);
		spElement(offset, e);
		soProducerIndex(currentProducerIndex + 1); // StoreStore
		return true;
	}

	@Override
	public E poll() {
		final long currentConsumerIndex = lvConsumerIndex(); // LoadLoad
		if (currentConsumerIndex >= lpProducerIndexCache()) {
			spProducerIndexCache(lvProducerIndex()); // LoadLoad
			if (currentConsumerIndex >= lpProducerIndexCache()) {
				return null;
			}
		}

		final int offset = calcOffset(currentConsumerIndex);
		final E e = lpElement(offset);
		spElement(offset, null);
		soConsumerIndex(currentConsumerIndex + 1); // StoreStore
		return e;
	}

	@Override
	public E peek() {
		final int offset = calcOffset(lvConsumerIndex());
		return lpElement(offset);
	}

	@Override
	public int size() {
		return (int) (lvProducerIndex() - lvConsumerIndex());
	}

	@Override
	public Iterator<E> iterator() {
		throw new UnsupportedOperationException();
	}
}
