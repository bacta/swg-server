package io.bacta.game.object.attributes;

import io.bacta.swg.object.AttributeList;

public interface AttributeListAppender<T> {
    void append(T object, AttributeList attributeList);
}
