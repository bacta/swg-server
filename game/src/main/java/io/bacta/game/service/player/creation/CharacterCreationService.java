package io.bacta.game.service.player.creation;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import gnu.trove.list.TIntList;
import io.bacta.engine.conf.BactaConfiguration;
import io.bacta.game.context.GameRequestContext;
import io.bacta.game.message.ClientCreateCharacter;
import io.bacta.game.message.ClientCreateCharacterFailed;
import io.bacta.game.message.ClientCreateCharacterSuccess;
import io.bacta.game.name.NameErrors;
import io.bacta.game.name.NameService;
import io.bacta.game.object.ServerObject;
import io.bacta.game.object.intangible.player.PlayerObject;
import io.bacta.game.object.tangible.creature.Attribute;
import io.bacta.game.object.tangible.creature.CreatureObject;
import io.bacta.game.object.template.server.ServerCreatureObjectTemplate;
import io.bacta.game.service.chat.GameChatService;
import io.bacta.game.service.container.ContainerTransferService;
import io.bacta.game.service.object.ObjectTemplateService;
import io.bacta.game.service.object.ServerObjectService;
import io.bacta.game.service.player.BiographyService;
import io.bacta.shared.container.ContainerResult;
import io.bacta.shared.foundation.ConstCharCrcLowerString;
import io.bacta.shared.math.Vector;
import io.bacta.shared.object.template.SharedObjectTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
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

        this.disabledProfessions = new HashSet<>(bactaConfiguration.getStringCollection(
                CONFIG_SECTION, "disabledProfession"));

        this.defaultProfession = bactaConfiguration.getStringWithDefault(
                CONFIG_SECTION, "defaultProfession", "crafting_artisan");

        this.pendingCreations = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    }

    public void createCharacter(GameRequestContext context, ClientCreateCharacter createCharacter) {
        //TODO: Account verification.
        final int accountId = 1;
        final String username = "crush";

        final ServerCreatureObjectTemplate objectTemplate = objectTemplateService.getObjectTemplate(createCharacter.getTemplateName());

        if (objectTemplate == null) {
            LOGGER.error("Account {} attempted to create a player with invalid player template {}.",
                    username,
                    createCharacter.getTemplateName());

            context.sendMessage(new ClientCreateCharacterFailed(createCharacter.getCharacterName(), NameErrors.NO_TEMPLATE));
            return;
        }

        final CreatureObject playerCreature = serverObjectService.createObject(objectTemplate.getResourceName());

        if (playerCreature == null) {
            LOGGER.error("Account {} attempted to create a character but the object service failed to create it. {}",
                    username,
                    createCharacter.getTemplateName());

            context.sendMessage(new ClientCreateCharacterFailed(createCharacter.getCharacterName(), NameErrors.CANT_CREATE_AVATAR));
            return;
        }

        playerCreature.setObjectName(createCharacter.getCharacterName());
        playerCreature.setOwnerId(playerCreature.getNetworkId());
        playerCreature.setPlayerControlled(true);

        final PlayerObject ghost = serverObjectService.createObject(GHOST_TEMPLATE, playerCreature);

        createRequiredSlots(playerCreature);

        final ClientCreateCharacterSuccess success = new ClientCreateCharacterSuccess(playerCreature.getNetworkId());
        context.sendMessage(success);
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
