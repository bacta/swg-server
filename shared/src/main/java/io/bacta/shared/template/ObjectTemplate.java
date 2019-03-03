package io.bacta.shared.template;

import io.bacta.shared.foundation.DataResource;
import io.bacta.shared.foundation.DataResourceList;
import io.bacta.shared.iff.Iff;
import io.bacta.shared.object.template.SharedObjectTemplate;

/**
 * Created by crush on 4/19/2016.
 */
public abstract class ObjectTemplate extends DataResource {
    protected final DataResourceList<ObjectTemplate> objectTemplateList;
    protected ObjectTemplate baseData;

    public ObjectTemplate(final String filename, final DataResourceList<ObjectTemplate> objectTemplateList) {
        super(filename);
        this.objectTemplateList = objectTemplateList;
    }

    @Override
    protected void release() {
        objectTemplateList.release(this);
    }

    public ObjectTemplate getBaseTemplate() {
        return baseData;
    }

    public abstract int getId();

    /**
     * Loads the object template from an iff resource.
     *
     * @param iff The iff file.
     */
    public final void loadFromIff(final Iff iff) {
        preLoad();
        load(iff);
        postLoad();
    }

    /**
     * Saves the object template to an iff resource.
     *
     * @param iff The iff file.
     */
    public final void saveToIff(final Iff iff) {
    }

    /**
     * Checks if this template derives from the given potential ancestor template.
     *
     * @param potentialAncestorName The ancestor template.
     * @return True if this template derives from the ancestor; otherwise false.
     */
    public boolean derivesFrom(final String potentialAncestorName) {
        if (getResourceName().equalsIgnoreCase(potentialAncestorName))
            return true;

        if (baseData != null)
            return baseData.derivesFrom(potentialAncestorName);

        return false;
    }

    protected abstract void load(final Iff iff);

    /**
     * Specifies work that should occur before the loading process.
     */
    protected void preLoad() {
    }

    /**
     * Specifies work that should occur after the loading process.
     */
    protected void postLoad() {
    }

    public SharedObjectTemplate asSharedObjectTemplate() {
        if (this instanceof SharedObjectTemplate)
            return (SharedObjectTemplate) this;

        return null;
    }
}
