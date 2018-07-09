package io.bacta.shared.iff;

/**
 * Created by crush on 5/14/2016.
 * <p>
 * Declares that a class can be serialized to an Iff file, and defines how it does it.
 */
public interface IffWritable {
    void writeToIff(final Iff iff);
}
