package io.bacta.shared.tre;

/**
 * Created by crush on 3/20/14.
 */
class SearchCache extends SearchNode {

    //PersistentCrcString name
    //char* buffer
    //int length
    //bool compressed
    //int uncompressedLength

    public SearchCache(int searchPriority) {
        super(searchPriority);
    }

    @Override
    public byte[] open(String filePath) {
        return null;
    }

    @Override
    public boolean exists(String filePath) {
        return false;
    }
}