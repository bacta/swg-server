package io.bacta.game.player.creation;

import io.bacta.game.object.tangible.creature.CreatureObject;
import io.bacta.swg.math.Vector;
import org.springframework.stereotype.Service;

import java.util.Random;

import static java.lang.Math.abs;

/**
 * Created by kyle on 5/9/2016.
 */
@Service
public class NewbieTutorialService {

    private final String sceneId = "tutorial";
    private final String tutorialTemplate = "object/building/general/npe_hangar_1.iff";

    private final Vector startCoords = new Vector(-12.5f, 0.0f, 19.5f);
    private final String startCellName = "medicalroom";

    private final float tutorialMapWidth = 16384.0f;
    private final float tutorialSpacing = 512.0f;
    private final int sqrtMaxTutorials = (int) (tutorialMapWidth / tutorialSpacing);

    private final String tutorialObjVar = "npe.phase_number";
    private final String skipTutorialObjVar = "npe.skippingTutorial";
    private final int tutorialResetThreshold = 2;

    public Vector getTutorialLocation() {
        // Pick a random spot, keep them 512m apart, and don't let them get too close to either axis because we are
        // now using them as server boundaries for multi-server
        float x, z;
        do {
            Random random = new Random();
            x = tutorialSpacing * random.nextInt(sqrtMaxTutorials - 1) - tutorialMapWidth / 2.0f;
            z = tutorialSpacing * random.nextInt(sqrtMaxTutorials - 1) - tutorialMapWidth / 2.0f;
        } while (abs(x) < 300.0f || abs(z) < 300.0f);

        return new Vector(x, 0.0f, z);
    }

    // TODO: ObjVars
    public void setupCharacterForTutorial(final CreatureObject character) {
//        if (character != null)
//            character.setObjVarItem(tutorialObjVar, 1);
    }

    public void setupCharacterToSkipTutorial(final CreatureObject character) {
//        if (character != null)
//            character.setObjVarItem(skipTutorialObjVar, 1);
    }
}
