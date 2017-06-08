package com.ocdsoft.bacta.engine.io.db;

import java.io.Serializable;

/**
 * Created by kyle on 4/3/2017.
 */
public interface CrudRepository<Key, Value extends Serializable> {
    /**
     * Number of entries in the repository
     * @return number of entries
     */
    long count();

    /**
     * Delete a single specific entry from the repository
     * @param key Key of item to delete
     */
    void delete(Key key);

    /**
     * Delete a group of entries from the repository
     * @param entries Group of entries to delete
     */
    void delete(Iterable<? extends Value> entries);

    /**
     * Delete single specific value in repository
     * @param entry entry to delete
     */
    void delete(Value entry);

    /**
     * Delete all entries in the repository
     */
    void deleteAll();

    /**
     * Verify key exists in the repository
     * @param key
     * @return true if key is found in repository
     */
    boolean exists(Key key);

    /**
     * Get all values held in repository
     * @return Iterable of contained values
     */
    Iterable<Value> findAll();

    /**
     * Find a group of items in a repository
     * @param keys keys to fine
     * @return interable of matching items
     */
    Iterable<Value> findAll(Iterable<Key> keys);

    /**
     * Find single entry by key
     * @param key key of entry to find
     * @return
     */
    Value findOne(Key key);

    /**
     * Save a group of entries
     * @param entries entries to save
     * @param <S> Type of entry
     * @return list of entries saved
     */
    <S extends Value> Iterable<S> save(Iterable<S> entries);

    /**
     * Save single entry
     * @param entry entry to save
     * @param <S> Type of entry
     * @return entry saved
     */
    <S extends Value> S save(S entry);
}
