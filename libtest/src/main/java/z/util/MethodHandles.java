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

package z.util;

import java.lang.reflect.Field;

import static java.lang.invoke.MethodHandles.Lookup;
import static z.util.Unsafes.UNSAFE;

/**
 * All APIs to expose MethodHandles side things in Hotspot for use.
 *
 * @Performance, we keep all exposed publics in constant as possible
 *
 */
public class MethodHandles {

    public static final Lookup LOOKUP;
    static {
        //XXX: hack to be GOD?...
        Field f_IMPL_LOOKUP = Throwables.uncheckTo(() -> Lookup.class.getDeclaredField("IMPL_LOOKUP"));
        Object base = UNSAFE.staticFieldBase(Lookup.class);
        long offset = UNSAFE.staticFieldOffset(f_IMPL_LOOKUP);
        LOOKUP = (Lookup)UNSAFE.getObject(base, offset);
    }

}
