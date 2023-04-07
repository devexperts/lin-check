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
 * Created by jin on 12/27/13.
 */
public interface ByteBuffer extends AutoCloseable {

  /**
   * Returns this buffer's capacity.
   *
   * @return  The capacity of this buffer
   */
  long capacity();

  long address();

  ByteBuffer clear();

  long readCursor();

  long writeCursor();

  ByteBuffer reset();

  boolean isReadable();

  boolean isReadable(long numBytes);

  boolean isWritable();

  boolean isWritable(long numBytes);

  long readableBytes();

  long writableBytes();

  byte read();

  void skipRead(long length);

  void skipReadTo(long index);

  void readTo(ByteBuffer dstbuffer, long length);

  //FIXME: override to byte[] and nio buffer

  ByteBuffer write(byte value);

  NativeOrderBuffer nativeOrder();

  NetworkOrderBuffer networkOrder();

  LittleEndianOrderBuffer littleEndianOrder();

  @Deprecated
  ByteBuffer skipWrite(long length);

  @Deprecated
  ByteBuffer skipWriteTo(long index);

  void close();

//  @Override
//  void finalize();
}
