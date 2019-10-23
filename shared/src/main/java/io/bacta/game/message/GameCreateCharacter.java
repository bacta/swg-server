package io.bacta.game.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class GameCreateCharacter {
    private final int accountId;
    private final String appearanceData;
    private final String characterName;
    private final String templateName;
    private final String startingLocation;
    private final String hairTemplateName;
    private final String hairAppearanceData;
    private final String profession;
    private final boolean jedi;
    private final float scaleFactor;
    private final String biography;
    private final boolean useNewbieTutorial;
    private final String skillTemplate;
    private final String workingSkill;

    public GameCreateCharacter(int accountId, ClientCreateCharacter createMessage) {
        this.accountId = accountId;
        this.appearanceData = createMessage.getAppearanceData();
        this.characterName = createMessage.getCharacterName();
        this.templateName = createMessage.getTemplateName();
        this.startingLocation = createMessage.getStartingLocation();
        this.hairTemplateName = createMessage.getHairTemplateName();
        this.hairAppearanceData = createMessage.getHairAppearanceData();
        this.profession = createMessage.getProfession();
        this.jedi = createMessage.isJedi();
        this.scaleFactor = createMessage.getScaleFactor();
        this.biography = createMessage.getBiography();
        this.useNewbieTutorial = createMessage.isUseNewbieTutorial();
        this.skillTemplate = createMessage.getSkillTemplate();
        this.workingSkill = createMessage.getWorkingSkill();
    }
}
