package io.bacta.game.planet;

import com.google.common.collect.ImmutableList;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.bacta.game.message.planet.GetMapLocationsMessage;
import io.bacta.game.message.planet.MapLocation;
import io.bacta.game.message.planet.MapLocationType;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The planet map location cache keeps locations in memory, and can help limit the bandwidth to the client.
 * The client sends a version in the {@link GetMapLocationsMessage}. If that version is older than the version in the
 * cache, then the locations for that type are sent. Otherwise, they will be omitted.
 */
public final class PlanetMapLocationCache {
    //These versions start at 1 so that the client will always request an update on its first time request.
    private final AtomicInteger staticLocationsVersion = new AtomicInteger(1);
    private final AtomicInteger dynamicLocationsVersion = new AtomicInteger(1);
    private final AtomicInteger persistentLocationsVersion = new AtomicInteger(1);

    private final TLongObjectMap<MapLocation> staticLocations = new TLongObjectHashMap<>();
    private final TLongObjectMap<MapLocation> dynamicLocations = new TLongObjectHashMap<>();
    private final TLongObjectMap<MapLocation> persistentLocations = new TLongObjectHashMap<>();


    public Collection<MapLocation> getNewerThan(final MapLocationType locationType, final int cacheVersion) {
        final int typedCacheVersion = getCacheVersionForType(locationType);

        if (cacheVersion != 0 && typedCacheVersion <= cacheVersion)
            return Collections.emptyList();

        final Collection<MapLocation> typedLocations = getLocationsForType(locationType);

        return ImmutableList.copyOf(typedLocations);
    }

    public Collection<MapLocation> getLocationsForType(final MapLocationType locationType) {
        switch (locationType) {
            case STATIC:
                return staticLocations.valueCollection();
            case DYNAMIC:
                return dynamicLocations.valueCollection();
            case PERSISTENT:
                return persistentLocations.valueCollection();
            default:
                throw new IllegalArgumentException("Unknown map location type.");
        }
    }

    public int getCacheVersionForType(final MapLocationType type) {
        switch (type) {
            case STATIC:
                return staticLocationsVersion.get();
            case DYNAMIC:
                return dynamicLocationsVersion.get();
            case PERSISTENT:
                return persistentLocationsVersion.get();
            default:
                throw new IllegalArgumentException("Unknown map location type.");
        }
    }
}
