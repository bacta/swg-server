package io.bacta.game.db;

import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.bacta.game.object.ServerObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class InMemoryServerObjectDatabase {
    private final TLongObjectMap<ServerObject> objectLookup;

    public InMemoryServerObjectDatabase() {
        this.objectLookup = new TLongObjectHashMap<>(100000);
    }

    @SuppressWarnings("unchecked")
    public <T extends ServerObject> T lookup(long networkId) {
        return (T) objectLookup.get(networkId);
    }

    public void put(ServerObject object) {
        objectLookup.put(object.getNetworkId(), object);
    }

    public void remove(long networkId) {
        objectLookup.remove(networkId);
    }
}
