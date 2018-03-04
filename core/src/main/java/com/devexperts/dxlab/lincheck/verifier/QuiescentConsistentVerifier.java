package com.devexperts.dxlab.lincheck.verifier;

/*
 * #%L
 * core
 * %%
 * Copyright (C) 2015 - 2017 Devexperts, LLC
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 * 
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import com.devexperts.dxlab.lincheck.Actor;
import com.devexperts.dxlab.lincheck.Result;

import java.lang.reflect.Method;
import java.util.List;

public class QuiescentConsistentVerifier extends Verifier {
    public QuiescentConsistentVerifier(List<List<Actor>> actorsPerThread, Object testInstance, Method resetMethod) {
        super(actorsPerThread, testInstance, resetMethod);
    }

    @Override
    public void verifyResults(List<List<Result>> results) {
        System.out.println("QuiescentConsistentVerifier start");
    }
}
