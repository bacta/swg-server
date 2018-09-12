package io.bacta.shared.tre;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

class SearchTree extends SearchNode {
    private final String filePath;

    public final String getFilePath() {
        return filePath;
    }

    private Map<String, TableOfContentsEntry> tableOfContents = new HashMap<>();

    private int recordsOffset;
    private int recordsCompressionLevel;
    private int recordsDeflatedSize;
    private int namesCompressionLevel;
    private int namesDeflatedSize;
    private int namesInflatedSize;

    public SearchTree(String filePath, int priority) {
        super(priority);

        this.filePath = filePath;
    }

    public final void preprocess() throws
            IOException,
            UnsupportedTreeFileException,
            UnsupportedTreeFileVersionException {

        logger.trace(filePath);

        final RandomAccessFile file = new RandomAccessFile(filePath, "r");
        final FileChannel channel = file.getChannel();
        final MappedByteBuffer buffer = (MappedByteBuffer) channel.map(
                FileChannel.MapMode.READ_ONLY, 0, channel.size()).order(ByteOrder.LITTLE_ENDIAN);

        channel.close();
        file.close();

        if (buffer.remaining() < 4)
            throw new UnsupportedTreeFileException(1);

        //Read the file.
        final int fileId = buffer.getInt();

        if (fileId != TreeFile.ID_TREE)
            throw new UnsupportedTreeFileException(fileId);

        final int version = buffer.getInt();

        if (version != TreeFile.ID_0005 && version != TreeFile.ID_0006)
            throw new UnsupportedTreeFileVersionException(version);

        final int totalRecords = buffer.getInt();

        tableOfContents = new HashMap<>(totalRecords);

        recordsOffset = buffer.getInt();
        recordsCompressionLevel = buffer.getInt();
        recordsDeflatedSize = buffer.getInt();
        namesCompressionLevel = buffer.getInt();
        namesDeflatedSize = buffer.getInt();
        namesInflatedSize = buffer.getInt();

        buffer.position(recordsOffset);

        final ByteBuffer recordData = ByteBuffer.allocate(TableOfContentsEntry.SIZE * totalRecords);
        final ByteBuffer namesData = ByteBuffer.allocate(namesInflatedSize);

        TreeFileUtil.expand(buffer, recordData, recordsCompressionLevel, recordsDeflatedSize);
        TreeFileUtil.expand(buffer, namesData, namesCompressionLevel, namesDeflatedSize);

        final ByteBuffer checksumData = buffer.slice();
        final byte[] md5 = new byte[16];

        for (int i = 0; i < totalRecords; ++i) {
            ByteBuffer data = recordData.order(ByteOrder.LITTLE_ENDIAN);

            final TableOfContentsEntry entry = new TableOfContentsEntry();
            entry.crc = data.getInt();
            entry.length = data.getInt();
            entry.offset = data.getInt();
            entry.compressor = data.getInt();
            entry.compressedLength = data.getInt();
            entry.fileNameOffset = data.getInt();

            if (entry.compressor == 0)
                entry.compressedLength = entry.length;

            //Find the end of the string.
            final StringBuilder stringBuilder = new StringBuilder();
            namesData.position(entry.fileNameOffset);

            byte b = 0;
            while ((b = namesData.get()) != 0)
                stringBuilder.append((char) b);

            final String filename = stringBuilder.toString();
            checksumData.get(md5);

            //TODO: How do we use the md5 to check it? When and why?

            tableOfContents.put(filename, entry);
        }
    }

    @Override
    public byte[] open(String filePath) {

        final TableOfContentsEntry entry = tableOfContents.get(filePath);

        byte[] bytes = null;

        if (entry != null) {
            ByteBuffer buffer = null;

            try {
                RandomAccessFile file = new RandomAccessFile(this.filePath, "r");
                FileChannel channel = file.getChannel();
                MappedByteBuffer fileBuffer = channel.map(FileChannel.MapMode.READ_ONLY, entry.offset, entry.compressedLength);

                buffer = ByteBuffer.allocate(entry.length);

                TreeFileUtil.expand(fileBuffer, buffer, entry.compressor, entry.compressedLength);

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
    public boolean exists(String filePath) {
        return tableOfContents.containsKey(filePath);
    }

    public Set<String> listFiles() {
        return tableOfContents.keySet();
    }

    public final class Header {
        public static final int SIZE = 36;

        public int token;
        public int version;
        public int numberOfFiles;
        public int tocOffset;
        public int tocCompressor;
        public int sizeOfTOC;
        public int blockCompressor;
        public int sizeOfNameBlock;
        public int uncompSizeOfNameBlock;
    }

    public final class TableOfContentsEntry {
        public static final int SIZE = 24;
        public int crc;
        public int length;
        public int offset;
        public int compressor;
        public int compressedLength;
        public int fileNameOffset;
    }
}