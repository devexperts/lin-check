/*
 * Lincheck - Linearizability checker
 *
 * Copyright (C) 2015-2022 Devexperts, LLC
 * Copyright (C) 2023 Devexperts Ireland Limited
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

package tests.custom.transfer;

import java.util.HashMap;
import java.util.Map;

public class AccountsWrong2 implements Accounts {

    Map<Integer, Integer> data;

    public AccountsWrong2() {
        data = new HashMap<>();
    }

    @Override
    public Integer getAmount(int id) {
        if (data.containsKey(id)) {
            return data.get(id);
        } else {
            return 0;
        }
    }

    @Override
    public synchronized void setAmount(int id, int value) {
        data.put(id, value);
    }

    @Override
    public  void transfer(int id1, int id2, int value) {
        if (id1 == id2) return;
        Integer v1 = data.get(id1);
        Integer v2 = data.get(id2);
        if (v1 == null) v1 = 0;
        if (v2 == null) v2 = 0;
        v1 -= value;
        v2 += value;
        data.put(id1, v1);
//        try {
//            Thread.sleep(10);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        data.put(id2, v2);
    }

    @Override
    public String toString() {
        return "AccountsSynchronized{" +
                "data=" + data +
                '}';
    }
}
