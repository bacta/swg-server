package bacta.io.network;

import bacta.io.object.NetworkObject;

/**
 * Created by kyle on 6/4/2016.
 */
public interface NetworkObjectByteSerializer {
    <T extends NetworkObject> byte[] serialize(T object);
    <T extends NetworkObject> T deserialize(byte[] bytes);
}
