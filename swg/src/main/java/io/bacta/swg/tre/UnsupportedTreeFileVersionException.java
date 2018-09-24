package io.bacta.swg.tre;

/**
 * Created by crush on 12/16/2014.
 */
public class UnsupportedTreeFileVersionException extends Exception {
    public UnsupportedTreeFileVersionException(int version) {
        super(String.format("Unsupported tree file version: %n.", version));
    }
}
