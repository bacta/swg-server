package bacta.io.service.objectfactory;

import bacta.io.object.NetworkObject;

/**
 * Created by kburkhardt on 2/23/14.
 */
public interface NetworkObjectFactory<Object extends NetworkObject, Template> {
    <O extends Object> Object createNetworkObject(Class<O> clazz, Template template);
}
