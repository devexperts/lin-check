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

/**
 * Function of 8 parameters.
 * (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R
 * @param <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R>
 */
@FunctionalInterface
public interface Function11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> {

    /**
     * Return the result of applying the lambda.
     *
     * @return the function result
     */
    R apply(T1 v1, T2 v2, T3 v3, T4 v4, T5 v5, T6 v6, T7 v7, T8 v8, T9 v9, T10 v10, T11 v11);

}
