package com.ocdsoft.bacta.engine.service.objectfactory;

import com.ocdsoft.bacta.engine.object.NetworkObject;

/**
 * Created by kburkhardt on 2/23/14.
 */
public interface NetworkObjectFactory<Object extends NetworkObject, Template> {
    <O extends Object> Object createNetworkObject(Class<O> clazz, Template template);
}
