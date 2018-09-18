package io.bacta.game.planet;

import io.bacta.game.context.GameRequestContext;
import io.bacta.game.message.planet.GetMapLocationsMessage;
import io.bacta.game.message.planet.GetMapLocationsResponseMessage;
import io.bacta.game.message.planet.MapLocation;
import io.bacta.game.message.planet.MapLocationType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;


/**
 * Planet service manages data for a single planet. There should be one per loaded planet.
 */

@Slf4j
@Service
public class PlanetService {
    //private final PlanetObject planetObject;

    private final PlanetMapLocationCache locationCache;

    public PlanetService() {
        //TODO: One planet service per planet...
        this.locationCache = new PlanetMapLocationCache();
    }

    public void sendMapLocationsTo(final GameRequestContext context, final GetMapLocationsMessage message) {
//        if (!planetName.equals(message.getPlanetName())) {
//            LOGGER.error("The wrong planet service [{}] was chosen to provide map locations for planet {}.",
//                    this.planetName,
//                    message.getPlanetName());
//            return;
        //
        // }
        //HACK: We want to send the member planet name when that is done.
        final String planetName = message.getPlanetName();

        final int staticLocationsVersion = locationCache.getCacheVersionForType(MapLocationType.STATIC);
        final int dynamicLocationsVersion = locationCache.getCacheVersionForType(MapLocationType.DYNAMIC);
        final int persistentLocationsVersion = locationCache.getCacheVersionForType(MapLocationType.PERSISTENT);

        final Collection<MapLocation> staticLocations = locationCache.getNewerThan(MapLocationType.STATIC, message.getStaticCacheVersion());
        final Collection<MapLocation> dynamicLocations = locationCache.getNewerThan(MapLocationType.DYNAMIC, message.getDynamicCacheVersion());
        final Collection<MapLocation> persistentLocations = locationCache.getNewerThan(MapLocationType.PERSISTENT, message.getPersistentCacheVersion());

        //TODO: Remove any opposing factional locations before sending.
        final GetMapLocationsResponseMessage response = new GetMapLocationsResponseMessage(
                planetName,
                staticLocations,
                dynamicLocations,
                persistentLocations,
                staticLocationsVersion,
                dynamicLocationsVersion,
                persistentLocationsVersion);

        context.sendMessage(response);
    }

    public void addMapLocation(final MapLocation location, final MapLocationType locationType, boolean enforceLocationCountLimits) {
        //ensureLocationIsValid(location, locationType);

        //Get the location map for the given type.
        //Check if this is a new location, or overwriting old.
        //if new, then check location limits. if exceeded, then don't add the location?
        //set the location in the map by its id.
        //remove the location from other maps if present in them.
        //increment the map location cache version.
    }

    public void removeMapLocation(final long networkId, final MapLocationType locationType) {
        //Get the location map for the type.
        //If the location is found in the map, then remove it.
        //increment the cache version.
    }

    public void removeMapLocation(final long networkId) {
        //find the map that the location is in.
        //if found, remove it from that map with overload call.
    }

    public Set<MapLocation> getMapLocations(MapLocationType locationType) {
        return null;
    }

    public Set<MapLocation> GetMapLocationsByCategories(MapLocationType locationType, byte category, byte subCategory) {
        return null;
    }
}
