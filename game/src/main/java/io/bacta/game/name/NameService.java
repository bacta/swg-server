package io.bacta.game.name;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public final class NameService {

    private static final Map<String, String> nameGenerators = new HashMap<>();

    static {
        nameGenerators.put("object/creature/player/human_male.iff", "human_male");
    }

    public String getNameGeneratorTypeForTemplate(final String creatureTemplate) throws NameGeneratorNotFoundException {
        //NOTE: This is just a holdover until we are loading server data files again.
        LOGGER.info("Name generator type for template {}", creatureTemplate);

        if (nameGenerators.containsKey(creatureTemplate))
            return nameGenerators.get(creatureTemplate);

        throw new NameGeneratorNotFoundException(creatureTemplate);
    }

    public String generateUniqueRandomName(final String nameGeneratorType) {
        LOGGER.info("Request for random name with generator type {}, but logic has not been filled out.", nameGeneratorType);

        return "Random Name";
    }
}
