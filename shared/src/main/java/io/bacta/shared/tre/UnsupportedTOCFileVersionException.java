package io.bacta.shared.tre;

/**
 * Created by crush on 12/16/2014.
 */
public class UnsupportedTOCFileVersionException extends Exception {
    public UnsupportedTOCFileVersionException(int version) {
        super(String.format("Unsupported TOC file version: %n.", version));
    }
}
