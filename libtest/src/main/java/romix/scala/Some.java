/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package romix.scala;

/**
 * Mimic Some in Scala
 *  
 * @author Roman Levenstein <romixlev@gmail.com>
 *
 * @param <V>
 */
public class Some<V> extends Option<V> {
    final V value;
    public Some(V v) {
        value = v;
    }
    
    public V get() {
        return value;
    }
    
    public boolean nonEmpty () {
        return value != null;
    }
}
