package io.bacta.game.player.creation;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.io.Files;
import gnu.trove.list.TIntList;
import io.bacta.engine.conf.BactaConfiguration;
import io.bacta.engine.utils.StringUtil;
import io.bacta.game.chat.GameChatService;
import io.bacta.game.container.ContainerTransferService;
import io.bacta.game.context.GameRequestContext;
import io.bacta.game.message.ClientCreateCharacter;
import io.bacta.game.message.ClientCreateCharacterFailed;
import io.bacta.game.message.ClientCreateCharacterSuccess;
import io.bacta.game.name.NameErrors;
import io.bacta.game.name.NameService;
import io.bacta.game.object.ObjectTemplateService;
import io.bacta.game.object.ServerObject;
import io.bacta.game.object.ServerObjectService;
import io.bacta.game.object.intangible.player.PlayerObject;
import io.bacta.game.object.tangible.TangibleObject;
import io.bacta.game.object.tangible.creature.Attribute;
import io.bacta.game.object.tangible.creature.CreatureObject;
import io.bacta.game.object.tangible.creature.Gender;
import io.bacta.game.object.tangible.creature.Race;
import io.bacta.game.object.template.server.ServerCreatureObjectTemplate;
import io.bacta.game.player.BiographyService;
import io.bacta.shared.object.template.SharedObjectTemplate;
import io.bacta.swg.collision.CollisionProperty;
import io.bacta.swg.container.ContainerResult;
import io.bacta.swg.foundation.ConstCharCrcLowerString;
import io.bacta.swg.localization.StringId;
import io.bacta.swg.math.Transform;
import io.bacta.swg.math.Vector;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CharacterCreationService {
    public static final String CONFIG_SECTION = "Bacta/GameServer/CharacterCreation";

    private static final ConstCharCrcLowerString GHOST_SLOT_NAME = new ConstCharCrcLowerString("ghost");
    private static final ConstCharCrcLowerString INVENTORY_SLOT_NAME = new ConstCharCrcLowerString("inventory");
    private static final ConstCharCrcLowerString DATAPAD_SLOT_NAME = new ConstCharCrcLowerString("datapad");
    private static final ConstCharCrcLowerString BANK_SLOT_NAME = new ConstCharCrcLowerString("bank");
    private static final ConstCharCrcLowerString MISSION_BAG_SLOT_NAME = new ConstCharCrcLowerString("mission_bag");
    private static final ConstCharCrcLowerString APPEARANCE_SLOT_NAME = new ConstCharCrcLowerString("appearance_inventory");

    private static final String GHOST_TEMPLATE = "object/player/player.iff";
    private static final String INVENTORY_TEMPLATE = "object/tangible/inventory/character_inventory.iff";
    private static final String DATAPAD_TEMPLATE = "object/tangible/datapad/character_datapad.iff";
    private static final String MISSION_BAG_TEMPLATE = "object/tangible/mission_bag/mission_bag.iff";
    private static final String BANK_TEMPLATE = "object/tangible/bank/character_bank.iff";
    private static final String APPEARANCE_TEMPLATE = "object/tangible/inventory/appearance_inventory.iff";

    private final ServerObjectService serverObjectService;
    private final ProfessionDefaultsService professionDefaultsService;
    private final ObjectTemplateService objectTemplateService;
    private final NameService nameService;
    private final StartingLocations startingLocations;
    private final NewbieTutorialService newbieTutorialService;
    private final BiographyService biographyService;
    private final GameChatService chatService;
    private final AllowBaldService allowBaldService;
    private final HairStylesService hairStylesService;
    private final ContainerTransferService containerTransferService;
    private final AttributeLimitsService attributeLimitsService;
    private final ProfessionModsService professionModsService;
    private final RacialModsService racialModsService;

    private final Cache<String, Integer> pendingCreations;
    private final String defaultProfession;
    private final Set<String> disabledProfessions;

    @Inject
    public CharacterCreationService(final ServerObjectService serverObjectService,
                                    final ProfessionDefaultsService professionDefaultsService,
                                    final NameService nameService,
                                    final ObjectTemplateService objectTemplateService,
                                    final StartingLocations startingLocations,
                                    final NewbieTutorialService newbieTutorialService,
                                    final AllowBaldService allowBaldService,
                                    final HairStylesService hairStylesService,
                                    final GameChatService chatService,
                                    final BiographyService biographyService,
                                    final ContainerTransferService containerTransferService,
                                    final AttributeLimitsService attributeLimitsService,
                                    final ProfessionModsService professionModsService,
                                    final RacialModsService racialModsService,
                                    final BactaConfiguration bactaConfiguration) {

        this.serverObjectService = serverObjectService;
        this.professionDefaultsService = professionDefaultsService;
        this.objectTemplateService = objectTemplateService;
        this.startingLocations = startingLocations;
        this.newbieTutorialService = newbieTutorialService;
        this.chatService = chatService;
        this.allowBaldService = allowBaldService;
        this.biographyService = biographyService;
        this.nameService = nameService;
        this.containerTransferService = containerTransferService;
        this.attributeLimitsService = attributeLimitsService;
        this.professionModsService = professionModsService;
        this.racialModsService = racialModsService;
        this.hairStylesService = hairStylesService;

        this.disabledProfessions = new HashSet<>(bactaConfiguration.getStringCollectionWithDefault(
                CONFIG_SECTION, "disabledProfession", Collections.emptySet()));

        this.defaultProfession = bactaConfiguration.getStringWithDefault(
                CONFIG_SECTION, "defaultProfession", "crafting_artisan");

        this.pendingCreations = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    }

    public void createCharacter(GameRequestContext context, ClientCreateCharacter createMessage) {
        //TODO: Account verification.
        final int accountId = 1;
        final String username = "crush";

        final String serverTemplateName = createMessage.getTemplateName();
        final String speciesGender = Files.getNameWithoutExtension(serverTemplateName);
        final Gender gender = Gender.fromSpeciesGender(speciesGender);
        final Race race = Race.fromSpeciesGender(speciesGender);

        final String profession;

        if (createMessage.getProfession().isEmpty() || this.disabledProfessions.contains(createMessage.getProfession())) {
            profession = this.defaultProfession;
        } else {
            profession = createMessage.getProfession();
        }

        final String firstName = StringUtil.getFirstWord(createMessage.getCharacterName()).toLowerCase();

        //Validate the name.
        StringId result = nameService.validate(NameService.PLAYER, accountId, createMessage.getCharacterName(), race, gender);

        //Override for developers to use developer names if it is the same name as their account.
        if (result.equals(NameErrors.DEVELOPER) && firstName.equalsIgnoreCase(username)) {
            result = NameErrors.APPROVED;
        }

        //If approval failed, then send character created failed message.
        if (!NameErrors.APPROVED.equals(result)) {
            ClientCreateCharacterFailed failed = new ClientCreateCharacterFailed(createMessage.getCharacterName(), result);
            context.sendMessage(failed);

            return;
        }

        final ServerCreatureObjectTemplate objectTemplate = objectTemplateService.getObjectTemplate(serverTemplateName);

        if (objectTemplate == null) {
            LOGGER.error("Account {} attempted to create a player with invalid player template {}.",
                    username,
                    createMessage.getTemplateName());

            context.sendMessage(new ClientCreateCharacterFailed(createMessage.getCharacterName(), NameErrors.NO_TEMPLATE));
            return;
        }

        //If someone else is already creating a character with this name, then we need to reject the attempt to create.
        if (pendingCreations.getIfPresent(firstName) != null) {
            ClientCreateCharacterFailed failed = new ClientCreateCharacterFailed(createMessage.getCharacterName(), NameErrors.IN_USE);
            context.sendMessage(failed);
            return;
        }

        pendingCreations.put(firstName, accountId);

        final CreatureObject playerCreature = serverObjectService.createObject(objectTemplate.getResourceName());

        if (playerCreature == null) {
            LOGGER.error("Account {} attempted to create a character but the object service failed to create it. {}",
                    username,
                    createMessage.getTemplateName());

            context.sendMessage(new ClientCreateCharacterFailed(createMessage.getCharacterName(), NameErrors.CANT_CREATE_AVATAR));
            return;
        }

        applyScaleLimits(playerCreature, createMessage.getScaleFactor());
        playerCreature.setObjectName(createMessage.getCharacterName());
        playerCreature.setOwnerId(playerCreature.getNetworkId());
        playerCreature.setPlayerControlled(true);

        StartingLocations.StartingLocationInfo startingLocationInfo = startingLocations.getStartingLocationInfo(createMessage.getStartingLocation());

        final Transform transform = new Transform();
        if (createMessage.isUseNewbieTutorial()) {
            newbieTutorialService.setupCharacterForTutorial(playerCreature);
            final Vector newbieTutorialLocation = newbieTutorialService.getTutorialLocation();
            transform.setPositionInParentSpace(newbieTutorialLocation);
        } else {
            newbieTutorialService.setupCharacterToSkipTutorial(playerCreature);

            //TODO: Position should be a random point within the radius of these coordinates.
            final Vector position = new Vector(
                    startingLocationInfo.getX(),
                    startingLocationInfo.getY(),
                    startingLocationInfo.getZ());

            transform.setPositionInParentSpace(position);
            transform.yaw(startingLocationInfo.getHeading());
        }

        playerCreature.setTransformObjectToParent(transform);

        final CollisionProperty collision = playerCreature.getCollisionProperty();

        if (collision != null)
            collision.setPlayerControlled(true);

        final TangibleObject tangibleObject = TangibleObject.asTangibleObject(playerCreature);

        if (tangibleObject != null)
            tangibleObject.setAppearanceData(createMessage.getAppearanceData());

        final String hairStyleTemplate;

        //Validate the hair style. If it is invalid, then use the default hair template.
        if (!isValidHairSelection(createMessage.getHairTemplateName(), speciesGender)) {
            hairStyleTemplate = hairStylesService.getDefaultHairStyle(speciesGender);

            LOGGER.error("{} used an invalid hair style {} for species/gender {}.",
                    username, createMessage.getHairTemplateName(), speciesGender);
        } else {
            hairStyleTemplate = createMessage.getHairTemplateName();
        }

        // hair equip hack - lives on
        if (!hairStyleTemplate.isEmpty()) {
            final ServerObject hair = serverObjectService.createObject(createMessage.getHairTemplateName(), playerCreature);
            assert hair != null : String.format("Could not create hair %s\n", createMessage.getHairTemplateName());

            final TangibleObject tangibleHair = TangibleObject.asTangibleObject(hair);

            assert tangibleHair != null : "Hair is not tangible, wtf.  Can't customize it.  (among other things, probably)...";

            tangibleHair.setAppearanceData(createMessage.getHairAppearanceData());
        }

        setupPlayer(playerCreature, speciesGender, profession, createMessage.isJedi());

        if (!createMessage.getBiography().isEmpty())
            biographyService.setBiography(playerCreature, createMessage.getBiography());

        final PlayerObject play = serverObjectService.createObject("object/player/player.iff", playerCreature);

        assert play != null : String.format("%d unable to create player object for new character %s", accountId, playerCreature.getNetworkId());

        play.setStationId(accountId);
        play.setBornDate((int) Instant.now().getEpochSecond());
        play.setSkillTemplate(createMessage.getSkillTemplate(), true);
        play.setWorkingSkill(createMessage.getWorkingSkill(), true);

        playerCreature.setSceneIdOnThisAndContents(startingLocationInfo.getPlanet());

        // Persist object (Done in Object Manager)

        //Post character setup.
//        final CharacterInfo info = new CharacterInfo(
//                playerCreature.getAssignedObjectName(),
//                SOECRC32.hashCode(playerCreature.getObjectTemplateName()),
//                playerCreature.getNetworkId(),
//                gameServerState.getClusterId(),
//                CharacterInfo.Type.NORMAL,
//                false
//        );

        //nameService.addPlayerName(firstName);

        context.sendMessage(new ClientCreateCharacterSuccess(playerCreature.getNetworkId()));

        pendingCreations.invalidate(firstName);

        chatService.destroyAvatar(firstName);
    }

    public void setupPlayer(final CreatureObject creatureObject, final String speciesGender, final String profession, final boolean jedi) {
        final String sharedTemplateName = creatureObject.getSharedTemplate().getResourceName();
        final ProfessionInfo professionInfo = professionDefaultsService.getDefaults(profession);

        createStartingEquipment(creatureObject, sharedTemplateName, professionInfo);
        createRequiredSlots(creatureObject);

        applyAttributeMods(creatureObject, speciesGender, profession);
    }

    /**
     * Validates that the selected hair style belongs to the selected species gender. If no hair style is selected, then
     * checks to make sure that the character may be bald. SOE didn't perform this check, but we learned at SWGEmu
     * that it could easily be hacked to give hair to species that shouldn't have them.
     *
     * @param hairStyleTemplate The template of the hair.
     * @param speciesGender     The species gender string. i.e. human_male
     * @return
     */
    private boolean isValidHairSelection(final String hairStyleTemplate, final String speciesGender) {
        if (hairStyleTemplate.isEmpty())
            return allowBaldService.isAllowedBald(speciesGender);

        return hairStylesService.isValidForPlayerTemplate(speciesGender, hairStyleTemplate);
    }

    private void applyScaleLimits(final CreatureObject creatureObject, float scaleFactor) {
        final SharedObjectTemplate sharedObjectTemplate = creatureObject.getSharedTemplate();

        if (sharedObjectTemplate != null) {
            final float scaleMax = sharedObjectTemplate.getScaleMax();
            final float scaleMin = sharedObjectTemplate.getScaleMin();

            scaleFactor = Math.min(scaleFactor, scaleMax);
            scaleFactor = Math.max(scaleFactor, scaleMin);
        }

        creatureObject.setScaleFactor(scaleFactor);
        creatureObject.setScale(Vector.XYZ111.multiply(scaleFactor));
    }

    private void applyAttributeMods(final CreatureObject creatureObject, final String speciesGender, final String profession) {
        final ProfessionModsService.ProfessionModInfo professionModInfo = professionModsService.getProfessionModInfo(profession);
        final RacialModsService.RacialModInfo racialModInfo = racialModsService.getRacialModInfo(speciesGender);

        if (professionModInfo == null) {
            LOGGER.warn("Could not find profession mod info for profession {}", profession);
            return;
        }

        if (racialModInfo == null) {
            LOGGER.warn("Could not find racial mod info for species/gender {}", speciesGender);
            return;
        }

        final TIntList profList = professionModInfo.getAttributes();
        final TIntList raceList = racialModInfo.getAttributes();

        for (int i = 0; i < Attribute.SIZE; ++i) {
            final int value = profList.get(i) + raceList.get(i);
            creatureObject.initializeAttribute(i, value);
        }
    }

    /**
     * Creates all the starting equipment based on the player's profession selection. This doesn't include inventory
     * items. It's mainly just the clothes that the player sees on the creation screen. The inventory items will be
     * created post tutorial.
     *
     * @param creatureObject     The creature who will own the items.
     * @param sharedTemplateName The template of the creature.
     * @param professionInfo     The profession of the creature.
     */
    private void createStartingEquipment(final CreatureObject creatureObject, final String sharedTemplateName, final ProfessionInfo professionInfo) {
        if (professionInfo != null) {
            final List<EquipmentInfo> equipmentList = professionInfo.getEquipmentForTemplate(sharedTemplateName);

            //TODO: Use arrangementIndex.
            for (final EquipmentInfo equipmentInfo : equipmentList) {
                serverObjectService.createObject(
                        equipmentInfo.getServerTemplateName(),
                        creatureObject);
            }
        }
    }

    private void createRequiredSlots(final CreatureObject creatureObject) {
        final ContainerResult containerResult = new ContainerResult();

        final ServerObject inventory = serverObjectService.createObject(INVENTORY_TEMPLATE);
        if (!containerTransferService.transferItemToSlottedContainerSlotId(creatureObject, inventory, null, INVENTORY_SLOT_NAME, containerResult)) {
            LOGGER.error("Could not slot inventory on creature {} because {}",
                    creatureObject.getDebugInformation(),
                    containerResult.getError());
        }

        final ServerObject datapad = serverObjectService.createObject(DATAPAD_TEMPLATE);
        if (!containerTransferService.transferItemToSlottedContainerSlotId(creatureObject, datapad, null, DATAPAD_SLOT_NAME, containerResult)) {
            LOGGER.error("Could not slot datapad on creature {} because {}",
                    creatureObject.getDebugInformation(),
                    containerResult.getError());
        }

        final ServerObject bank = serverObjectService.createObject(BANK_TEMPLATE);
        if (!containerTransferService.transferItemToSlottedContainerSlotId(creatureObject, bank, null, BANK_SLOT_NAME, containerResult)) {
            LOGGER.error("Could not slot bank on creature {} because {}",
                    creatureObject.getDebugInformation(),
                    containerResult.getError());
        }

        final ServerObject missionBag = serverObjectService.createObject(MISSION_BAG_TEMPLATE);
        if (!containerTransferService.transferItemToSlottedContainerSlotId(creatureObject, missionBag, null, MISSION_BAG_SLOT_NAME, containerResult)) {
            LOGGER.error("Could not slot mission bag on creature {} because {}",
                    creatureObject.getDebugInformation(),
                    containerResult.getError());
        }

        final ServerObject appearanceInventory = serverObjectService.createObject(APPEARANCE_TEMPLATE);
        if (!containerTransferService.transferItemToSlottedContainerSlotId(creatureObject, appearanceInventory, null, APPEARANCE_SLOT_NAME, containerResult)) {
            LOGGER.error("Could not slot appearance inventory on creature {} because {}",
                    creatureObject.getDebugInformation(),
                    containerResult.getError());
        }
    }
}
