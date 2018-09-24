package io.bacta.swg.iff;

import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by crush on 12/17/2014.
 */
public final class Iff {
    private static final Logger LOGGER = LoggerFactory.getLogger(Iff.class);

    public static final int TAG_FORM = createChunkId("FORM");
    public static final int TAG_PROP = createChunkId("PROP");
    public static final int TAG_LIST = createChunkId("LIST");
    public static final int TAG_CAT = createChunkId("CAT ");
    public static final int TAG_FILL = createChunkId("    ");

    private static final int CHUNK_HEADER_SIZE = 8;
    private static final int GROUP_HEADER_SIZE = 12;
    private static final int DEFAULT_STACK_DEPTH = 64;

    public static int createChunkId(final String chunkId) {
        final byte[] bytes = chunkId.getBytes();
        return ((bytes[0] << 24 | bytes[1] << 16 | bytes[2] << 8 | bytes[3]));
    }

    public static String getChunkName(final int id) {
        return new String(ByteBuffer.allocate(4).putInt(id).array());
    }

    public static boolean isGroupChunkId(final int id) {
        return id == TAG_FORM || id == TAG_LIST || id == TAG_CAT;
    }

    private static int endianSwap32(int val) {
        return (((val & 0x000000ff) << 24) +
                ((val & 0x0000ff00) << 8) +
                ((val & 0x00ff0000) >> 8) +
                ((val >> 24) & 0x000000ff));
    }

    private String fileName;
    private ByteBuffer data;
    private final List<Stack> stack;
    private int stackDepth;
    private boolean inChunk;

    public Iff() {
        this.data = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        this.stack = new ArrayList<>(DEFAULT_STACK_DEPTH);

        final Stack rootStack = new Stack();
        rootStack.offset = 0;
        rootStack.length = 0;
        rootStack.used = 0;

        this.stack.add(rootStack);
    }

    public Iff(final String fileName) {
        this.fileName = fileName;
        this.data = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
        this.stack = new ArrayList<>(DEFAULT_STACK_DEPTH);
        this.stackDepth = 0;
        this.inChunk = false;

        final Stack rootStack = new Stack();
        rootStack.offset = 0;
        rootStack.length = 0;
        rootStack.used = 0;

        this.stack.add(rootStack);
    }

    public Iff(final String fileName, final byte[] bytes) {
        this.fileName = fileName;
        this.data = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN);
        this.stack = new ArrayList<>(DEFAULT_STACK_DEPTH);
        this.stackDepth = 0;
        this.inChunk = false;

        final Stack rootStack = new Stack();
        rootStack.offset = 0;
        rootStack.length = bytes.length;
        rootStack.used = 0;

        this.stack.add(rootStack);
    }

    /**
     * Construct an IFF for writing new data.
     *
     * @param initialSize Initial size of the Iff data.
     */
    public Iff(final int initialSize) {
        this.data = ByteBuffer.allocate(initialSize).order(ByteOrder.LITTLE_ENDIAN);
        this.stack = new ArrayList<>(DEFAULT_STACK_DEPTH);

        final Stack rootStack = new Stack();
        rootStack.offset = 0;
        rootStack.length = 0;
        rootStack.used = 0;

        this.stack.add(rootStack);
    }

    public byte[] getRawData() {
        return data.array();
    }

    /**
     * Calculate the number of bytes contained in the raw Iff data buffer.
     *
     * @return
     */
    public int calculateRawDataSize() {
        final int length = data.capacity();

        int offset = 0;
        int blockLength;
        int tempLength;

        do {
            //get an int from the current offset + 4
            tempLength = data.getInt(offset + 4);
            tempLength = endianSwap32(tempLength);
            blockLength = tempLength + 4 + 4;
            offset += blockLength;
        } while ((offset < length) && blockLength != 0);

        return offset;
    }

    public final String getFileName() {
        return this.fileName;
    }

    public final int getStackDepth() {
        return this.stackDepth;
    }

    public final boolean readBoolean() {
        return readByte() == 1;
    }

    public final byte readByte() {
        if (!this.inChunk)
            throw new UnsupportedOperationException("Cannot read while not in a chunk.");

        final Stack chunk = this.stack.get(this.stackDepth);

        if (chunk.used + 1 > chunk.length)
            throw new BufferOverflowException();

        byte value = data.get(chunk.used + chunk.offset);

        chunk.used += 1;

        return value;
    }

    public final short readShort() {
        if (!this.inChunk)
            throw new UnsupportedOperationException("Cannot read while not in a chunk.");

        final Stack chunk = this.stack.get(this.stackDepth);

        if (chunk.used + 2 > chunk.length)
            throw new BufferOverflowException();

        short value = data.getShort(chunk.used + chunk.offset);

        chunk.used += 2;

        return value;
    }

    public final int readInt() {
        if (!this.inChunk)
            throw new UnsupportedOperationException("Cannot read while not in a chunk.");

        final Stack chunk = this.stack.get(this.stackDepth);

        if (chunk.used + 4 > chunk.length)
            throw new BufferOverflowException();

        int value = data.getInt(chunk.used + chunk.offset);

        chunk.used += 4;

        return value;
    }

    public final long readLong() {
        if (!this.inChunk)
            throw new UnsupportedOperationException("Cannot read while not in a chunk.");

        final Stack chunk = this.stack.get(this.stackDepth);

        if (chunk.used + 8 > chunk.length)
            throw new BufferOverflowException();

        long value = data.getLong(chunk.used + chunk.offset);

        chunk.used += 8;

        return value;
    }

    public final float readFloat() {
        if (!this.inChunk)
            throw new UnsupportedOperationException("Cannot read while not in a chunk.");

        final Stack chunk = this.stack.get(this.stackDepth);

        if (chunk.used + 4 > chunk.length)
            throw new BufferOverflowException();

        float value = data.getFloat(chunk.used + chunk.offset);

        chunk.used += 4;

        return value;
    }

    public final String readString() {
        if (!this.inChunk)
            throw new UnsupportedOperationException("Cannot read while not in a chunk.");

        final Stack chunk = this.stack.get(this.stackDepth);

        if (chunk.length - chunk.used <= 0)
            throw new UnsupportedOperationException("At end of chunk, cannot read.");

        final StringBuilder stringBuilder = new StringBuilder();

        for (int index = 0; index < chunk.length - chunk.used; ++index) {
            byte b = this.data.get(chunk.offset + chunk.used + index);

            if (b == 0)
                break;

            stringBuilder.append((char) b);
        }

        chunk.used += stringBuilder.length() + 1; //+1 for null byte terminator.

        return stringBuilder.toString();
    }

    public final String readUnicode() {
        if (!this.inChunk)
            throw new UnsupportedOperationException("Cannot read while not in a chunk.");

        final Stack chunk = this.stack.get(this.stackDepth);

        int length = readInt();

        if (chunk.length - chunk.used <= 0)
            throw new UnsupportedOperationException("At end of chunk, cannot read.");

        byte[] bytes = new byte[length];
        this.data.get(bytes, chunk.offset + chunk.used, length);

        return new String(bytes, Charset.forName("UTF-16LE"));
    }

    public final void enterChunk() {
        enterChunk(0, false, false);
    }

    public final void enterChunk(final int chunkId) {
        enterChunk(chunkId, true, false);
    }

    public final boolean enterChunk(final boolean optional) {
        return enterChunk(0, false, optional);
    }

    public final boolean enterChunk(final int chunkId, final boolean optional) {
        return enterChunk(chunkId, true, optional);
    }

    public final boolean enterChunk(final int chunkId, boolean validateName, boolean optional) {
        if (!this.inChunk && !isAtEndOfForm() && isCurrentChunk()
                && (!validateName || getFirstTag(this.stackDepth) == chunkId)) {

            final Stack prevStack = this.stack.get(this.stackDepth);
            final Stack nextStack = this.stack.size() <= this.stackDepth + 1 ? new Stack() : this.stack.get(this.stackDepth + 1);
            nextStack.offset = prevStack.offset + prevStack.used + CHUNK_HEADER_SIZE;
            nextStack.length = getLength(this.stackDepth, 0);
            nextStack.used = 0;

            if (this.stack.size() <= this.stackDepth + 1) {
                this.stack.add(nextStack);
            } else {
                this.stack.set(this.stackDepth + 1, nextStack);
            }

            ++this.stackDepth;
            this.inChunk = true;

            return true;
        }

        if (!optional)
            throw new IllegalStateException(String.format("Enter chunk [%s] failed.", Iff.getChunkName(chunkId)));

        return false;
    }

    public final void enterForm() {
        enterForm(0, false, false);
    }

    public final void enterForm(final int formId) {
        enterForm(formId, true, false);
    }

    public final boolean enterForm(final boolean optional) {
        return enterForm(0, false, optional);
    }

    public final boolean enterForm(final int formId, final boolean optional) {
        return enterForm(formId, true, optional);
    }

    private boolean enterForm(final int formId, final boolean validateName, final boolean optional) {

        if (!this.inChunk && !isAtEndOfForm() && isCurrentForm() && (!validateName || getSecondTag(this.stackDepth) == formId)) {
            final Stack prevStack = this.stack.get(this.stackDepth);
            final Stack nextStack = this.stack.size() <= this.stackDepth + 1 ? new Stack() : this.stack.get(this.stackDepth + 1);
            nextStack.offset = prevStack.offset + prevStack.used + GROUP_HEADER_SIZE;
            nextStack.length = getLength(this.stackDepth, 0) - 4;
            nextStack.used = 0;

            if (this.stack.size() <= this.stackDepth + 1) {
                this.stack.add(nextStack);
            } else {
                this.stack.set(this.stackDepth + 1, nextStack);
            }

            ++this.stackDepth;

            return true;
        }

        if (!optional) {
            final String msg = String.format("Entering form %s failed.", Iff.getChunkName(formId));
            LOGGER.info(msg);
            throw new IllegalStateException(msg);
        }

        return false;
    }

    public final void exitChunk() {
        exitChunk(0);
    }

    public final void exitChunk(final int chunkId) {
        if (chunkId != 0) {
            final int prevChunkId = getFirstTag(this.stackDepth - 1);

            if (prevChunkId != chunkId) {
                throw new IllegalArgumentException(String.format("Trying to exit chunk [%s] but found [%s].",
                        Iff.getChunkName(chunkId),
                        Iff.getChunkName(prevChunkId)));
            }
        }

        assert inChunk : "not in chunk";

        final Stack prevStack = this.stack.get(this.stackDepth - 1);
        final Stack thisStack = this.stack.get(this.stackDepth);

        prevStack.used += thisStack.length + CHUNK_HEADER_SIZE;

        --this.stackDepth;
        this.inChunk = false;
    }

    public final void exitForm() {
        exitForm(0);
    }

    public final void exitForm(final int formId) {
        if (this.stackDepth == 0)
            throw new IllegalArgumentException("Trying to exit root.");

        if (formId != 0) {
            final int prevFormId = getSecondTag(this.stackDepth - 1);

            if (prevFormId != formId) {
                throw new IllegalArgumentException(String.format("Trying to exit form [%s] but found [%s].",
                        Iff.getChunkName(formId),
                        Iff.getChunkName(prevFormId)));
            }
        }

        if (this.inChunk)
            throw new IllegalArgumentException("Tried to exit a form while within a chunk.");

        final Stack prevStack = this.stack.get(this.stackDepth - 1);
        final Stack thisStack = this.stack.get(this.stackDepth);

        prevStack.used += thisStack.length + GROUP_HEADER_SIZE;
        --this.stackDepth;
        this.inChunk = false;
    }

    public void insertForm(int nameTag) {
        insertForm(nameTag, true);
    }

    /**
     * Insert a new form into the Iff at the current location.
     * <p>
     * This routine will handle adding a form into the middle of an existing
     * Iff instance.
     * <p>
     * If the Iff is already inside a chunk, this routine will call Fatal for
     * debug compiles, but will have undefined behavior in release compiles.
     *
     * @param nameTag         int for the new form
     * @param shouldEnterForm True to automatically enter the form
     */

    public void insertForm(int nameTag, boolean shouldEnterForm) {

        Preconditions.checkNotNull(data);
        Preconditions.checkArgument(!inChunk, "inside chunk");

        // make sure the data array can handle this addition
        adjustDataAsNeeded(GROUP_HEADER_SIZE);

        // add the form header
        data.putInt(endianSwap32(TAG_FORM));

        // add the size of the form
        data.putInt(endianSwap32(4));

        // add the real form name
        data.putInt(endianSwap32(nameTag));

        // enter the form if requested
        if (shouldEnterForm)
            enterForm();
    }

    public void insertChunk(int tagName) {
        insertChunk(tagName, true);
    }

    /**
     * Insert a new chunk into the Iff at the current location.
     * <p>
     * This routine will handle adding a chunk into the middle of an existing
     * Iff instance.
     * <p>
     * If the Iff is already inside a chunk, this routine will call Fatal for
     * debug compiles, but will have undefined behavior in release compiles.
     *
     * @param tagName          Name for the new form
     * @param shouldEnterChunk True to automatically enter the chunk
     */

    public void insertChunk(int tagName, boolean shouldEnterChunk) {
        Preconditions.checkNotNull(data);
        Preconditions.checkArgument(!inChunk, "inside chunk");

        //Adjust the data to handle a new chunk.
        adjustDataAsNeeded(CHUNK_HEADER_SIZE);

        final Stack currentStack = stack.get(stackDepth);
        final int offset = currentStack.offset + currentStack.used;

        //Ensure position of data pointer.
        data.position(offset);

        //CHUNK header
        data.putInt(endianSwap32(tagName));

        //Size placeholder
        data.putInt(0);

        //Enter chunk if requested.
        if (shouldEnterChunk)
            enterChunk();
    }

    public boolean write(final String filePath) {
        return write(filePath, false);
    }

    public boolean write(final Path filePath) {
        return write(filePath, false);
    }

    public boolean write(final String filePath, boolean optional) {
        return write(Paths.get(filePath), optional);
    }

    public boolean write(final Path filePath, boolean optional) {
        fileName = filePath.getFileName().toString();

        try {
            Files.write(filePath, data.array());
            return true;
        } catch (IOException e) {
            Preconditions.checkArgument(!optional, String.format("file write failed for %s", fileName));
            return false;
        }
    }

    public void close() {
        fileName = null;
        data.clear();  //lint !e672 // possible memory leak in assignment to Iff::data // no, we only delete when we own it
        stackDepth = 0;
    }

    /**
     * Adjust the data array as necessary.
     * <p>
     * This routine will check if the data array needs to be expanded to hold
     * the specified amount of new data.  If it does, and the Iff is not growable,
     * it will call Fatal in debug compiles, but in release compiles the behavior
     * is undefined.
     * <p>
     * The data array will be doubled in size if it does need to be grown until it
     * will hold the specified amount of data.
     * <p>
     * This routine will also handle size being negative, in which case it will
     * remove the specified number of bytes from the current location in the Iff.
     *
     * @param size Delta number of bytes
     */

    public void adjustDataAsNeeded(int size) {
        // calculate the final required size of the data array
        final int neededLength = stack.get(0).length + size;

        assert neededLength >= 0 : ("data size underflow");
        assert data.limit() == data.capacity() : "Buffer limit should not be different than capacity";

        // check if we need to expand the data array
        if (neededLength > data.capacity()) {
            int length = data.capacity();

            if (length <= 0)
                length = 1;

            // double in size until it supports the needed length
            int newLength;
            for (newLength = length * 2; newLength < neededLength; newLength *= 2) ;

            // make sure the iff was growable

            final int oldPosition = data.position();
            final ByteBuffer newData = ByteBuffer.allocate(newLength).order(ByteOrder.LITTLE_ENDIAN);
            newData.put(data.array());
            newData.position(oldPosition);
            data = newData;
        }

        // move data around to either make room or remove data
        final int offset = stack.get(stackDepth).offset + stack.get(stackDepth).used;
        final int lengthToEnd = stack.get(0).length - offset;

        if (size > 0) {
            if (lengthToEnd > 0) {
                final int startingPosition = data.position();
                final byte[] moveArray = new byte[lengthToEnd];
                data.position(offset);
                data.get(moveArray); //Get the bytes from the array.
                data.position(offset + size);
                data.put(moveArray); //Move them to the end of the array.
                data.position(startingPosition); //Reset the position pointer.
            }
        } else {
            if (lengthToEnd + size > 0) {
                final int startingPosition = data.position();
                final byte[] moveArray = new byte[lengthToEnd + size];
                data.position(offset-size);
                data.get(moveArray);
                data.position(offset);
                data.put(moveArray);
                data.position(startingPosition);
            }
        }

        // make sure all the enclosing stack entries know about the changed size
        for (int i = 0; i <= stackDepth; ++i) {
            // update the stack's idea of the block length
            stack.get(i).length += size;

            // the length of level 0 is the file size, so we should not write it
            if (i != 0) {
                // update the data's idea of the block length
                if (i == stackDepth && inChunk) {
                    data.putInt(stack.get(i).offset - 4, endianSwap32(stack.get(i).length));
                } else {
                    // account for forms start beyond the first 4 data bytes, which is their real form name
                    //stack.get(i).offset = stack.get(i).length + 4;
                    data.putInt(stack.get(i).offset - 8, endianSwap32(stack.get(i).length + 4));
                }
            }
        }

    }

    /**
     * Insert data into the current chunk at the current location.
     * <p>
     * This routine will handle adding data into the middle of an existing
     * chunk.  The current position pointer will be moved to the end of
     * the inserted data.
     * <p>
     * If the Iff is not inside a chunk, this routine will call Fatal for
     * debug compiles, but will have undefined behavior in release compiles.
     *
     * @param data boolean to put into the chunk
     */
    public void insertChunkData(final boolean data) {
        insertChunkData((byte)(data ? 1 : 0));
    }

    public void insertChunkData(final byte data) {
        Preconditions.checkNotNull(this.data);
        Preconditions.checkArgument(inChunk, "not in chunk");

        // make sure the data array can handle this addition
        adjustDataAsNeeded(1);

        // compute the offset to start inserting data at
        final int offset = stack.get(stackDepth).offset + stack.get(stackDepth).used;

        //Ensure the position of the data.
        this.data.position(offset);

        // add the size of the chunk
        this.data.put(data);

        // move the current pointer to the end of the inserted text
        stack.get(stackDepth).used += 1;
    }

    public void insertChunkData(final short data) {
        Preconditions.checkNotNull(this.data);
        Preconditions.checkArgument(inChunk, "not in chunk");

        // make sure the data array can handle this addition
        adjustDataAsNeeded(2);

        // compute the offset to start inserting data at
        final int offset = stack.get(stackDepth).offset + stack.get(stackDepth).used;

        //Ensure the position of the data.
        this.data.position(offset);

        // add the size of the chunk
        this.data.putShort(data);

        // move the current pointer to the end of the inserted text
        stack.get(stackDepth).used += 2;
    }

    public void insertChunkData(final int data) {
        Preconditions.checkNotNull(this.data);
        Preconditions.checkArgument(inChunk, "not in chunk");

        // make sure the data array can handle this addition
        adjustDataAsNeeded(4);

        // compute the offset to start inserting data at
        final int offset = stack.get(stackDepth).offset + stack.get(stackDepth).used;

        //Ensure the position of the data.
        this.data.position(offset);

        // add the size of the chunk
        this.data.putInt(data);

        // move the current pointer to the end of the inserted text
        stack.get(stackDepth).used += 4;
    }

    public void insertChunkData(final long data) {
        Preconditions.checkNotNull(this.data);
        Preconditions.checkArgument(inChunk, "not in chunk");

        // make sure the data array can handle this addition
        adjustDataAsNeeded(8);

        // compute the offset to start inserting data at
        final int offset = stack.get(stackDepth).offset + stack.get(stackDepth).used;

        //Ensure the position of the data.
        this.data.position(offset);

        // add the size of the chunk
        this.data.putLong(data);

        // move the current pointer to the end of the inserted text
        stack.get(stackDepth).used += 8;
    }

    public void insertChunkData(final float data) {
        Preconditions.checkNotNull(this.data);
        Preconditions.checkArgument(inChunk, "not in chunk");

        // make sure the data array can handle this addition
        adjustDataAsNeeded(4);

        // compute the offset to start inserting data at
        final int offset = stack.get(stackDepth).offset + stack.get(stackDepth).used;

        //Ensure the position of the data.
        this.data.position(offset);

        // add the size of the chunk
        this.data.putFloat(data);

        // move the current pointer to the end of the inserted text
        stack.get(stackDepth).used += 4;
    }

    public void insertChunkData(final String data) {
        Preconditions.checkNotNull(this.data);
        Preconditions.checkArgument(inChunk, "not in chunk");
        Preconditions.checkNotNull(data);

        // make sure the data array can handle this addition
        adjustDataAsNeeded(data.length() + 1); //Add 1 for the null byte.

        // compute the offset to start inserting data at
        final int offset = stack.get(stackDepth).offset + stack.get(stackDepth).used;

        //Move the position to the offset position for a true insert.
        this.data.position(offset);

        // add the size of the chunk
        this.data.put(data.getBytes());
        this.data.put((byte) 0); //Put the null byte at the end.

        // move the current pointer to the end of the inserted text
        stack.get(stackDepth).used += data.length() + 1;
    }

    /**
     * Insert a string into the current chunk at the current location.
     * <p>
     * This routine will call insertChunkData(const void *, int length)
     * with the string using its string length (plus one for the null
     * terminator).
     */

    public void insertChunkString(final String value) {
        Preconditions.checkNotNull(value);
        insertChunkData(value);
    }

    public final int getBlockName(int depth) {
        int value = getFirstTag(depth);

        if (isGroupChunkId(value))
            value = getSecondTag(depth);

        return value;
    }

    public final int getNumberOfBlocksLeft() {
        if (this.inChunk)
            throw new UnsupportedOperationException("Cannot get number of blocks left while in a chunk.");

        final Stack chunk = this.stack.get(this.stackDepth);

        int depth = 0, offset = 0;
        while (chunk.used + offset < chunk.length) {
            offset += (getLength(stackDepth, offset) + CHUNK_HEADER_SIZE);
            ++depth;
        }

        return depth;
    }

    public final boolean isCurrentChunk() {
        return !Iff.isGroupChunkId(getFirstTag(this.stackDepth));
    }

    public final boolean isCurrentForm() {
        return getFirstTag(this.stackDepth) == TAG_FORM;
    }

    public final int getCurrentName() {
        return getBlockName(this.stackDepth);
    }

    public final int getCurrentLength() {
        return getLength(this.stackDepth, 0);
    }

    public final int getChunkLengthTotal(int elementSize) {
        if (!this.inChunk)
            throw new UnsupportedOperationException("Not in a chunk.");

        final Stack chunk = this.stack.get(this.stackDepth);

        int lengthMod = chunk.length % elementSize;
        int lengthDiv = chunk.length / elementSize;

        if (chunk.length % elementSize != 0) {
            throw new IllegalArgumentException(String.format("%d is not a multiple of %d.",
                    lengthMod,
                    lengthDiv));
        }

        return lengthDiv;
    }

    public final int getChunkLengthLeft() {
        return getChunkLengthLeft(1);
    }

    public final int getChunkLengthLeft(int elementSize) {
        if (!this.inChunk)
            throw new UnsupportedOperationException("Not in a chunk.");

        final Stack chunk = this.stack.get(this.stackDepth);

        int remaining = chunk.length - chunk.used;

        int remainingMod = remaining % elementSize;
        int remainingDiv = remaining / elementSize;

        if (remainingMod != 0) {
            throw new IllegalArgumentException(String.format("%d is not a multiple of %d.",
                    remainingMod,
                    remainingDiv));
        }

        return remainingDiv;
    }

    public final int getPositionInChunk() {
        return stack.get(stackDepth).used;
    }

    private int getFirstTag(int depth) {
        final Stack chunk = this.stack.get(depth);

        if (chunk.length - chunk.used < CHUNK_HEADER_SIZE)
            throw new BufferOverflowException();

        return Iff.endianSwap32(data.getInt(chunk.offset + chunk.used));
    }

    private int getSecondTag(int depth) {
        final Stack chunk = this.stack.get(depth);

        if (chunk.length - chunk.used < GROUP_HEADER_SIZE)
            throw new BufferOverflowException();

        return Iff.endianSwap32(data.getInt(chunk.offset + chunk.used + CHUNK_HEADER_SIZE));
    }

    public final boolean isAtEndOfForm() {
        final Stack chunkStack = this.stack.get(this.stackDepth);

        return chunkStack.used == chunkStack.length;
    }

    private int getLength(int depth, int offset) {
        final Stack chunkStack = this.stack.get(depth);

        if (offset + chunkStack.length - chunkStack.used < CHUNK_HEADER_SIZE)
            throw new BufferOverflowException();

        return Iff.endianSwap32(data.getInt(offset + 4 + chunkStack.used + chunkStack.offset));
    }

    public final void goToTopOfForm() {
        if (this.inChunk)
            throw new UnsupportedOperationException("Cannot go to the top of form while in a chunk.");

        this.stack.get(this.stackDepth).used = 0;
    }

    public final void goForward(int count) {
        if (this.inChunk)
            throw new UnsupportedOperationException("Cannot go forward when in a chunk.");

        for (int index = count; count > 0 && !isAtEndOfForm(); --index)
            this.stack.get(this.stackDepth).used += getLength(this.stackDepth, 0) + CHUNK_HEADER_SIZE;
    }

    public final boolean seekForm(final int formId) {
        return seek(formId, BlockType.FORM);
    }

    public final boolean seekChunk(final int chunkId) {
        return seek(chunkId, BlockType.CHUNK);
    }

    public void seekWithinChunk(int offset, final SeekType seekType) {
        switch (seekType) {
            case BEGIN:
                stack.get(stackDepth).used = offset;
                break;

            case CURRENT:
                stack.get(stackDepth).used += offset;
                break;

            case END:
                stack.get(stackDepth).used = stack.get(stackDepth).length + offset;
                break;
        }
    }

    private boolean seek(final int chunkId, final BlockType blockType) {
        assert !inChunk : "in chunk";

        while (!isAtEndOfForm()) {
            if (getCurrentName() == chunkId
                    && (blockType == BlockType.EITHER
                    || (blockType == BlockType.FORM && isCurrentForm()
                    || (blockType == BlockType.CHUNK && isCurrentChunk())))) {
                return true;
            }

            stack.get(stackDepth).used += (getLength(stackDepth, 0) + CHUNK_HEADER_SIZE);
        }

        return false;
    }

    private static final class Stack {
        int offset;
        int length;
        int used;
    }

    public enum SeekType {
        BEGIN,
        CURRENT,
        END
    }

    public enum BlockType {
        EITHER,
        FORM,
        CHUNK
    }
}
