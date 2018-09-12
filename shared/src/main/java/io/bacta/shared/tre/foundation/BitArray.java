package io.bacta.shared.tre.foundation;

import gnu.trove.list.TByteList;
import gnu.trove.list.array.TByteArrayList;
import io.bacta.engine.buffer.ByteBufferWritable;

import java.nio.ByteBuffer;

/**
 * Created by crush on 5/8/2016.
 */
public class BitArray implements ByteBufferWritable {
    private static final char[] HEX_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private static final int DEFAULT_SIZE = 16;
    private final TByteList arrayData = new TByteArrayList(DEFAULT_SIZE);
    private int numAllocatedBytes;
    private int numInUseBytes;
    private int numInUseBits;

    public BitArray() {
        numAllocatedBytes = arrayData.size();
        numInUseBytes = 0;
        numInUseBits = 0;
    }

    public BitArray(final int numBits) {
        numAllocatedBytes = arrayData.size();

        if (numBits > (numAllocatedBytes << 3)) {
            numAllocatedBytes = 0;
            numInUseBytes = 0;
            numInUseBits = 0;
            reserve(numBits);
        }
    }

    public BitArray(final ByteBuffer source) {
        int numInUseBytes = source.getInt();
        int numInUseBits = source.getInt();

        //trivial case, rhs is empty
        if ((numInUseBits <= 0) || (numInUseBytes <= 0)) {
            clear();
            return;
        }

        //char array is too small, reallocate new char array
        if (numAllocatedBytes < numInUseBytes) {
            final int numToAdd = numInUseBytes - numAllocatedBytes;

            for (int i = 0; i < numToAdd; ++i)
                arrayData.add((byte) 0);

            numAllocatedBytes = numInUseBytes;
        } else if (this.numInUseBytes > numInUseBytes) {
            //char array is large enough, no reallocation required,
            //but clear out char array, if necessary
            arrayData.fill((byte) 0);
        }

        this.numInUseBytes = numInUseBytes;
        this.numInUseBits = numInUseBits;

        for (int i = 0; i < this.numInUseBytes; ++i)
            this.arrayData.set(i, source.get());
    }

    public void setBit(int index) {
        assert index >= 0 : String.format("Index %d out of range", index);

        if (index >= 0) {
            if (index >= numInUseBits)
                reserve(index + 1);

            final int arrayPos = index >> 3;
            final int bitPos = index & 7;
            final byte val = arrayData.get(arrayPos);
            arrayData.set(arrayPos, (byte) (val | (1 << bitPos)));
        }
    }

    public void setValue(int beginIndex, int endIndex, int value) {
        assert beginIndex >= 0 : String.format("beginIndex [%d] out of range", beginIndex);
        assert endIndex >= 0 : String.format("endIndex [%d] out of range", endIndex);
        assert beginIndex <= endIndex : String.format("beginIndex [%d] > endIndex [%d]", beginIndex, endIndex);

        if ((beginIndex >= 0) && (endIndex >= 0) && (beginIndex <= endIndex)) {
            int currentIndex = beginIndex;

            while (currentIndex <= endIndex) {
                if (value == 0)
                    break;

                if ((value & 0x1) != 0) {
                    setBit(currentIndex++);
                } else {
                    clearBit(currentIndex++);
                }

                value >>= 1;
            }

            for (int i = currentIndex; i <= endIndex; ++i)
                clearBit(i);
        }
    }

    public void setMultipleBits(int count) {
        if (count <= 0)
            return;

        if (count > numInUseBits)
            reserve(count);

        final int charCount = (count >> 3);

        if (charCount != 0)
            arrayData.fill(0, charCount, (byte) 0xFF);

        for (int i = (charCount << 3); i < count; ++i)
            setBit(i);
    }

    public void clearBit(int index) {
        assert index >= 0 : String.format("index [%d] out of range", index);

        if ((index >= 0) && (index < numInUseBits)) {
            final int arrayPos = index >> 3;
            final int bitPos = index & 7;
            final byte value = arrayData.get(arrayPos);
            arrayData.set(arrayPos, (byte) (value & ~(1 << bitPos)));
        }
    }

    public boolean testBit(final int index) {
        assert index >= 0 : String.format("index [%d] out of range", index);

        if ((index < 0) || (index >= numInUseBits))
            return false;

        final int arrayPos = index >> 3;
        final int bitPos = index & 7;

        return (arrayData.get(arrayPos) & (1 << bitPos)) != 0;
    }

    public int getValue(final int beginIndex, final int endIndex) {
        assert beginIndex >= 0 : String.format("beginIndex [%d] out of range", beginIndex);
        assert endIndex >= 0 : String.format("endIndex [%d] out of range", endIndex);
        assert beginIndex <= endIndex : String.format("beginIndex [%d] > endIndex [%d]", beginIndex, endIndex);

        int value = 0;
        if ((beginIndex >= 0) && (endIndex >= 0) && (beginIndex <= endIndex)) {
            for (int i = endIndex; i >= beginIndex; --i) {
                if (value != 0)
                    value <<= 1;

                if (testBit(i))
                    ++value;
            }
        }

        return value;
    }

    public void clear() {
        if (numInUseBytes > 0) {
            arrayData.fill((byte) 0);
            numInUseBytes = 0;
            numInUseBits = 0;
        }
    }

    public int getNumberOfSetBits() {
        int count = 0;
        for (int i = 0; i < numInUseBits; ++i)
            if (testBit(i))
                ++count;

        return count;
    }

    public boolean isEmpty() {
        for (int i = 0; i < numInUseBytes; ++i) {
            if (arrayData.get(i) != 0)
                return false;
        }

        return true;
    }

    public void insertBit(final int index, final boolean value) {
        assert index >= 0 : String.format("index [%d] out of range", index);

        //nothing to do if inserting a new bit past
        //the end and the new bit will not be set
        if (!value && (index >= numInUseBits))
            return;

        //if inserting a new bit in the middle, then shift all
        //bits at and after the insertion point "backward"
        if (index >= 0 && index < numInUseBits) {
            for (int i = (numInUseBits - 1); i >= index; --i) {
                if (testBit(i))
                    setBit(i + 1);
                else
                    clearBit(i + 1);
            }
        }

        //set/clear the inserted bit
        if (value)
            setBit(index);
        else
            clearBit(index);
    }

    public void removeBit(final int index) {
        assert index >= 0 : String.format("index [%d} out of range", index);

        //shift all bits after the removal point "forward"
        if (index >= 0 && index < numInUseBits) {
            final int lastBitIndexToShift = numInUseBits - 1;

            for (int i = index; i < lastBitIndexToShift; ++i) {
                if (testBit(i + 1))
                    setBit(i);
                else
                    clearBit(i);
            }

            //shrink the array by 1
            reserve(numInUseBits - 1);
        }
    }

    private void getAsDbTextString(final StringBuilder sb) {
        final int maxNibbleCount = 32767;

        boolean anyBitSet = false;
        int nibbleIntValue;
        final int nibbleCount = Math.min(maxNibbleCount, (numInUseBytes << 1));

        for (int i = (nibbleCount - 1); i >= 0; --i) {
            nibbleIntValue = arrayData.get(i >> 1);

            if (i % 2 != 0)
                nibbleIntValue >>= 4;
            else
                nibbleIntValue &= 0xF;

            if (nibbleIntValue != 0 && !anyBitSet) {
                anyBitSet = true;
            }

            if (anyBitSet)
                sb.append(HEX_DIGITS[nibbleIntValue]);
        }
    }

    public String getDebugString() {
        final StringBuilder sb = new StringBuilder(512);
        int i;
        for (i = 0; i < numInUseBits; ++i) {
            if ((i > 0) && ((i % 8) == 0))
                sb.append(' ');

            sb.append(testBit(i) ? '1' : '0');
        }

        sb.append(" (");
        getAsDbTextString(sb);
        sb.append(") (");

        for (i = 0; i < numAllocatedBytes; ++i) {
            if (i > 0)
                sb.append(' ');

            if (i == numInUseBytes)
                sb.append("   ");

            sb.append(String.format("%02X", arrayData.get(i)));
        }

        sb.append(String.format(") (%d/%d heap storage)", numInUseBytes, numAllocatedBytes));

        return sb.toString();
    }

    public boolean match(final BitArray rhs) {
        if (this == rhs)
            return true;

        //compare teh individual chars in the 2 array up to the number of chars needed to hold the smaller of
        //the 2 numInUseBits
        final int smallerArraySize = Math.min(numInUseBytes, rhs.numInUseBytes);
        for (int i = 0; i < smallerArraySize; ++i)
            if (arrayData.get(i) != rhs.arrayData.get(i))
                return false;

        //if there are any additional set bits (i.e. non-zero char)
        //in the larger of the 2 arrays, then equality is false.
        if (numInUseBytes != rhs.numInUseBytes) {
            final TByteList largerArrayData;
            final int largerArraySize;

            if (numInUseBytes > rhs.numInUseBytes) {
                largerArraySize = numInUseBytes;
                largerArrayData = arrayData;
            } else {
                largerArraySize = rhs.numInUseBytes;
                largerArrayData = rhs.arrayData;
            }

            for (int j = smallerArraySize; j < largerArraySize; ++j)
                if (largerArrayData.get(j) != 0)
                    return false;
        }

        return true;
    }

    public boolean matchAnyBit(final BitArray rhs) {
        //compare the individual chars in the 2 array up to the number
        //of chars needed to hold the smaller of the 2 numInUseBits
        final int smallerArraySize = Math.min(numInUseBytes, rhs.numInUseBytes);
        for (int i = 0; i < smallerArraySize; ++i)
            if ((arrayData.get(i) & rhs.arrayData.get(i)) != 0)
                return true;
        return false;
    }

    public void reserve(final int numBits) {
        assert numBits >= 0 : String.format("number of bits [%d] out of range", numBits);

        if (numBits > 0) {
            // if shrinking, make sure to clear all the bits between
            // the new size and the old size to to maintain requirement
            // that every bit past 0-based index (m_numInUseBits - 1) is
            // guaranteed to be unset
            if (numBits < numInUseBits) {
                for (int i = numBits; i < numInUseBits; ++i)
                    clearBit(i);
            }

            numInUseBits = numBits;

            final int oldNumInUseBytes = numInUseBytes;
            numInUseBytes = (numInUseBits + 7) >> 3;

            //Expand the list to have more bytes.
            if (numInUseBytes > numAllocatedBytes) {
                //We need to add the number of bytes that is the difference between numInUseBytes and numAllocatedBytes.
                final int numBytesToAdd = numInUseBytes - numAllocatedBytes;

                for (int i = 0; i < numBytesToAdd; ++i)
                    arrayData.add((byte) 0);

                numAllocatedBytes = numInUseBytes;
            }
        } else if (numBits == 0) {
            //We can't really shrink the capacity of groves TByteList, so we will just reset it to all 0s.
            arrayData.fill((byte) 0);
            numAllocatedBytes = DEFAULT_SIZE;
            numInUseBytes = 0;
            numInUseBits = 0;
        }
    }

    @Override
    public void writeToBuffer(final ByteBuffer target) {
        target.putInt(this.numInUseBytes);
        target.putInt(this.numInUseBits);

        if (this.numInUseBits > 0 && this.numInUseBytes > 0)
            for (int i = 0; i < this.numInUseBytes; ++i)
                target.put(arrayData.get(i)); //This is faster than using toArray because toArray does a memcopy.
    }
}
