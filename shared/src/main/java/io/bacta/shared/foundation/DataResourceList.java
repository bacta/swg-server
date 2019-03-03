package io.bacta.shared.foundation;


import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.bacta.shared.iff.Iff;
import io.bacta.shared.template.ObjectTemplate;
import io.bacta.shared.tre.TreeFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;

/**
 * Created by crush on 11/22/2015.
 */
public abstract class DataResourceList<DataType extends DataResource> {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataResourceList.class);

    protected final TreeFile treeFile;

    private final TIntObjectMap<BiFunction<String, DataResourceList<DataType>, DataType>> createDataResourceMap;
    private final Map<CrcString, DataType> loadedDataResourceMap;

    public DataResourceList(final TreeFile treeFile) {
        this.treeFile = treeFile;

        createDataResourceMap = new TIntObjectHashMap<>();
        loadedDataResourceMap = new ConcurrentHashMap<>();
    }

    /**
     * Maps a template Tag id to a function used to create a template.
     *
     * @param id         id of the template
     * @param createFunc template creation function
     */
    public void registerTemplate(final int id, final BiFunction<String, DataResourceList<DataType>, DataType> createFunc) {
        createDataResourceMap.put(id, createFunc);
    }

    /**
     * Checks the reference count of a resource. If it is 0, deletes it.
     *
     * @param dataResource The data resource to release.
     */
    public void release(final DataType dataResource) {
        if (dataResource.getReferenceCount() == 0 && loadedDataResourceMap.containsKey(dataResource.getCrcName())) {
            loadedDataResourceMap.remove(dataResource.getCrcName());
        }
    }

    public <T extends DataType> T fetch(final String filename) {
        return fetch(new TemporaryCrcString(filename, true));
    }

    /**
     * Creates a data resource from a registered data resource id. The caller is
     * responsible for deleting the data resource.
     *
     * @param id the iff tag for the data resource
     * @return the blank data resource
     */
    @SuppressWarnings("unchecked")
    public <T extends DataType> T fetchByTag(final int id) {
        final BiFunction<String, DataResourceList<DataType>, DataType> binding = createDataResourceMap.get(id);

        if (binding == null)
            return null;

        return (T) binding.apply("", this);
    }

    /**
     * Loads a data resource from an iff file/buffer.
     *
     * @param source iff source to read from
     * @return the data resource
     */
    @SuppressWarnings("unchecked")
    public <T extends DataType> T fetch(final Iff source) {
        final int id = source.getCurrentName();

        final BiFunction<String, DataResourceList<DataType>, DataType> createFunc = createDataResourceMap.get(id);

        final T newResource = (T) createFunc.apply(source.getFileName(), this);

        if (newResource instanceof ObjectTemplate) {
            ((ObjectTemplate) newResource).loadFromIff(source);
        }

        postFetch(newResource);

        return newResource;
    }

    /**
     * Loads a data resource from an iff file.
     *
     * @param filename the file to load from
     * @return the data resource
     */
    @SuppressWarnings("unchecked")
    public <T extends DataType> T fetch(final CrcString filename) {
        if (loadedDataResourceMap.containsKey(filename))
            return (T) loadedDataResourceMap.get(filename);

        final byte[] fileBytes = treeFile.open(filename.getString());

        if (fileBytes == null)
            return null;

        final Iff iff = new Iff(filename.getString(), fileBytes);

        final T newDataResource = fetch(iff);

        if (newDataResource != null) {
            newDataResource.addReference();
            loadedDataResourceMap.put(newDataResource.getCrcName(), newDataResource);
        }

        //Don't call postFetch here because its called in the iff loader version.

        return newDataResource;
    }

    public boolean isLoaded(final String filename) {
        return loadedDataResourceMap.containsKey(filename);
    }

    /**
     * Reloads a data resource from an iff file/buffer.
     *
     * @param source iff source to read from
     * @return the data resource
     */
    @SuppressWarnings("unchecked")
    public <T extends DataType> T reload(final Iff source) {
        final TemporaryCrcString sourceCrcString = new TemporaryCrcString(source.getFileName(), true);

        if (!loadedDataResourceMap.containsKey(sourceCrcString)) {
            LOGGER.warn("Trying to reload unloaded resource {}", source.getFileName());
            return null;
        }

        final T dataResource = (T) loadedDataResourceMap.get(sourceCrcString);

        if (dataResource != null && dataResource instanceof ObjectTemplate)
            ((ObjectTemplate) dataResource).loadFromIff(source);

        postFetch(dataResource);

        return dataResource;
    }

    /**
     * Changes the default tag->create function binding.
     *
     * @param id         the tag to change
     * @param createFunc the creation function to associate with the id
     * @return the old creation function associated with the tag
     */
    public BiFunction<String, DataResourceList<DataType>, DataType> assignBinding(int id, final BiFunction<String, DataResourceList<DataType>, DataType> createFunc) {
        final BiFunction<String, DataResourceList<DataType>, DataType> oldFunc = createDataResourceMap.get(id);
        createDataResourceMap.put(id, createFunc);
        return oldFunc;
    }

    /**
     * Removes the default tag->create function binding.
     *
     * @param id the tag to remove
     * @return the old creation function associated with the tag
     */
    public BiFunction<String, DataResourceList<DataType>, DataType> removeBinding(int id) {
        return createDataResourceMap.remove(id);
    }

    /**
     * internal callback method that can be overriden on an implementing class to do post fetch logic.
     *
     * @param objectTemplate The object template that was loaded.
     */
    protected void postFetch(final DataType objectTemplate) {
        //Do nothing but we don't want to force classes to implement.
    }
}
