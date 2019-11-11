/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.bacta.engine.buffer;

import gnu.trove.list.TIntList;
import gnu.trove.list.TLongList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.list.array.TLongArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.bacta.engine.lang.UnicodeString;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings({"unused", "squid:S1319"})
public class BufferUtil {

    private BufferUtil() {}

    private static final Charset UTF_16LE = StandardCharsets.UTF_16LE;
    private static final Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;

    public static String bytesToHex(byte[] bytes, char seperator) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        int length = seperator != 0 ? 3 : 2;
        char[] hexChars = new char[(bytes.length * length)];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * length] = hexArray[v >>> 4];
            hexChars[j * length + 1] = hexArray[v & 0x0F];

            if (seperator != 0)
                hexChars[j * length + 2] = seperator;
        }
        return new String(hexChars);
    }

    public static String bytesToHex(byte[] bytes) {
        return bytesToHex(bytes, ' ');
    }

    public static String bytesToHex(ByteBuffer buffer, char seperator) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        int length = seperator != 0 ? 3 : 2;

        int bufferSize = buffer.limit();
        char[] hexChars = new char[(bufferSize * length)];
        int v;
        for (int j = 0; j < bufferSize; j++) {
            v = buffer.get(j) & 0xFF;
            hexChars[j * length] = hexArray[v >>> 4];
            hexChars[j * length + 1] = hexArray[v & 0x0F];

            if (seperator != 0)
                hexChars[j * length + 2] = seperator;
        }
        return new String(hexChars);
    }

    public static String bytesToAscii(byte[] bytes) {

        StringBuilder buffer = new StringBuilder();
        int v;
        for (byte aByte : bytes) {
            v = (aByte & 0xFF);
            if (v < 32) {
                v = 46;
            }
            if (v > 126) {
                v = 46;
            }
            buffer.append((char) v);

        }
        return buffer.toString();
    }

    public static String bytesToHex(ByteBuffer buffer) {
        return bytesToHex(buffer, ' ');
    }

    public static String bytesToHex(short[] bytes) {
        final char[] hexArray = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        char[] hexChars = new char[(bytes.length * 3)];
        int v;
        for (int j = 0; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 3] = hexArray[v >>> 4];
            hexChars[j * 3 + 1] = hexArray[v & 0x0F];
            hexChars[j * 3 + 2] = ' ';
        }
        return new String(hexChars);
    }

    public static boolean getBoolean(ByteBuffer buffer) {
        return buffer.get() == 1;
    }

    public static byte[] getByteArray(ByteBuffer buffer) {
        int length = buffer.getInt();
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return bytes;
    }

    public static void putByteArray(ByteBuffer buffer, byte[] bytes) {
        buffer.putInt(bytes.length);
        buffer.put(bytes);
    }

    public static String getAscii(ByteBuffer buffer) {
        short length = buffer.getShort();
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        return new String(bytes, ISO_8859_1);
    }

    public static void putAscii(ByteBuffer buffer, String value) {
        if (value != null) {
            buffer.putShort((short) value.length());
            buffer.put(value.getBytes(ISO_8859_1));
        } else {
            buffer.putShort((short) 0);
        }
    }

    public static void putUnicode(ByteBuffer buffer, String value) {
        if (value != null) {
            buffer.putInt(value.length());
            buffer.put(value.getBytes(UTF_16LE));
        } else {
            buffer.putInt(0);
        }
    }

    public static String getUnicode(ByteBuffer buffer) {
        int length = buffer.getInt();
        byte[] bytes = new byte[length * 2];
        buffer.get(bytes);
        return new String(bytes, UTF_16LE);
    }

    public static void putBinaryString(ByteBuffer buffer, String value) {
        if (value != null) {
            buffer.putInt(value.length());
            buffer.put(value.getBytes(ISO_8859_1));
        } else {
            buffer.putInt(0);
        }
    }

    public static String getBinaryString(ByteBuffer buffer) {
        int size = buffer.getInt();
        byte[] bytes = new byte[size];
        buffer.get(bytes);
        return new String(bytes);
    }

    public static String getNullTerminatedString(ByteBuffer buffer) {
        final StringBuilder builder = new StringBuilder();

        byte b;
        while ((b = buffer.get()) != 0)
            builder.append((char) b);

        return builder.toString();
    }

    /*
    public static Vec3 getVec3(ByteBuffer buffer) {
        float px = buffer.getFloat();
        float pz = buffer.getFloat();
        float py = buffer.getFloat();

        return new Vec3(px, pz, py);
    }

    public static Quat4f getQuat4f(ByteBuffer buffer) {
        float rx = buffer.getFloat();
        float ry = buffer.getFloat();
        float rz = buffer.getFloat();
        float rw = buffer.getFloat();

        return new Quat4f(rx, ry, rz, rw);
    }

    public static void put(ByteBuffer buffer, Vec3 vector) {
        if (vector != null) {
            buffer.putFloat(vector.x);
            buffer.putFloat(vector.z);
            buffer.putFloat(vector.y);
        } else {
            buffer.putFloat(0.f);
            buffer.putFloat(0.f);
            buffer.putFloat(0.f);
        }
    }

    public static void put(ByteBuffer buffer, Quat4f quaternion) {
        if (quaternion != null) {
            buffer.putFloat(quaternion.x); // X Direction
            buffer.putFloat(quaternion.y); // Y Direction
            buffer.putFloat(quaternion.z); // Z Direction
            buffer.putFloat(quaternion.w); // W Direction
        } else {
            buffer.putFloat(0.f);
            buffer.putFloat(0.f);
            buffer.putFloat(0.f);
            buffer.putFloat(1.f);
        }
    }

     */

    public static void put(ByteBuffer buffer, boolean value) {
        buffer.put(value ? (byte) 1 : (byte) 0);
    }

    public static void put(ByteBuffer buffer, byte value) {
        buffer.put(value);
    }

    public static void put(ByteBuffer buffer, short value) {
        buffer.putShort(value);
    }

    public static void put(ByteBuffer buffer, int value) {
        buffer.putInt(value);
    }

    public static void put(ByteBuffer buffer, long value) {
        buffer.putLong(value);
    }

    public static void put(ByteBuffer buffer, float value) {
        buffer.putFloat(value);
    }

    public static void put(ByteBuffer buffer, String value) {
        putAscii(buffer, value);
    }

    public static void put(ByteBuffer buffer, UnicodeString value) {
        putUnicode(buffer, value.getString());
    }

    public static void put(ByteBuffer buffer, ByteBufferWritable value) {
        value.writeToBuffer(buffer);
    }

    public static <K, V> HashMap<K, V> getHashMap(ByteBuffer buffer, Function<ByteBuffer, K> keyResolver, Function<ByteBuffer, V> valueResolver) {
        final int size = buffer.getInt();

        final HashMap<K, V> map = new HashMap<>(size);

        for (int i = 0; i < size; ++i) {
            final K key = keyResolver.apply(buffer);
            final V value = valueResolver.apply(buffer);
            map.put(key, value);
        }

        return map;
    }

    public static <V> TIntObjectHashMap<V> getTIntObjectHashMap(ByteBuffer buffer, Function<ByteBuffer, V> entryResolver) {
        final int size = buffer.getInt();

        final TIntObjectHashMap<V> map = new TIntObjectHashMap<>(size);

        for (int i = 0; i < size; ++i) {
            final int key = buffer.getInt();
            map.put(key, entryResolver.apply(buffer));
        }

        return map;
    }

    public static <V> TLongObjectHashMap<V> getTLongObjectHashMap(ByteBuffer buffer, Function<ByteBuffer, V> entryResolver) {
        final int size = buffer.getInt();

        final TLongObjectHashMap<V> map = new TLongObjectHashMap<>(size);

        for (int i = 0; i < size; ++i) {
            final long key = buffer.getLong();
            map.put(key, entryResolver.apply(buffer));
        }

        return map;
    }

    public static <T> ArrayList<T> getArrayList(ByteBuffer buffer, Function<ByteBuffer, T> valueResolver) {
        final int size = buffer.getInt();
        final ArrayList<T> list = new ArrayList<>(size);

        for (int i = 0; i < size; ++i)
            list.add(valueResolver.apply(buffer));

        return list;
    }

    public static <T> HashSet<T> getHashSet(ByteBuffer buffer, Function<ByteBuffer, T> valueResolver) {
        final int size = buffer.getInt();
        final HashSet<T> set = new HashSet<>(size);

        for (int i = 0; i < size; ++i)
            set.add(valueResolver.apply(buffer));

        return set;
    }

    public static <T> TreeSet<T> getTreeSet(ByteBuffer buffer, Function<ByteBuffer, T> valueResolver) {
        final int size = buffer.getInt();
        final TreeSet<T> set = new TreeSet<>();

        for (int i = 0; i < size; ++i)
            set.add(valueResolver.apply(buffer));

        return set;
    }

    public static TIntArrayList getTIntArrayList(ByteBuffer buffer) {
        final int size = buffer.getInt();
        final TIntArrayList list = new TIntArrayList(size);

        for (int i = 0; i < size; ++i)
            list.add(buffer.getInt());

        return list;
    }

    public static TLongArrayList getTLongArrayList(ByteBuffer buffer) {
        final int size = buffer.getInt();
        final TLongArrayList list = new TLongArrayList(size);

        for (int i = 0; i < size; ++i)
            list.add(buffer.getLong());

        return list;
    }

    public static <K extends ByteBufferWritable, V extends ByteBufferWritable> void put(ByteBuffer buffer, Map<K, V> map) {
        buffer.putInt(map.size());

        map.entrySet().stream().forEachOrdered(entry -> {
            put(buffer, entry.getKey());
            put(buffer, entry.getValue());
        });
    }

    public static <V extends ByteBufferWritable> void put(ByteBuffer buffer, TIntObjectMap<V> map) {
        buffer.putInt(map.size());

        map.forEachEntry((key, entry) -> {
            BufferUtil.put(buffer, key);
            put(buffer, entry);
            return true;
        });
    }

    public static <V> void put(ByteBuffer buffer, TIntObjectMap<V> map, BiConsumer<ByteBuffer, V> valueWriter) {
        buffer.putInt(map.size());

        map.forEachEntry((key, entry) -> {
            BufferUtil.put(buffer, key);
            valueWriter.accept(buffer, entry);
            return true;
        });
    }

    public static <V extends ByteBufferWritable> void put(ByteBuffer buffer, TLongObjectMap<V> map) {
        buffer.putInt(map.size());

        map.forEachEntry((key, entry) -> {
            BufferUtil.put(buffer, key);
            put(buffer, entry);
            return true;
        });
    }

    public static <T extends ByteBufferWritable> void put(ByteBuffer buffer, List<T> list) {
        buffer.putInt(list.size());
        list.stream().forEachOrdered(item -> put(buffer, item));
    }

    public static <T> void put(ByteBuffer buffer, List<T> list, BiConsumer<ByteBuffer, T> valueWriter) {
        buffer.putInt(list.size());
        list.stream().forEachOrdered(item -> valueWriter.accept(buffer, item));
    }

    public static <T extends ByteBufferWritable> void put(ByteBuffer buffer, Set<T> set) {
        buffer.putInt(set.size());
        set.stream().forEachOrdered(item -> put(buffer, item));
    }

    public static <T> void put(ByteBuffer buffer, Set<T> set, BiConsumer<ByteBuffer, T> valueWriter) {
        buffer.putInt(set.size());
        set.stream().forEachOrdered(item -> valueWriter.accept(buffer, item));
    }

    public static void put(ByteBuffer buffer, TIntList list) {
        buffer.putInt(list.size());

        list.forEach(item -> {
            BufferUtil.put(buffer, item);
            return true;
        });
    }

    public static void put(ByteBuffer buffer, TLongList list) {
        buffer.putInt(list.size());

        list.forEach(item -> {
            BufferUtil.put(buffer, item);
            return true;
        });
    }

    public static ByteBuffer ensureCapacity(final ByteBuffer buffer, final int additionalSize) {

        if (buffer.remaining() >= additionalSize) {
            return buffer;
        }

        int newLength;
        final int length = buffer.limit();
        final int neededLength = length + additionalSize - buffer.remaining();

        for (newLength = length * 2; newLength < neededLength; newLength *= 2) ;

        final ByteBuffer newBuffer = ByteBuffer.allocate(newLength).order(ByteOrder.LITTLE_ENDIAN);
        buffer.limit(buffer.position());
        buffer.rewind();
        newBuffer.put(buffer);

        return newBuffer;
    }

    public static ByteBuffer combineBuffers(ByteBuffer firstBuffer, final ByteBuffer secondBuffer) {
        firstBuffer = ensureCapacity(firstBuffer, secondBuffer.remaining());
        firstBuffer.put(secondBuffer);
        return firstBuffer;
    }
}
