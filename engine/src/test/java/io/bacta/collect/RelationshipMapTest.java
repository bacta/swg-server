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

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by crush on 5/29/2016.
 */
public class RelationshipMapTest {
    @Test
    public void shouldAddRelationship() {
        final RelationshipMap<String> relationshipMap = new RelationshipMap<>();
        relationshipMap.add("a", "b");

        Assert.assertTrue(relationshipMap.hasRelationshipWith("a", "b"));
        Assert.assertFalse(relationshipMap.hasRelationshipWith("b", "a"));
    }

    @Test
    public void shouldRemoveRelationship() {
        final RelationshipMap<String> relationshipMap = new RelationshipMap<>();
        relationshipMap.add("a", "b");
        relationshipMap.add("b", "a");
        relationshipMap.remove("a", "b");

        Assert.assertFalse(relationshipMap.hasRelationshipWith("a", "b"));
        Assert.assertTrue(relationshipMap.hasRelationshipWith("b", "a"));
    }

    @Test
    public void shouldHaveMutualRelationship() {
        final RelationshipMap<String> relationshipMap = new RelationshipMap<>();
        relationshipMap.add("a", "b");
        relationshipMap.add("b", "a");

        Assert.assertTrue(relationshipMap.isMutual("a", "b"));
        Assert.assertTrue(relationshipMap.isMutual("b", "a"));
        Assert.assertFalse(relationshipMap.isMutual("a", "a"));
        Assert.assertFalse(relationshipMap.isMutual("b", "b"));
    }

    @Test
    public void shouldNotHaveMutualRelationship() {
        final RelationshipMap<String> relationshipMap = new RelationshipMap<>();
        relationshipMap.add("a", "b");

        Assert.assertFalse(relationshipMap.isMutual("a", "b"));
    }

    @Test
    public void shouldGetAllRelationships() {
        final RelationshipMap<String> relationshipMap = new RelationshipMap<>();
        relationshipMap.add("a", "b");
        relationshipMap.add("a", "c");
        relationshipMap.add("a", "d");

        relationshipMap.add("b", "a");
        relationshipMap.add("b", "c");

        relationshipMap.add("c", "a");

        Assert.assertEquals(3, relationshipMap.getRelationships("a").size());
        Assert.assertEquals(2, relationshipMap.getRelationships("b").size());
        Assert.assertEquals(1, relationshipMap.getRelationships("c").size());
        Assert.assertEquals(0, relationshipMap.getRelationships("d").size());
    }

    @Test
    public void shouldGetAllMutualRelationships() {
        final RelationshipMap<String> relationshipMap = new RelationshipMap<>();
        relationshipMap.add("a", "b");
        relationshipMap.add("a", "c");
        relationshipMap.add("a", "d");

        relationshipMap.add("b", "a");
        relationshipMap.add("b", "c");

        relationshipMap.add("c", "a");

        Assert.assertEquals(2, relationshipMap.getMutualRelationships("a").size());
        Assert.assertEquals(1, relationshipMap.getMutualRelationships("b").size());
        Assert.assertEquals(1, relationshipMap.getMutualRelationships("c").size());
        Assert.assertEquals(0, relationshipMap.getMutualRelationships("d").size());
    }

    @Test
    public void shouldGetAllReverseRelationships() {
        final RelationshipMap<String> relationshipMap = new RelationshipMap<>();
        relationshipMap.add("a", "b");
        relationshipMap.add("a", "c");
        relationshipMap.add("a", "d");

        relationshipMap.add("b", "a");
        relationshipMap.add("b", "c");

        relationshipMap.add("c", "a");

        Assert.assertEquals(2, relationshipMap.getReverseRelationships("a").size());
        Assert.assertEquals(1, relationshipMap.getReverseRelationships("b").size());
        Assert.assertEquals(2, relationshipMap.getReverseRelationships("c").size());
        Assert.assertEquals(1, relationshipMap.getReverseRelationships("d").size());
    }

    @Test
    public void shouldNotHaveRelationshipToSelf() {
        final RelationshipMap<String> relationshipMap = new RelationshipMap<>();
        relationshipMap.add("a", "a");

        Assert.assertEquals(0, relationshipMap.getRelationships("a").size());
    }
}
