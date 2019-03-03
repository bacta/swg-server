package io.bacta.shared.tre;

/**
 * Created by crush on 12/16/2014.
 */
public class UnsupportedTreeFileException extends Exception {
    public UnsupportedTreeFileException(int fileId) {
        super(String.format("Unsupported tree file specifier: %n.", fileId));
    }
}
