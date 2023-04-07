/**
 * Copyright 2013, Landz and its contributors. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package z.util.concurrent;

import java.util.function.Supplier;

import static z.util.Unsafes.currentThreadId;

/**
 */
public class ThreadLocalValueHolder<T> implements AutoCloseable {

  private static final int MAX_SUPPORTED_THREAD_ID = 1024;//FIXME

  //TODO: false sharing
  private final T[] tlValues = (T[])new Object[MAX_SUPPORTED_THREAD_ID];
  private final Supplier<T> supplier;//return supplier.get();

  public ThreadLocalValueHolder(Supplier<T> supplier) {
    this.supplier = supplier;
  }

  public T get() {
    int tid = (int)currentThreadId();
    //FIXME as contract
    if (tid==MAX_SUPPORTED_THREAD_ID)
      throw new IllegalStateException(
          "the supported threadId should be less than "
              +MAX_SUPPORTED_THREAD_ID);

    T rt = tlValues[tid];
    if (rt==null) {
      rt = supplier.get();
      tlValues[tid] = rt;
    }
    return rt;
  }

  @Override
  public void close() throws Exception {
    for (int i = 0; i < tlValues.length; i++) {
      T tlValue = tlValues[i];
      if (tlValue instanceof AutoCloseable)
        ((AutoCloseable)tlValue).close();
      tlValues[i] = null;
    }
  }
}
