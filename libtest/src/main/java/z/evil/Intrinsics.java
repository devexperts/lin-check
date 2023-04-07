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

package z.evil;

public class Intrinsics {

  /**
   * Tricks:
   *   warmup several static methods around the whole Landz,
   *   to enable the early join of Hotspot intrinsics.
   *   This tricks is not much useful for long-run instances.
   *
   * NOTE: here, it is assumed that intrinsification is more early than
   *       other optimizations.
   */
  public static final void warmup() {
//    int COUNT = 5000_000;
//    for (int i = 0; i < COUNT; i++) {
//      Allocator.sizeClassIndex(i);
//    }
  }


}
