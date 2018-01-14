package io.bacta.login.server.rest.controller;

import io.bacta.login.server.service.GalaxyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/galaxies")
public final class GalaxiesController {
    private final GalaxyService galaxyService;

    public GalaxiesController(GalaxyService galaxyService) {
        this.galaxyService = galaxyService;
    }

    /**
     * Gets a list of all the galaxies from the galaxy cluster.
     *
     * @return Returns a list of galaxies, or an empty list if none exist.
     */
    @GetMapping
    public ResponseEntity<?> galaxies() {
        return ResponseEntity.ok(galaxyService.getGalaxies());
    }

    /**
     * Gets the details of a specific galaxy from the galaxy cluster.
     * @param id The id of the galaxy.
     * @return The galaxy details. 404 if galaxy does not exist.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> galaxy(@PathVariable int id) {
        //GalaxyListEntry galaxy = galaxyService.getGalaxyById
        return ResponseEntity.ok(null);
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<?> galaxy(@RequestBody GalaxyListEntry galaxy) {
//        return ResponseEntity.ok(galaxy);
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGalaxy(@PathVariable int id) {
        return ResponseEntity.ok().build();
    }
//
//    @PostMapping
//    public ResponseEntity<?> createGalaxy(@RequestBody CreateGalaxyRequest galaxy) {
//        LOGGER.info("Creating galaxy {}", galaxy.getName());
//
//        GalaxyListEntry result = new GalaxyListEntry(1, galaxy.getName());
//
//        URI location = ServletUriComponentsBuilder
//                .fromCurrentRequest()
//                .path("/{id}")
//                .buildAndExpand(result.getId())
//                .toUri();
//
//        return ResponseEntity.created(location).body(result);
//    }
}
