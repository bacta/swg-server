package io.bacta.game.object.attributes;

import io.bacta.game.object.ServerObject;
import io.bacta.swg.object.AttributeList;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
@AppendsAttributesFor(ServerObject.class)
public final class ServerObjectAttributeListAppender implements AttributeListAppender<ServerObject> {
    @Override
    public void append(ServerObject object, AttributeList attributeList) {
        //Trigger scripts
    }
}
