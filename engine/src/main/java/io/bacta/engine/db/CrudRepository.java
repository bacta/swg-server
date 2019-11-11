/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.bacta.engine.db;

import java.io.Serializable;

/**
 * Created by kyle on 4/3/2017.
 */
public interface CrudRepository<K, V extends Serializable> {

    /**
     * Number of entries in the repository
     * @return number of entries
     */
    long count();

    /**
     * Delete a single specific entry from the repository
     * @param key Key of item to delete
     */
    void delete(K key);

    /**
     * Delete a group of entries from the repository
     * @param entries Group of entries to delete
     */
    void delete(Iterable<? extends V> entries);

    /**
     * Delete single specific value in repository
     * @param entry entry to delete
     */
    void delete(V entry);

    /**
     * Delete all entries in the repository
     */
    void deleteAll();

    /**
     * Verify key exists in the repository
     * @param key
     * @return true if key is found in repository
     */
    boolean exists(K key);

    /**
     * Get all values held in repository
     * @return Iterable of contained values
     */
    Iterable<V> findAll();

    /**
     * Find a group of items in a repository
     * @param keys keys to fine
     * @return interable of matching items
     */
    Iterable<V> findAll(Iterable<K> keys);

    /**
     * Find single entry by key
     * @param key key of entry to find
     * @return
     */
    V findOne(K key);

    /**
     * Save a group of entries
     * @param entries entries to save
     * @param <S> Type of entry
     * @return list of entries saved
     */
    <S extends V> Iterable<S> save(Iterable<S> entries);

    /**
     * Save single entry
     * @param entry entry to save
     * @param <S> Type of entry
     * @return entry saved
     */
    <S extends V> S save(S entry);
}
