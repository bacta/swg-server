package io.bacta.login.server.rest.controller;

import io.bacta.engine.security.CryptoUtil;
import io.bacta.login.server.model.Galaxy;
import io.bacta.login.server.model.GalaxyStatusUpdate;
import io.bacta.login.server.rest.mapping.DetailedGalaxyStatusMapping;
import io.bacta.login.server.rest.model.DetailedGalaxyStatus;
import io.bacta.login.server.rest.model.GalaxyListEntry;
import io.bacta.login.server.rest.model.RegisterGalaxyRequest;
import io.bacta.login.server.rest.model.RegisteredGalaxyResponse;
import io.bacta.login.server.service.GalaxyNotFoundException;
import io.bacta.login.server.service.GalaxyRegistrationFailedException;
import io.bacta.login.server.service.GalaxyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/galaxies")
public final class GalaxiesController {
    private final GalaxyService galaxyService;

    public GalaxiesController(final GalaxyService galaxyService) {
        this.galaxyService = galaxyService;
    }

    /**
     * Gets a list of all the galaxies from the galaxy cluster.
     *
     * @return Returns a list of galaxies, or an empty list if none exist.
     */
    @GetMapping
    public ResponseEntity<?> galaxies() {
        final Collection<Galaxy> galaxies = galaxyService.getGalaxies();

        final Collection<GalaxyListEntry> galaxyList = galaxies.stream()
                .map(galaxy -> {
                    final GalaxyListEntry gle = new GalaxyListEntry(
                            galaxy.getName(),
                            galaxy.getAddress(),
                            galaxy.getPort(),
                            galaxy.getTimeZone());

                    gle.setStatus(galaxyService.determineGalaxyStatus(galaxy, false, false));
                    gle.setPopulation(galaxyService.determineGalaxyPopulationStatus(galaxy));
                    gle.setOnlinePlayers(galaxy.getOnlinePlayers());
                    gle.setMaxOnlinePlayers(galaxy.getOnlinePlayerLimit());
                    //gle.setUptime(0); //TODO: Track uptime?

                    return gle;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(galaxyList);
    }

    /**
     * Gets the details of a specific galaxy from the galaxy cluster.
     * @param name The name of the galaxy.
     * @return The galaxy details. 404 if galaxy does not exist.
     */
    @GetMapping("/{name}")
    public ResponseEntity<?> galaxy(@PathVariable String name) {
//        final Galaxy galaxy = galaxyService.getGalaxyById(id);
//
//        if (galaxy == null)
//            return ResponseEntity.notFound().build();
//
//        return ResponseEntity.ok(galaxy);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/{name}")
    public ResponseEntity<?> updateGalaxy(
            @PathVariable String name,
            @RequestBody DetailedGalaxyStatus galaxyStatus) {
        try {
            LOGGER.info("Updating galaxy with name {}", name);

            final GalaxyStatusUpdate update = DetailedGalaxyStatusMapping.map(galaxyStatus);
            galaxyService.updateGalaxyStatus(name, update);

            return ResponseEntity.ok().build();

        } catch (GalaxyNotFoundException ex) {
            LOGGER.warn("Could not update galaxy with name {} because it did not exist.", name);
            return ResponseEntity.notFound().build();

        } catch (GalaxyRegistrationFailedException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<?> galaxy(@RequestBody GalaxyListEntry galaxy) {
//        return ResponseEntity.ok(galaxy);
//    }

    @DeleteMapping("/{name}")
    public ResponseEntity<?> deleteGalaxy(@PathVariable String name) {
        //galaxyService.unregisterGalaxy(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<?> registerGalaxy(@RequestBody RegisterGalaxyRequest galaxy) {
        try {
            LOGGER.info("Registering galaxy {}", galaxy.getName());

            final Galaxy createdGalaxy = galaxyService.registerGalaxy(
                    galaxy.getName(), galaxy.getAddress(), galaxy.getPort(), galaxy.getTimeZone());

            //The location where this galaxy's public information lives.
            final URI location = ServletUriComponentsBuilder
                    .fromCurrentRequest()
                    .path("/{name}")
                    .buildAndExpand(createdGalaxy.getName())
                    .toUri();

            //Convert the bytes to a public key.
            final Key publicKey = KeyFactory.getInstance("RSA").generatePublic(
                    new X509EncodedKeySpec(createdGalaxy.getPublicKey()));

            //Create our view model response.
            final RegisteredGalaxyResponse response = new RegisteredGalaxyResponse(
                    createdGalaxy.getName(),
                    CryptoUtil.toPem(publicKey));

            return ResponseEntity.created(location).body(response);

        } catch (GalaxyRegistrationFailedException ex) {
            LOGGER.warn("Attempted to register new galaxy, but failed.");
            return ResponseEntity.badRequest().body(ex.getMessage());

        } catch (Exception ex) {
            LOGGER.error("Registration failed.", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
