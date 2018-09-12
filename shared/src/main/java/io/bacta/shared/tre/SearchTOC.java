package io.bacta.shared.tre;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by crush on 3/19/14.
 */
class SearchTOC extends SearchNode {
    private static final int ID_TOC = TreeFileUtil.makeId("TOC ");
    private static final int ID_0001 = TreeFileUtil.makeId("0001");

    private static final Comparator<TableOfContentsEntry> tocComparator = new Comparator<TableOfContentsEntry>() {
        @Override
        public int compare(TableOfContentsEntry toc1, TableOfContentsEntry toc2) {
            return Integer.compare(toc1.crc, toc2.crc);
        }
    };

    private final Path filePath;

    public final String getFilePath() {
        return filePath.toAbsolutePath().toString();
    }

    private final List<TableOfContentsEntry> tableOfContents;
    private final List<String> treeFileNames;
    private final ByteBuffer fileNameBlock;

    public SearchTOC(final String filePath, int searchPriority) throws
            IOException,
            UnsupportedTOCFileException,
            UnsupportedTOCFileVersionException {
        super(searchPriority);

        logger.trace("Processing SearchTOC with filename [{}].", filePath);

        this.filePath = Paths.get(filePath);

        final RandomAccessFile file = new RandomAccessFile(filePath, "r");
        final FileChannel channel = file.getChannel();
        final MappedByteBuffer buffer = (MappedByteBuffer) channel.map(
                FileChannel.MapMode.READ_ONLY, 0, channel.size()).order(ByteOrder.LITTLE_ENDIAN);

        channel.close();
        file.close();

        if (buffer.remaining() < Header.SIZE)
            throw new UnsupportedTOCFileException();

        final Header header = new Header(buffer);

        if (header.token != ID_TOC)
            throw new UnsupportedTOCFileException();

        if (header.version != ID_0001)
            throw new UnsupportedTOCFileVersionException(header.version);

        this.tableOfContents = new ArrayList<>(TableOfContentsEntry.SIZE * header.numberOfFiles);
        this.treeFileNames = new ArrayList<>(header.numberOfTreeFiles);

        //Read the tree file paths.
        while (buffer.position() < Header.SIZE + header.sizeOfTreeFileNameBlock) {
            this.treeFileNames.add(TreeFileUtil.readNullTerminatedString(buffer));
        }

        //Read the TOC entries.
        final ByteBuffer tableOfContentsBuffer = ByteBuffer
                .allocate(TableOfContentsEntry.SIZE * header.numberOfFiles)
                .order(ByteOrder.LITTLE_ENDIAN);
        TreeFileUtil.expand(buffer, tableOfContentsBuffer, header.tocCompressor, header.sizeOfTOC);

        int adjustedFileOffset = 0;
        int previousFileOffset = 0;
        for (int i = 0; i < header.numberOfFiles; i++) {
            TableOfContentsEntry toc = new TableOfContentsEntry(tableOfContentsBuffer);

            previousFileOffset = toc.fileNameOffset;
            toc.fileNameOffset = adjustedFileOffset;

            adjustedFileOffset += previousFileOffset + 1;

            this.tableOfContents.add(toc);
        }

        //Read the file names.
        this.fileNameBlock = ByteBuffer
                .allocate(header.uncompSizeOfNameBlock)
                .order(ByteOrder.LITTLE_ENDIAN);
        TreeFileUtil.expand(buffer, this.fileNameBlock, header.fileNameBlockCompressor, header.sizeOfNameBlock);

        Collections.sort(this.tableOfContents, tocComparator);
    }

    @Override
    public byte[] open(final String filePath) {
        byte[] bytes = null;

        int index = indexOf(filePath);

        if (index != -1) {
            final TableOfContentsEntry entry = this.tableOfContents.get(index);

            try {
                final RandomAccessFile file = new RandomAccessFile(this.filePath.getParent().toString() + "/" + this.treeFileNames.get(entry.treeFileIndex), "r");
                final FileChannel channel = file.getChannel();
                final MappedByteBuffer fileBuffer = channel.map(FileChannel.MapMode.READ_ONLY, entry.offset, entry.compressor != 0 ? entry.compressedLength : entry.length);

                final ByteBuffer buffer = ByteBuffer.allocate(entry.length);

                TreeFileUtil.expand(fileBuffer, buffer, entry.compressor, entry.compressor != 0 ? entry.compressedLength : entry.length);

                bytes = buffer.array();

                channel.close();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return bytes;
    }

    @Override
    public boolean exists(final String filePath) {
        return indexOf(filePath) > -1;
    }

    public int indexOf(final String filePath) {
        return searchTableOfContents(TreeFileUtil.calculateCrc(filePath), 0, this.tableOfContents.size() - 1);
    }

    private int searchTableOfContents(int crc, int left, int right) {
        if (left > right)
            return -1;

        int middle = (left + right) / 2;

        if (this.tableOfContents.get(middle).crc == crc)
            return middle;

        if (this.tableOfContents.get(middle).crc > crc)
            return searchTableOfContents(crc, left, middle - 1);

        return searchTableOfContents(crc, middle + 1, right);
    }

    public final class Header {
        public static final int SIZE = 36;

        public int token;
        public int version;
        public byte tocCompressor;
        public byte fileNameBlockCompressor;
        public byte unusedOne;
        public byte unusedTwo;
        public int numberOfFiles;
        public int sizeOfTOC;
        public int sizeOfNameBlock;
        public int uncompSizeOfNameBlock;
        public int numberOfTreeFiles;
        public int sizeOfTreeFileNameBlock;

        public Header(ByteBuffer buffer) {
            this.token = buffer.getInt();
            this.version = buffer.getInt();
            this.tocCompressor = buffer.get();
            this.fileNameBlockCompressor = buffer.get();
            this.unusedOne = buffer.get();
            this.unusedTwo = buffer.get();
            this.numberOfFiles = buffer.getInt();
            this.sizeOfTOC = buffer.getInt();
            this.sizeOfNameBlock = buffer.getInt();
            this.uncompSizeOfNameBlock = buffer.getInt();
            this.numberOfTreeFiles = buffer.getInt();
            this.sizeOfTreeFileNameBlock = buffer.getInt();
        }
    }

    public final class TableOfContentsEntry {
        public static final int SIZE = 24;

        public byte compressor;
        public byte unused;
        public short treeFileIndex;
        public int crc;
        public int fileNameOffset;
        public int offset;
        public int length;
        public int compressedLength;

        public TableOfContentsEntry(ByteBuffer buffer) {
            this.compressor = buffer.get();
            this.unused = buffer.get();
            this.treeFileIndex = buffer.getShort();
            this.crc = buffer.getInt();
            this.fileNameOffset = buffer.getInt();
            this.offset = buffer.getInt();
            this.length = buffer.getInt();
            this.compressedLength = buffer.getInt();
        }
    }
}