package io.bacta.game.player.creation;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.io.Files;
import gnu.trove.list.TIntList;
import io.bacta.engine.conf.BactaConfiguration;
import io.bacta.engine.utils.StringUtil;
import io.bacta.game.chat.GameChatService;
import io.bacta.game.container.ContainerTransferFailedException;
import io.bacta.game.container.ContainerTransferService;
import io.bacta.game.message.ClientCreateCharacter;
import io.bacta.game.message.ClientCreateCharacterFailed;
import io.bacta.game.message.ClientCreateCharacterSuccess;
import io.bacta.game.name.NameErrors;
import io.bacta.game.name.NameService;
import io.bacta.game.object.ObjectTemplateService;
import io.bacta.game.object.ServerObject;
import io.bacta.game.object.ServerObjectCreationFailedException;
import io.bacta.game.object.ServerObjectService;
import io.bacta.game.object.intangible.player.PlayerObject;
import io.bacta.game.object.tangible.TangibleObject;
import io.bacta.game.object.tangible.creature.Attribute;
import io.bacta.game.object.tangible.creature.CreatureObject;
import io.bacta.game.object.tangible.creature.Gender;
import io.bacta.game.object.tangible.creature.Race;
import io.bacta.game.object.template.server.ServerCreatureObjectTemplate;
import io.bacta.game.player.BiographyService;
import io.bacta.shared.collision.CollisionProperty;
import io.bacta.shared.foundation.ConstCharCrcLowerString;
import io.bacta.shared.localization.StringId;
import io.bacta.shared.math.Transform;
import io.bacta.shared.math.Vector;
import io.bacta.shared.object.template.SharedObjectTemplate;
import io.bacta.soe.context.SoeRequestContext;
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

    public void createCharacter(SoeRequestContext context, ClientCreateCharacter createMessage) {
        //TODO: Account verification.
        final int accountId = 1;
        final String username = "crush";

        try {
            final String serverTemplateName = createMessage.getTemplateName();
            final String speciesGender = Files.getNameWithoutExtension(serverTemplateName);
            final Gender gender = Gender.fromSpeciesGender(speciesGender);
            final Race race = Race.fromSpeciesGender(speciesGender);
            final String profession = getValidProfession(createMessage);
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
            final String sharedTemplateName = playerCreature.getSharedTemplate().getResourceName();
            final ProfessionInfo professionInfo = professionDefaultsService.getDefaults(profession);

            playerCreature.setObjectName(createMessage.getCharacterName());
            playerCreature.setOwnerId(playerCreature.getNetworkId());
            playerCreature.setPlayerControlled(true);
            playerCreature.setAppearanceData(createMessage.getAppearanceData());

            configurePlayerPosition(playerCreature, createMessage);

            final CollisionProperty collision = playerCreature.getCollisionProperty();

            if (collision != null)
                collision.setPlayerControlled(true);

            applyScaleLimits(playerCreature, createMessage.getScaleFactor());
            configurePlayerHair(playerCreature, speciesGender, createMessage);
            createStartingEquipment(playerCreature, sharedTemplateName, professionInfo);
            createRequiredSlots(playerCreature);
            applyAttributeMods(playerCreature, speciesGender, profession);
            configureBiography(playerCreature, createMessage.getBiography());
            configurePlayerObject(playerCreature, createMessage);
//

//
//        playerCreature.setSceneIdOnThisAndContents(startingLocationInfo.getPlanet());

            // Persist object (Done in Object Manager)

            //Post character setup.
            //Send this to the login server so that it knows about this character.
//        final CharacterInfo info = new CharacterInfo(
//                playerCreature.getAssignedObjectName(),
//                SOECRC32.hashCode(playerCreature.getObjectTemplateName()),
//                playerCreature.getNetworkId(),
//                gameServerState.getClusterId(),
//                CharacterInfo.Type.NORMAL,
//                false
//        );

            //nameService.addPlayerName(firstName);

            final ClientCreateCharacterSuccess responseMessage = new ClientCreateCharacterSuccess(
                    playerCreature.getNetworkId());

            context.sendMessage(responseMessage);

            pendingCreations.invalidate(firstName);

            chatService.destroyAvatar(firstName);

        } catch (ServerObjectCreationFailedException ex) {
            //The only time this should hit here is if the player creature failed to create. Other items failing should
            //be caught in their respective methods.
            LOGGER.error("Account {} attempted to create a character but the object service failed to create it. {}",
                    username,
                    createMessage.getTemplateName());

            final ClientCreateCharacterFailed message = new ClientCreateCharacterFailed(
                    createMessage.getCharacterName(),
                    NameErrors.CANT_CREATE_AVATAR);

            context.sendMessage(message);
        } catch (ContainerTransferFailedException ex) {
            LOGGER.error("Unable to transfer player to container. This shouldn't happen.");

            final ClientCreateCharacterFailed message = new ClientCreateCharacterFailed(
                    createMessage.getCharacterName(),
                    NameErrors.CANT_CREATE_AVATAR);

            context.sendMessage(message);
        }
    }

    private void configurePlayerObject(final CreatureObject playerCreature, final ClientCreateCharacter request) {
        try {
            final int accountId = 1;
            final PlayerObject play = serverObjectService.createObject(GHOST_TEMPLATE, playerCreature);
            containerTransferService.transferItemToSlottedContainerSlotId(playerCreature, play, null, GHOST_SLOT_NAME);

            play.setStationId(accountId);
            play.setBornDate((int) Instant.now().getEpochSecond());
            play.setSkillTemplate(request.getSkillTemplate(), true);
            play.setWorkingSkill(request.getWorkingSkill(), true);
        } catch (ServerObjectCreationFailedException ex) {
            LOGGER.error("Failed to create player object for character {}.", playerCreature.getNetworkId());
        } catch (ContainerTransferFailedException ex) {
            LOGGER.error("Failed to add player object to creature {} ghost slot.", playerCreature.getNetworkId());
        }
    }

    private void configureBiography(final CreatureObject playerCreature, final String biography) {
        if (biography != null && !biography.isEmpty()) {
            biographyService.setBiography(playerCreature.getNetworkId(), biography);
        }
    }

    private void configurePlayerPosition(final CreatureObject playerCreature, final ClientCreateCharacter request) {
        final String startingLocation = request.getStartingLocation();
        final Transform transform = new Transform();

        final StartingLocations.StartingLocationInfo startingLocationInfo
                = startingLocations.getStartingLocationInfo(startingLocation);

        if (request.isUseNewbieTutorial()) {
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
    }

    private void configurePlayerHair(final CreatureObject playerCreature, final String speciesGender, final ClientCreateCharacter request) {
        String hairStyleTemplate = request.getHairTemplateName();

        try {
            //If hair is invalid, use default.
            if (!isValidHairSelection(request.getHairTemplateName(), speciesGender)) {
                LOGGER.error("Invalid hair style {} for species/gender {}.", hairStyleTemplate, speciesGender);
                hairStyleTemplate = hairStylesService.getDefaultHairStyle(speciesGender);
            }

            //Create the hair and equip it to the player.
            if (!hairStyleTemplate.isEmpty()) {
                final ServerObject hair = serverObjectService.createObject(hairStyleTemplate, playerCreature);
                final TangibleObject tangibleHair = (TangibleObject) hair;
                tangibleHair.setAppearanceData(request.getHairAppearanceData());
            }

        } catch (ServerObjectCreationFailedException ex) {
            LOGGER.error("Could not create hair with template {}.", hairStyleTemplate);

        } catch (ContainerTransferFailedException ex) {
            LOGGER.error("Unable to slot hair with template {}.", hairStyleTemplate);

        } catch (ClassCastException ex) {
            LOGGER.error("Requested hair template was not tangible.");
        }
    }

    private String getValidProfession(final ClientCreateCharacter request) {
        final String professionName = request.getProfession();

        final boolean validProfessionName = professionName != null
                && !professionName.isEmpty()
                && !this.disabledProfessions.contains(professionName);

        return validProfessionName
                ? professionName
                : this.defaultProfession;
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

        //creatureObject.setScaleFactor(scaleFactor);
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
                final String templateName = equipmentInfo.getServerTemplateName();

                try {
                    serverObjectService.createObject(templateName, creatureObject);
                } catch (ServerObjectCreationFailedException ex) {
                    LOGGER.error("Failed creating starting equipment item {}", templateName);
                } catch (ContainerTransferFailedException ex) {
                    LOGGER.error("Failed transferring starting equipment item {} to player {} because {}",
                            templateName,
                            creatureObject.getNetworkId(),
                            ex.getErrorCode().name());
                }
            }
        }
    }

    private void createRequiredSlots(final CreatureObject creatureObject) {
        createRequiredSlotItem(creatureObject, INVENTORY_TEMPLATE, INVENTORY_SLOT_NAME);
        createRequiredSlotItem(creatureObject, DATAPAD_TEMPLATE, DATAPAD_SLOT_NAME);
        createRequiredSlotItem(creatureObject, BANK_TEMPLATE, BANK_SLOT_NAME);
        createRequiredSlotItem(creatureObject, MISSION_BAG_TEMPLATE, MISSION_BAG_SLOT_NAME);
        createRequiredSlotItem(creatureObject, APPEARANCE_TEMPLATE, APPEARANCE_SLOT_NAME);
    }

    private void createRequiredSlotItem(final CreatureObject creatureObject, final String templateName, final ConstCharCrcLowerString slotName) {
        try {
            final ServerObject item = serverObjectService.createObject(templateName);
            containerTransferService.transferItemToSlottedContainerSlotId(creatureObject, item, null, slotName);

        } catch (ServerObjectCreationFailedException ex) {
            LOGGER.error("Failed to create required slot item {} in slot {} for player {}.",
                    templateName,
                    slotName.toString(),
                    creatureObject.getNetworkId());
        } catch (ContainerTransferFailedException ex) {
            LOGGER.error("Failed to transfer required slot item {} to slot {} for player {} because {}.",
                    templateName,
                    slotName.getString(),
                    creatureObject.getNetworkId(),
                    ex.getErrorCode().name());
        }
    }
}
