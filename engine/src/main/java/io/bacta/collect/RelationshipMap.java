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

package io.bacta.collect;

import com.google.common.collect.*;

import java.util.Set;

/**
 * Created by crush on 5/29/2016.
 * <p>
 * Tracks the forward and reverse relationships between T objects.
 *
 * @param <T> The type of objects of which to track relationships.
 */
public class RelationshipMap<T> {
    private final SetMultimap<T, T> forwardLookup;
    private final SetMultimap<T, T> reverseLookup;

    public RelationshipMap() {
        this.forwardLookup = Multimaps.synchronizedSetMultimap(HashMultimap.create());
        this.reverseLookup = Multimaps.synchronizedSetMultimap(HashMultimap.create());
    }

    /**
     * Adding a relationship from a to b.
     *
     * @param a The T declaring a relationship to T b. This relationship might not be mutual.
     * @param b The T to which the relationship is being declared.
     */
    public synchronized void add(final T a, final T b) {
        //Can't add a relationship to yourself.
        if (a.equals(b))
            return;

        this.forwardLookup.put(a, b);
        this.reverseLookup.put(b, a);
    }

    /**
     * Removes a relationship for Avatar A with Avatar B. If Avatar B has a relationship with Avatar A, it will be
     * unaffected by this operation.
     *
     * @param avatarId        The avatar that is removing its relationship with another avatar.
     * @param relatedAvatarId The avatar that is being removed from the relationship.
     */
    public synchronized void remove(final T avatarId, final T relatedAvatarId) {
        //Can't remove yourself because you can't add yourself.
        if (avatarId.equals(relatedAvatarId))
            return;

        this.forwardLookup.remove(avatarId, relatedAvatarId);
        this.reverseLookup.remove(relatedAvatarId, avatarId);
    }

    /**
     * Removes all the forward lookup entries for the given avatar. Any reverse lookups for the avatar will remain.
     *
     * @param avatarId The avatar whos forward relationships will be removed.
     */
    public synchronized void removeAll(final T avatarId) {
        this.forwardLookup.removeAll(avatarId);
    }

    /**
     * Checks to see if a mutual relationship exists between two avatars. Avatar A must have a relationship to Avatar B,
     * and Avatar B must have a relationship to Avatar A.
     *
     * @param avatarId          The first avatar to check.
     * @param associateAvatarId The second avatar to check.
     * @return True if both avatars have a relationship to the other. Otherwise, false.
     */
    public synchronized boolean isMutual(final T avatarId, final T associateAvatarId) {
        //Can't have a mutual relationship with yourself.
        if (avatarId.equals(associateAvatarId))
            return false;

        //We could check the reverse lookup for avatarId or the forward lookup for associateAvatarId here. I chose to
        //use the forward lookup for both.
        return this.forwardLookup.get(avatarId).contains(associateAvatarId) &&
                this.forwardLookup.get(associateAvatarId).contains(avatarId);
    }

    /**
     * Checks for the existence of a forward relationship between two avatars. Avatar A must have a relationship to
     * Avatar B, but the relationship does not have to be mutual.
     *
     * @param avatarId          The avatar who should have a relationship with the other avatar.
     * @param associateAvatarId The avatar for which the relationship should be defined, but does not have to be mutual.
     * @return True if the avatar has a relationship defined for the other avatar. Otherwise, false.
     */
    public synchronized boolean hasRelationshipWith(final T avatarId, final T associateAvatarId) {
        //Can't have a relationship with yourself.
        if (avatarId.equals(associateAvatarId))
            return false;

        return this.forwardLookup.get(avatarId).contains(associateAvatarId);
    }

    /**
     * Gets a set of all the avatars to which the specified avatar has added a relationship. The relationship does not
     * need to be mutual to be included.
     *
     * @param avatarId The avatar for which to find forward relationships.
     * @return An immutable set of avatars for which a relationship has been added by the specified avatar. If none
     * have been added, then an empty set is returned.
     */
    public synchronized Set<T> getRelationships(final T avatarId) {
        return ImmutableSet.copyOf(this.forwardLookup.get(avatarId));
    }

    /**
     * Gets a set of all the avatars that have added a relationship to the specified avatar.
     *
     * @param avatarId The avatar for which to find reverse relationships.
     * @return An immutable set of avatars that have added a relationship to the specified avatar. If none have added
     * a relationship to the avatar, then an empty set is returned.
     */
    public synchronized Set<T> getReverseRelationships(final T avatarId) {
        return ImmutableSet.copyOf(this.reverseLookup.get(avatarId));
    }

    /**
     * Gets a set of all the mutual relationships for the specified avatar.
     *
     * @param avatarId The avatar for which to get the mutual relationships.
     * @return An immutable set of avatars which have a mutual relationship defined with the avatar. If no mutual
     * relationships exist, then an empty set is returned.
     */
    public synchronized Set<T> getMutualRelationships(final T avatarId) {
        return Sets.intersection(this.forwardLookup.get(avatarId), this.reverseLookup.get(avatarId))
                .immutableCopy();
    }
}
