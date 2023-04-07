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

package z.function;

import java.util.function.BooleanSupplier;

/**
 * Static utility methods kinds of Funcitons .
 *
 */
public class Functions {

    private Functions() {}

    /**
     * Returns a function whose {@code apply} method returns the provided
     * input. Useful when a {@code Function<T,R>} is required and {@code <T>}
     * and {@code <U>} are the same type.
     */
    public static BooleanSupplier toBooleanSupplier(ToBooleanFunction0 f) {
        return () -> f.apply();
    }


}
