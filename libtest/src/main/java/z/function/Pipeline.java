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

import java.util.function.Function;

/**
 *
 * TODO: the logic of type inferencing still has some flaws, although works
 *       basically.
 *
 */
public class Pipeline<IN, OUT> implements Function<IN, OUT> {

  Pipe<IN, ?> head;
  Pipe tail;

  private Pipeline(Pipe<IN,?> head) {
    this.head = head;
    this.tail = head;
  }

  public static <IN, R> Pipeline<R,?> create(Function<IN,R> head){
    //TODO contract(head!=null)
    return new Pipeline(new Pipe(head));
  }

  public <T,R> Pipeline<R,?> next(Function<T,R> f) {
    Pipe np = new Pipe(f);
    tail.next(np);
    tail = np;
    return (Pipeline<R,?>)this;
  }

  public <T, OUT> Pipeline<T, OUT> end() {
    return (Pipeline<T, OUT>)this;
  }

  @Override
  public OUT apply(IN in) {
    Pipe n = head;
    Object r = n.function.apply(in);
    while ( (n = n.next)!=null ) {
      r = n.function.apply(r);
    }
    return (OUT)r;
  }
}
