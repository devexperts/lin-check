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

package z.offheap.buffer;

/**
 */
public interface LittleEndianOrderBuffer extends ByteBuffer {
  short readShortLE();

  int readIntLE();

  long readLongLE();

  char readCharLE();

  float readFloatLE();

  double readDoubleLE();

  LittleEndianOrderBuffer writeShortLE(short value);

  LittleEndianOrderBuffer writeIntLE(int value);

  LittleEndianOrderBuffer writeLongLE(long value);

  LittleEndianOrderBuffer writeCharLE(char value);

  LittleEndianOrderBuffer writeFloatLE(float value);

  LittleEndianOrderBuffer writeDoubleLE(double value);

  LittleEndianOrderBuffer write(byte value);
}
