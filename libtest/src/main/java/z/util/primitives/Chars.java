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

package z.util.primitives;

import java.io.Serializable;
import java.util.*;

/**
 * modified by:
 * @auther Landz's contributors
 */

/**
 * Static utility methods pertaining to {@code char} primitives, that are not
 * already found in either {@link Character} or {@link java.util.Arrays}.
 *
 * <p>All the operations in this class treat {@code char} values strictly
 * numerically; they are neither Unicode-aware nor locale-dependent.
 *
 * <p>See the Guava User Guide article on <a href=
 * "http://code.google.com/p/guava-libraries/wiki/PrimitivesExplained">
 * primitive utilities</a>.
 *
 * @author Kevin Bourrillion
 * @since 1.0
 */
public final class Chars {
  private Chars() {}

  /**
   * The number of bytes required to represent a primitive {@code char}
   * value.
   */
  public static final int BYTES = Character.SIZE / Byte.SIZE;

  /**
   * Returns a hash code for {@code value}; equal to the result of invoking
   * {@code ((Character) value).hashCode()}.
   *
   * @param value a primitive {@code char} value
   * @return a hash code for the value
   */
  public static int hashCode(char value) {
    return value;
  }

  /**
   * Returns the {@code char} value that is equal to {@code value}, if possible.
   *
   * @param value any value in the range of the {@code char} type
   * @return the {@code char} value that equals {@code value}
   * @throws IllegalArgumentException if {@code value} is greater than {@link
   *     Character#MAX_VALUE} or less than {@link Character#MIN_VALUE}
   */
  public static char checkedCast(long value) {
    char result = (char) value;
    if (result != value) {
      // don't use checkArgument here, to avoid boxing
      throw new IllegalArgumentException("Out of range: " + value);
    }
    return result;
  }

  /**
   * Returns the {@code char} nearest in value to {@code value}.
   *
   * @param value any {@code long} value
   * @return the same value cast to {@code char} if it is in the range of the
   *     {@code char} type, {@link Character#MAX_VALUE} if it is too large,
   *     or {@link Character#MIN_VALUE} if it is too small
   */
  public static char saturatedCast(long value) {
    if (value > Character.MAX_VALUE) {
      return Character.MAX_VALUE;
    }
    if (value < Character.MIN_VALUE) {
      return Character.MIN_VALUE;
    }
    return (char) value;
  }

  /**
   * Compares the two specified {@code char} values. The sign of the value
   * returned is the same as that of {@code ((Character) a).compareTo(b)}.
   *
   * <p><b>Note:</b> projects using JDK 7 or later should use the equivalent
   * {@link Character#compare} method instead.
   *
   * @param a the first {@code char} to compare
   * @param b the second {@code char} to compare
   * @return a negative value if {@code a} is less than {@code b}; a positive
   *     value if {@code a} is greater than {@code b}; or zero if they are equal
   */
  // TODO(kevinb): if JDK 6 ever becomes a non-concern, remove this
  public static int compare(char a, char b) {
    return a - b; // safe due to restricted range
  }

  /**
   * Returns {@code true} if {@code target} is present as an element anywhere in
   * {@code array}.
   *
   * @param array an array of {@code char} values, possibly empty
   * @param target a primitive {@code char} value
   * @return {@code true} if {@code array[i] == target} for some value of {@code
   *     i}
   */
  public static boolean contains(char[] array, char target) {
    for (char value : array) {
      if (value == target) {
        return true;
      }
    }
    return false;
  }

  /**
   * Returns the index of the first appearance of the value {@code target} in
   * {@code array}.
   *
   * @param array an array of {@code char} values, possibly empty
   * @param target a primitive {@code char} value
   * @return the least index {@code i} for which {@code array[i] == target}, or
   *     {@code -1} if no such index exists.
   */
  public static int indexOf(char[] array, char target) {
    return indexOf(array, target, 0, array.length);
  }

  // TODO(kevinb): consider making this public
  private static int indexOf(
      char[] array, char target, int start, int end) {
    for (int i = start; i < end; i++) {
      if (array[i] == target) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Returns the start position of the first occurrence of the specified {@code
   * target} within {@code array}, or {@code -1} if there is no such occurrence.
   *
   * <p>More formally, returns the lowest index {@code i} such that {@code
   * java.util.Arrays.copyOfRange(array, i, i + target.length)} contains exactly
   * the same elements as {@code target}.
   *
   * @param array the array to search for the sequence {@code target}
   * @param target the array to search for as a sub-sequence of {@code array}
   */
  public static int indexOf(char[] array, char[] target) {
    if (target.length == 0) {
      return 0;
    }

    outer:
    for (int i = 0; i < array.length - target.length + 1; i++) {
      for (int j = 0; j < target.length; j++) {
        if (array[i + j] != target[j]) {
          continue outer;
        }
      }
      return i;
    }
    return -1;
  }

  /**
   * Returns the index of the last appearance of the value {@code target} in
   * {@code array}.
   *
   * @param array an array of {@code char} values, possibly empty
   * @param target a primitive {@code char} value
   * @return the greatest index {@code i} for which {@code array[i] == target},
   *     or {@code -1} if no such index exists.
   */
  public static int lastIndexOf(char[] array, char target) {
    return lastIndexOf(array, target, 0, array.length);
  }

  // TODO(kevinb): consider making this public
  private static int lastIndexOf(
      char[] array, char target, int start, int end) {
    for (int i = end - 1; i >= start; i--) {
      if (array[i] == target) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Returns the least value present in {@code array}.
   *
   * @param array a <i>nonempty</i> array of {@code char} values
   * @return the value present in {@code array} that is less than or equal to
   *     every other value in the array
   * @throws IllegalArgumentException if {@code array} is empty
   */
  public static char min(char... array) {
    char min = array[0];
    for (int i = 1; i < array.length; i++) {
      if (array[i] < min) {
        min = array[i];
      }
    }
    return min;
  }

  /**
   * Returns the greatest value present in {@code array}.
   *
   * @param array a <i>nonempty</i> array of {@code char} values
   * @return the value present in {@code array} that is greater than or equal to
   *     every other value in the array
   * @throws IllegalArgumentException if {@code array} is empty
   */
  public static char max(char... array) {
    char max = array[0];
    for (int i = 1; i < array.length; i++) {
      if (array[i] > max) {
        max = array[i];
      }
    }
    return max;
  }

  /**
   * Returns the values from each provided array combined into a single array.
   * For example, {@code concat(new char[] {a, b}, new char[] {}, new
   * char[] {c}} returns the array {@code {a, b, c}}.
   *
   * @param arrays zero or more {@code char} arrays
   * @return a single array containing all the values from the source arrays, in
   *     order
   */
  public static char[] concat(char[]... arrays) {
    int length = 0;
    for (char[] array : arrays) {
      length += array.length;
    }
    char[] result = new char[length];
    int pos = 0;
    for (char[] array : arrays) {
      System.arraycopy(array, 0, result, pos, array.length);
      pos += array.length;
    }
    return result;
  }

  /**
   * Returns a big-endian representation of {@code value} in a 2-element byte
   * array; equivalent to {@code
   * ByteBuffer.allocate(2).putChar(value).array()}.  For example, the input
   * value {@code '\\u5432'} would yield the byte array {@code {0x54, 0x32}}.
   *
   */
  public static byte[] toByteArray(char value) {
    return new byte[] {
        (byte) (value >> 8),
        (byte) value};
  }

  /**
   * Returns the {@code char} value whose big-endian representation is
   * stored in the first 2 bytes of {@code bytes}; equivalent to {@code
   * ByteBuffer.wrap(bytes).getChar()}. For example, the input byte array
   * {@code {0x54, 0x32}} would yield the {@code char} value {@code '\\u5432'}.
   *
   * <p>Arguably, it's preferable to use {@link java.nio.ByteBuffer}; that
   * library exposes much more flexibility at little cost in readability.
   *
   * @throws IllegalArgumentException if {@code bytes} has fewer than 2
   *     elements
   */
  public static char fromByteArray(byte[] bytes) {
    return fromBytes(bytes[0], bytes[1]);
  }

  /**
   * Returns the {@code char} value whose byte representation is the given 2
   * bytes, in big-endian order; equivalent to {@code Chars.fromByteArray(new
   * byte[] {b1, b2})}.
   *
   * @since 7.0
   */
  public static char fromBytes(byte b1, byte b2) {
    return (char) ((b1 << 8) | (b2 & 0xFF));
  }

  /**
   * Returns an array containing the same values as {@code array}, but
   * guaranteed to be of a specified minimum length. If {@code array} already
   * has a length of at least {@code minLength}, it is returned directly.
   * Otherwise, a new array of size {@code minLength + padding} is returned,
   * containing the values of {@code array}, and zeroes in the remaining places.
   *
   * @param array the source array
   * @param minLength the minimum length the returned array must guarantee
   * @param padding an extra amount to "grow" the array by if growth is
   *     necessary
   * @throws IllegalArgumentException if {@code minLength} or {@code padding} is
   *     negative
   * @return an array containing the values of {@code array}, with guaranteed
   *     minimum length {@code minLength}
   */
  public static char[] ensureCapacity(
      char[] array, int minLength, int padding) {
    return (array.length < minLength)
        ? copyOf(array, minLength + padding)
        : array;
  }

  // Arrays.copyOf() requires Java 6
  private static char[] copyOf(char[] original, int length) {
    char[] copy = new char[length];
    System.arraycopy(original, 0, copy, 0, Math.min(original.length, length));
    return copy;
  }

  /**
   * Returns a string containing the supplied {@code char} values separated
   * by {@code separator}. For example, {@code join("-", '1', '2', '3')} returns
   * the string {@code "1-2-3"}.
   *
   * @param separator the text that should appear between consecutive values in
   *     the resulting string (but not at the start or end)
   * @param array an array of {@code char} values, possibly empty
   */
  public static String join(String separator, char... array) {
    int len = array.length;
    if (len == 0) {
      return "";
    }

    StringBuilder builder
        = new StringBuilder(len + separator.length() * (len - 1));
    builder.append(array[0]);
    for (int i = 1; i < len; i++) {
      builder.append(separator).append(array[i]);
    }
    return builder.toString();
  }

  /**
   * Returns a comparator that compares two {@code char} arrays
   * lexicographically. That is, it compares, using {@link
   * #compare(char, char)}), the first pair of values that follow any
   * common prefix, or when one array is a prefix of the other, treats the
   * shorter array as the lesser. For example,
   * {@code [] < ['a'] < ['a', 'b'] < ['b']}.
   *
   * <p>The returned comparator is inconsistent with {@link
   * Object#equals(Object)} (since arrays support only identity equality), but
   * it is consistent with {@link java.util.Arrays#equals(char[], char[])}.
   *
   * @see <a href="http://en.wikipedia.org/wiki/Lexicographical_order">
   *     Lexicographical order article at Wikipedia</a>
   * @since 2.0
   */
  public static Comparator<char[]> lexicographicalComparator() {
    return LexicographicalComparator.INSTANCE;
  }

  private enum LexicographicalComparator implements Comparator<char[]> {
    INSTANCE;

    @Override
    public int compare(char[] left, char[] right) {
      int minLength = Math.min(left.length, right.length);
      for (int i = 0; i < minLength; i++) {
        int result = Chars.compare(left[i], right[i]);
        if (result != 0) {
          return result;
        }
      }
      return left.length - right.length;
    }
  }

  /**
   * Copies a collection of {@code Character} instances into a new array of
   * primitive {@code char} values.
   *
   * <p>Elements are copied from the argument collection as if by {@code
   * collection.toArray()}.  Calling this method is as thread-safe as calling
   * that method.
   *
   * @param collection a collection of {@code Character} objects
   * @return an array containing the same values as {@code collection}, in the
   *     same order, converted to primitives
   * @throws NullPointerException if {@code collection} or any of its elements
   *     is null
   */
  public static char[] toArray(Collection<Character> collection) {
    if (collection instanceof CharArrayAsList) {
      return ((CharArrayAsList) collection).toCharArray();
    }

    Object[] boxedArray = collection.toArray();
    int len = boxedArray.length;
    char[] array = new char[len];
    for (int i = 0; i < len; i++) {
      // checkNotNull for GWT (do not optimize)
      array[i] = (Character) boxedArray[i];
    }
    return array;
  }

  /**
   * Returns a fixed-size list backed by the specified array, similar to {@link
   * java.util.Arrays#asList(Object[])}. The list supports {@link List#set(int, Object)},
   * but any attempt to set a value to {@code null} will result in a {@link
   * NullPointerException}.
   *
   * <p>The returned list maintains the values, but not the identities, of
   * {@code Character} objects written to or read from it.  For example, whether
   * {@code list.get(0) == list.get(0)} is true for the returned list is
   * unspecified.
   *
   * @param backingArray the array to back the list
   * @return a list view of the array
   */
  public static List<Character> asList(char... backingArray) {
    if (backingArray.length == 0) {
      return Collections.emptyList();
    }
    return new CharArrayAsList(backingArray);
  }

  private static class CharArrayAsList extends AbstractList<Character>
      implements RandomAccess, Serializable {
    final char[] array;
    final int start;
    final int end;

    CharArrayAsList(char[] array) {
      this(array, 0, array.length);
    }

    CharArrayAsList(char[] array, int start, int end) {
      this.array = array;
      this.start = start;
      this.end = end;
    }

    @Override public int size() {
      return end - start;
    }

    @Override public boolean isEmpty() {
      return false;
    }

    @Override public Character get(int index) {
      return array[start + index];
    }

    @Override public boolean contains(Object target) {
      // Overridden to prevent a ton of boxing
      return (target instanceof Character)
          && Chars.indexOf(array, (Character) target, start, end) != -1;
    }

    @Override public int indexOf(Object target) {
      // Overridden to prevent a ton of boxing
      if (target instanceof Character) {
        int i = Chars.indexOf(array, (Character) target, start, end);
        if (i >= 0) {
          return i - start;
        }
      }
      return -1;
    }

    @Override public int lastIndexOf(Object target) {
      // Overridden to prevent a ton of boxing
      if (target instanceof Character) {
        int i = Chars.lastIndexOf(array, (Character) target, start, end);
        if (i >= 0) {
          return i - start;
        }
      }
      return -1;
    }

    @Override public Character set(int index, Character element) {
      char oldValue = array[start + index];
      array[start + index] = element;
      return oldValue;
    }

    @Override public List<Character> subList(int fromIndex, int toIndex) {
      int size = size();
      if (fromIndex == toIndex) {
        return Collections.emptyList();
      }
      return new CharArrayAsList(array, start + fromIndex, start + toIndex);
    }

    @Override public boolean equals(Object object) {
      if (object == this) {
        return true;
      }
      if (object instanceof CharArrayAsList) {
        CharArrayAsList that = (CharArrayAsList) object;
        int size = size();
        if (that.size() != size) {
          return false;
        }
        for (int i = 0; i < size; i++) {
          if (array[start + i] != that.array[that.start + i]) {
            return false;
          }
        }
        return true;
      }
      return super.equals(object);
    }

    @Override public int hashCode() {
      int result = 1;
      for (int i = start; i < end; i++) {
        result = 31 * result + Chars.hashCode(array[i]);
      }
      return result;
    }

    @Override public String toString() {
      StringBuilder builder = new StringBuilder(size() * 3);
      builder.append('[').append(array[start]);
      for (int i = start + 1; i < end; i++) {
        builder.append(", ").append(array[i]);
      }
      return builder.append(']').toString();
    }

    char[] toCharArray() {
      // Arrays.copyOfRange() is not available under GWT
      int size = size();
      char[] result = new char[size];
      System.arraycopy(array, start, result, 0, size);
      return result;
    }

    private static final long serialVersionUID = 0;
  }
}
