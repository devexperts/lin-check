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
 * Mimic Option in Scala
 *  
 * @author Roman Levenstein <romixlev@gmail.com>
 *
 * @param <V>
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class Option<V> {
    static None none = new None();
    public static <V> Option<V> makeOption(V o){
        if(o!=null)
            return new Some<V>(o);
        else
            return (Option<V>)none;
    }

    public static <V> Option<V> makeOption(){
        return (Option<V>)none;
    }
    public boolean nonEmpty () {
        return false;
    }
}
