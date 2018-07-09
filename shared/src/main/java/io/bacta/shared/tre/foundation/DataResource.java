package io.bacta.shared.tre.foundation;

/**
 * Created by crush on 11/22/2015.
 * <p>
 * It seems that this class is meant to reference count a resource...it's basically good for cached lists to keep
 * track of when they can drop an object from their maps.
 */
public abstract class DataResource {
    private final PersistentCrcString name;
    private volatile int referenceCount;

    public DataResource(final String fileName) {
        this.name = new PersistentCrcString(fileName, true);
    }

    public String getResourceName() {
        return name.getString();
    }

    public CrcString getCrcName() {
        return name;
    }

    protected abstract void release();

    public int getReferenceCount() {
        return referenceCount;
    }

    public void addReference() {
        ++referenceCount;
    }

    public void releaseReference() {
        if (referenceCount > 0 && --referenceCount == 0)
            release();
    }
}