package io.bacta.game.name;

import io.bacta.game.object.tangible.creature.Gender;
import io.bacta.game.object.tangible.creature.Race;
import io.bacta.shared.localization.StringId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static io.bacta.game.name.NameErrors.APPROVED;

@Slf4j
@Service
public final class NameService {
    private static final Map<String, String> nameGenerators = new HashMap<>();

    public static final int PLAYER = 1;
    public static final int CREATURE = 2;
    public static final int RESOURCE = 3;

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

    public StringId validate(final int type, int accountId, final String name, final Race race, final Gender gender) {
        return APPROVED;
    }

    public void addPlayerName(final String firstName) {

    }

    public StringId verifyAndLock(final String name, final int accountId, final Race race, final Gender gender) {
        return APPROVED;
    }
}
