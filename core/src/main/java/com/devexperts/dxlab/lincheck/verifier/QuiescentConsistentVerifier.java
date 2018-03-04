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
import java.util.ArrayList;
import java.util.List;

public class QuiescentConsistentVerifier {
    private final LongExLinearizabilityVerifier verifier;
    public QuiescentConsistentVerifier(List<List<Actor>> actorsPerThread, Object testInstance, Method resetMethod) {
        //super(actorsPerThread, testInstance, resetMethod);
        verifier = new LongExLinearizabilityVerifier(actorsPerThread, testInstance, resetMethod);
    }

    public void verifyResults(List<List<Result>> results) {
        /**
         * TODO: формирование List<List<Result>> для каждого промежутка между периодами покоя
         * и вызов для каждой этой структуры LongExLinearizabilityVerifier.verifyResults
        */
        List<List<List<Result>>> possibleResults = generationResultsWithRestPeriods(results);
        possibleResults.forEach(res -> verifier.verifyResults(res));
    }

    private List<List<List<Result>>> generationResultsWithRestPeriods(List<List<Result>> results) {
        List<List<List<Result>>> possibleResults = new ArrayList<>();

        List<List<Result>> resultPiece = new ArrayList<>(results);

        for (int i = 0; i < resultPiece.size(); ++i) {
            if (!resultPiece.isEmpty()) {
                Pair period = creatingGap(resultPiece);
                //TODO: добавить фильтрацию по периоду
                //possibleResults.add(clearPeriod(period, resultPiece));
            }
        }

        return possibleResults;
    }

    /**
     * создание промежутка между сосостояниями покоя
     * @return List<Pair> список проверенных методов
     */
    private Pair creatingGap(List<List<Result>> results) {
        long startMethodTime = 0;
        for (int i = 0; i < results.size(); ++i) {
            if (results.get(i).isEmpty()) {
                continue;
            }
            else {
                startMethodTime = results.get(i).get(0).getStartCallTime();
                break;
            }
        }

        int indexStream = 0;

        for (int i = 0; i < results.size(); i++) {
            if (results.get(i).isEmpty())
                continue;
            if (results.get(i).get(0).getStartCallTime() < startMethodTime)
            {
                startMethodTime = results.get(i).get(0).getStartCallTime();
                indexStream = i;
            }
        }

        boolean flag = false; // условие просмотра метода
        long endMethodTime = results.get(indexStream).get(0).getEndCallTime();
        //конечный результат старта (самое левое время НЕ периода покоя
        long startMethodTimeResult = startMethodTime;

        List<Pair> viewedMethods = new ArrayList<>(); //просмотренные результаты
        viewedMethods.add(new Pair(indexStream, 0));

        for (int i = 0; i < results.size(); ++i) {
            if (i == indexStream) {
                continue;
            }

            for (int j = 0; j < results.get(i).size(); ++j) {
                for (int k = 0; k < viewedMethods.size(); ++k) {
                    if (i == viewedMethods.get(k).getFirst() && j == viewedMethods.get(k).getSecond()) {
                        flag = true;
                        break;
                    }
                }

                if (flag) {
                    flag = false;
                    continue;
                }

                if (results.get(i).get(j).getStartCallTime() < endMethodTime) {
                    startMethodTime = results.get(i).get(j).getStartCallTime();
                    endMethodTime = results.get(i).get(j).getEndCallTime();
                    indexStream = i;

                    viewedMethods.add(new Pair(indexStream, j));
                    i = -1; //вечный цикл, пока не будет найден период покоя
                }
                break;
            }
        }

        return new Pair(startMethodTimeResult, endMethodTime);
    }
}

class Pair {
    private long first;
    private long second;

    Pair(long first, long second) {
        this.first = first;
        this.second = second;
    }

    public long getFirst() {
        return first;
    }

    public long getSecond() {
        return second;
    }

    public void setFirst(long first) {
        this.first = first;
    }

    public void setSecond(long second) {
        this.second = second;
    }
}
