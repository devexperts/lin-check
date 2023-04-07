/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package romix.scala.collection.concurrent;

import java.util.Map;

/***
 * Helper class simulating a tuple of 2 elements in Scala
 * 
 * @author Roman Levenstein <romixlev@gmail.com>
 *
 * @param <K>
 * @param <V>
 */
public class Pair<K, V> implements Map.Entry<K, V> {

    final K k;
    final V v;

    Pair (K k, V v) {
        this.k = k;
        this.v = v;
    }

    @Override
    public K getKey () {
        // TODO Auto-generated method stub
        return k;
    }

    @Override
    public V getValue () {
        // TODO Auto-generated method stub
        return v;
    }

    @Override
    public V setValue (V value) {
        throw new RuntimeException ("Operation not supported");
    }

}
