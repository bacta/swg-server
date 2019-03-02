package io.bacta.game.object.intangible.player;

import io.bacta.archive.delta.*;
import io.bacta.archive.delta.map.AutoDeltaLongObjectMap;
import io.bacta.archive.delta.map.AutoDeltaObjectIntMap;
import io.bacta.archive.delta.map.AutoDeltaStringIntMap;
import io.bacta.archive.delta.packedmap.AutoDeltaPackedPlayerQuestDataMap;
import io.bacta.archive.delta.set.AutoDeltaLongSet;
import io.bacta.archive.delta.vector.AutoDeltaStringVector;
import io.bacta.game.matchmaking.MatchMakingId;
import io.bacta.game.object.intangible.IntangibleObject;
import io.bacta.game.object.intangible.schematic.DraftSchematicCombinedCrcs;
import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.game.waypoint.Waypoint;
import io.bacta.shared.container.SlotIdManager;
import io.bacta.shared.foundation.BitArray;
import io.bacta.shared.template.ObjectTemplateList;
import lombok.Getter;
import lombok.Setter;

import javax.inject.Inject;

@Getter
@Setter
public final class PlayerObject extends IntangibleObject {
    private String biography;
    private long houseId;

    private int stationId;

    //private StationId stationId;
    private final AutoDeltaInt accountNumLotsOverLimitSpam; /// controls whether the player should be spammed for exceeding the lot limit
    private final AutoDeltaObjectIntMap<DraftSchematicCombinedCrcs> draftSchematics; // draft schematics the player may use
    private final AutoDeltaStringIntMap experiencePoints;   // xp name->amount map
    private final AutoDeltaInt expModified;        // just a flag that we increment any time the creature is granted new xp
    private final AutoDeltaInt maxForcePower;      ///< Maximum force power the player can have
    private final AutoDeltaInt forcePower;         ///< Current force power the player has
    private float forceRegenRate;     ///< Rate at which the force power regenerates (units/sec)
    private float forceRegenValue;    ///< Amount of force power that's regenerated
    private final AutoDeltaInt craftingLevel;           // crafting level of the current crafting session
    private final AutoDeltaInt experimentPoints;        // experiment points available to the player
    private final AutoDeltaInt craftingStage;           // what stage in the crafting process a player is in
    private long craftingTool;         // tool a player is crafting with
    private final AutoDeltaLong craftingStation;         // station a player is crafting with
    private final AutoDeltaLong craftingComponentBioLink;// bio-link id of a component
    private final AutoDeltaVariable<MatchMakingId> matchMakingPersonalProfileId; // What the player defines their personality as
    private final AutoDeltaVariable<MatchMakingId> matchMakingCharacterProfileId; // What the player defines their character as
    private final AutoDeltaStringVector friendList;
    private final AutoDeltaStringVector ignoreList;
    private final AutoDeltaString skillTitle;
    private final AutoDeltaInt spokenLanguage;
    private final AutoDeltaLongObjectMap<Waypoint> waypoints;
    private final AutoDeltaLongObjectMap<Waypoint> groupWaypoints;
    private final AutoDeltaInt bornDate;
    private final AutoDeltaInt playedTime;
    private final AutoDeltaInt food;
    private final AutoDeltaInt maxFood;
    private final AutoDeltaInt drink;
    private final AutoDeltaInt maxDrink;
    private final AutoDeltaInt meds;
    private final AutoDeltaInt maxMeds;
    private final AutoDeltaByte privilegedTitle;
    private final AutoDeltaVariable<BitArray> completedQuests;
    private final AutoDeltaVariable<BitArray> activeQuests;
    private final AutoDeltaInt currentQuest;
    private final AutoDeltaPackedPlayerQuestDataMap quests;
    private final AutoDeltaInt roleIconChoice;
    private final AutoDeltaString skillTemplate;
    private final AutoDeltaString workingSkill;
    private final AutoDeltaInt currentGcwPoints;
    private final AutoDeltaInt currentPvpKills;
    private final AutoDeltaLong lifetimeGcwPoints;
    private final AutoDeltaInt lifetimePvpKills;
    private final AutoDeltaInt currentGcwRank;
    private final AutoDeltaFloat currentGcwRankProgress;
    private final AutoDeltaInt maxGcwImperialRank;
    private final AutoDeltaInt maxGcwRebelRank;
    private final AutoDeltaInt gcwRatingActualCalcTime;
    private final AutoDeltaLongSet playerHateList;
    private final AutoDeltaInt killMeter;
    private final AutoDeltaLong petId;
    private final AutoDeltaStringVector petCommandList;
    private final AutoDeltaStringVector petToggledCommands;
    private final AutoDeltaVariable<BitArray> collections;
    private final AutoDeltaVariable<BitArray> collections2;
    private final AutoDeltaString citizenshipCity;
    private final AutoDeltaByte citizenshipType; // CityDataCitizenType
    private final AutoDeltaVariable<GcwDefenderRegionQualifications> cityGcwDefenderRegion;
    private final AutoDeltaVariable<GcwDefenderRegionQualifications> guildGcwDefenderRegion;
    private final AutoDeltaLong squelchedById;   // id of the toon who squelched this toon; is NetworkId::cms_invalid if this toon is not squelched
    private final AutoDeltaString squelchedByName; // name of the toon who squelched this toon
    private final AutoDeltaInt squelchExpireTime; // the Epoch time when the toon will be unsquelched; is < 0 for indefinite squelch
    private final AutoDeltaBoolean showBackpack;
    private final AutoDeltaBoolean showHelmet;
    private final AutoDeltaInt environmentFlags; // Force Day, Night, whatever else.
    private final AutoDeltaString defaultAttackOverride; // This string will override the user's default attack
    private final AutoDeltaByte unknownByte;
    private final AutoDeltaInt unknownInt;
    private final AutoDeltaVariable<BitArray> guildRank;
    private final AutoDeltaVariable<BitArray> citizenRank;
    private final AutoDeltaByte galacticReserveDeposit;
    private final AutoDeltaLong pgcRatingCount;
    private final AutoDeltaLong pgcRatingTotal;
    private final AutoDeltaInt pgcLastRatingTime;

    @Inject
    public PlayerObject(final ObjectTemplateList objectTemplateList,
                        final SlotIdManager slotIdManager,
                        final ServerObjectTemplate template) {
        super(objectTemplateList, slotIdManager, template);

        accountNumLotsOverLimitSpam = new AutoDeltaInt();
        draftSchematics = new AutoDeltaObjectIntMap<>(DraftSchematicCombinedCrcs::new);
        experiencePoints = new AutoDeltaStringIntMap();
        expModified = new AutoDeltaInt();
        maxForcePower = new AutoDeltaInt();
        forcePower = new AutoDeltaInt();
        craftingLevel = new AutoDeltaInt();
        experimentPoints = new AutoDeltaInt();
        craftingStage = new AutoDeltaInt();
        craftingStation = new AutoDeltaLong();
        craftingComponentBioLink = new AutoDeltaLong();
        matchMakingPersonalProfileId = new AutoDeltaVariable<>(new MatchMakingId(), MatchMakingId::new);
        matchMakingCharacterProfileId = new AutoDeltaVariable<>(new MatchMakingId(), MatchMakingId::new);
        friendList = new AutoDeltaStringVector();
        ignoreList = new AutoDeltaStringVector();
        skillTitle = new AutoDeltaString();
        spokenLanguage = new AutoDeltaInt();
        waypoints = new AutoDeltaLongObjectMap<>(Waypoint::new);
        groupWaypoints = new AutoDeltaLongObjectMap<>(Waypoint::new);
        bornDate = new AutoDeltaInt();
        playedTime = new AutoDeltaInt();
        food = new AutoDeltaInt();
        maxFood = new AutoDeltaInt();
        drink = new AutoDeltaInt();
        maxDrink = new AutoDeltaInt();
        meds = new AutoDeltaInt();
        maxMeds = new AutoDeltaInt();
        privilegedTitle = new AutoDeltaByte();
        completedQuests = new AutoDeltaVariable<>(new BitArray(), BitArray::new);
        activeQuests = new AutoDeltaVariable<>(new BitArray(), BitArray::new);
        currentQuest = new AutoDeltaInt();
        quests = new AutoDeltaPackedPlayerQuestDataMap();
        roleIconChoice = new AutoDeltaInt();
        skillTemplate = new AutoDeltaString();
        workingSkill = new AutoDeltaString();
        currentGcwPoints = new AutoDeltaInt();
        currentPvpKills = new AutoDeltaInt();
        lifetimeGcwPoints = new AutoDeltaLong();
        lifetimePvpKills = new AutoDeltaInt();
        currentGcwRank = new AutoDeltaInt();
        currentGcwRankProgress = new AutoDeltaFloat();
        maxGcwImperialRank = new AutoDeltaInt();
        maxGcwRebelRank = new AutoDeltaInt();
        gcwRatingActualCalcTime = new AutoDeltaInt();
        playerHateList = new AutoDeltaLongSet();
        killMeter = new AutoDeltaInt();
        petId = new AutoDeltaLong();
        petCommandList = new AutoDeltaStringVector();
        petToggledCommands = new AutoDeltaStringVector();
        collections = new AutoDeltaVariable<>(new BitArray(), BitArray::new);
        collections2 = new AutoDeltaVariable<>(new BitArray(), BitArray::new);
        citizenshipCity = new AutoDeltaString();
        citizenshipType = new AutoDeltaByte();
        cityGcwDefenderRegion = new AutoDeltaVariable<>(new GcwDefenderRegionQualifications(), GcwDefenderRegionQualifications::new);
        guildGcwDefenderRegion = new AutoDeltaVariable<>(new GcwDefenderRegionQualifications(), GcwDefenderRegionQualifications::new);
        squelchedById = new AutoDeltaLong();
        squelchedByName = new AutoDeltaString();
        squelchExpireTime = new AutoDeltaInt();
        showBackpack = new AutoDeltaBoolean();
        showHelmet = new AutoDeltaBoolean();
        environmentFlags = new AutoDeltaInt();
        defaultAttackOverride = new AutoDeltaString();
        unknownByte = new AutoDeltaByte();
        unknownInt = new AutoDeltaInt();
        guildRank = new AutoDeltaVariable<>(new BitArray(), BitArray::new);
        citizenRank = new AutoDeltaVariable<>(new BitArray(), BitArray::new);
        galacticReserveDeposit = new AutoDeltaByte();
        pgcRatingCount = new AutoDeltaLong();
        pgcRatingTotal = new AutoDeltaLong();
        pgcLastRatingTime = new AutoDeltaInt();

        addMembersToPackages();
    }

    private void addMembersToPackages() {
        firstParentAuthClientServerPackageNp.addVariable(craftingLevel);
        firstParentAuthClientServerPackageNp.addVariable(craftingStage);
        firstParentAuthClientServerPackageNp.addVariable(craftingStation);
        firstParentAuthClientServerPackageNp.addVariable(draftSchematics);
        firstParentAuthClientServerPackageNp.addVariable(craftingComponentBioLink); //investigate
        firstParentAuthClientServerPackageNp.addVariable(experimentPoints);
        firstParentAuthClientServerPackageNp.addVariable(expModified);
        firstParentAuthClientServerPackageNp.addVariable(friendList);
        firstParentAuthClientServerPackageNp.addVariable(ignoreList);
        firstParentAuthClientServerPackageNp.addVariable(spokenLanguage);
        firstParentAuthClientServerPackageNp.addVariable(food);
        firstParentAuthClientServerPackageNp.addVariable(maxFood);
        firstParentAuthClientServerPackageNp.addVariable(drink);
        firstParentAuthClientServerPackageNp.addVariable(maxDrink);
        firstParentAuthClientServerPackageNp.addVariable(meds);
        firstParentAuthClientServerPackageNp.addVariable(maxMeds);
        firstParentAuthClientServerPackageNp.addVariable(groupWaypoints);
        firstParentAuthClientServerPackageNp.addVariable(playerHateList);
        firstParentAuthClientServerPackageNp.addVariable(killMeter);
        firstParentAuthClientServerPackageNp.addVariable(accountNumLotsOverLimitSpam);
        firstParentAuthClientServerPackageNp.addVariable(petId);
        firstParentAuthClientServerPackageNp.addVariable(petCommandList);
        firstParentAuthClientServerPackageNp.addVariable(petToggledCommands);
        firstParentAuthClientServerPackageNp.addVariable(unknownByte);
        firstParentAuthClientServerPackageNp.addVariable(unknownInt);
        firstParentAuthClientServerPackageNp.addVariable(guildRank);
        firstParentAuthClientServerPackageNp.addVariable(citizenRank);
        firstParentAuthClientServerPackageNp.addVariable(galacticReserveDeposit);
        firstParentAuthClientServerPackageNp.addVariable(pgcRatingCount);
        firstParentAuthClientServerPackageNp.addVariable(pgcRatingTotal);
        firstParentAuthClientServerPackageNp.addVariable(pgcLastRatingTime); //37

        firstParentAuthClientServerPackage.addVariable(experiencePoints);
        firstParentAuthClientServerPackage.addVariable(waypoints);
        firstParentAuthClientServerPackage.addVariable(forcePower);
        firstParentAuthClientServerPackage.addVariable(maxForcePower);
        firstParentAuthClientServerPackage.addVariable(completedQuests);
        firstParentAuthClientServerPackage.addVariable(activeQuests);
        firstParentAuthClientServerPackage.addVariable(currentQuest);
        firstParentAuthClientServerPackage.addVariable(quests);
        firstParentAuthClientServerPackage.addVariable(workingSkill);

        sharedPackage.addVariable(matchMakingCharacterProfileId);
        sharedPackage.addVariable(matchMakingPersonalProfileId);
        sharedPackage.addVariable(skillTitle);
        sharedPackage.addVariable(bornDate);
        sharedPackage.addVariable(playedTime);
        sharedPackage.addVariable(roleIconChoice);
        sharedPackage.addVariable(skillTemplate);
        sharedPackage.addVariable(currentGcwPoints);
        sharedPackage.addVariable(currentPvpKills);
        sharedPackage.addVariable(lifetimeGcwPoints);
        sharedPackage.addVariable(lifetimePvpKills);
        sharedPackage.addVariable(collections);
        sharedPackage.addVariable(collections2);
        sharedPackage.addVariable(showBackpack);
        sharedPackage.addVariable(showHelmet);

        sharedPackageNp.addVariable(privilegedTitle);
        sharedPackageNp.addVariable(currentGcwRank);
        sharedPackageNp.addVariable(currentGcwRankProgress);
        sharedPackageNp.addVariable(maxGcwImperialRank);
        sharedPackageNp.addVariable(maxGcwRebelRank);
        sharedPackageNp.addVariable(gcwRatingActualCalcTime);
        sharedPackageNp.addVariable(citizenshipCity);
        sharedPackageNp.addVariable(citizenshipType);
        sharedPackageNp.addVariable(cityGcwDefenderRegion);
        sharedPackageNp.addVariable(guildGcwDefenderRegion);
        sharedPackageNp.addVariable(squelchedById);
        sharedPackageNp.addVariable(squelchedByName);
        sharedPackageNp.addVariable(squelchExpireTime);
        sharedPackageNp.addVariable(environmentFlags);
        sharedPackageNp.addVariable(defaultAttackOverride);
    }
//
//    public final int getRoleIconChoice() {
//        return roleIconChoice.get();
//    }
//
//    public final void setRoleIconChoice(final int roleIconChoice) {
//        this.roleIconChoice.set(roleIconChoice);
//    }
//
//    public final String getBiography() {
//        return biography;
//    }
//
//    public final void setBiography(final String biography) {
//        this.biography = biography;
//        //setDirty(true);
//    }
//
//    public final int getBornDate() {
//        return bornDate.get();
//    }
//
//    public final void setBornDate(int value) {
//        bornDate.set(value);
//        //setDirty(true);
//    }
//
//    public final int getPlayedTime() {
//        return playedTime.get();
//    }
//
//    public final void setPlayedTime(int value) {
//        playedTime.set(value);
//        //setDirty(true);
//    }
//
//    public final boolean isLinkDead() {
//        return matchMakingCharacterProfileId.get().isBitSet(MatchMakingId.LINK_DEAD);
//    }
//
//    public final void setLinkDead() {
//        MatchMakingId matchMakingId = matchMakingCharacterProfileId.get();
//        matchMakingId.set(MatchMakingId.LINK_DEAD);
//
//        matchMakingCharacterProfileId.set(matchMakingId);
//        //setDirty(true);
//    }
//
//    public final void clearLinkDead() {
//        MatchMakingId matchMakingId = matchMakingCharacterProfileId.get();
//        matchMakingId.unset(MatchMakingId.LINK_DEAD);
//        matchMakingCharacterProfileId.set(matchMakingId);
//        //setDirty(true);
//    }
//
//    public boolean setSkillTemplate(final String templateName, final boolean clientRequest) {
//        final String previousTemplateName = skillTemplate.get();
//
//        // we need to pass an empty string to script to do cleanup, but we don't want to actually change the player's skill
//        if (!templateName.isEmpty())
//            skillTemplate.set(templateName);
//
//        final CreatureObject owner = getCreatureObject();
//
//        if (owner != null) {
//
//            // TODO: Scripting hooks
////            GameScriptObject * const script = owner->getScriptObject();
////
////            if(script) {
////                ScriptParams params;
////
////                params.addParam(templateName.c_str());
////                params.addParam(clientRequest);
////
////                if (script->trigAllScripts(Scripting::TRIG_SKILL_TEMPLATE_CHANGED, params) == SCRIPT_DEFAULT)
////                {
////                    m_skillTemplate.set(previousTemplateName);
////                    LOG("ScriptInvestigation", ("Scripts blocked setSkillTemplate( %s ) on Player( %s ).  Reverting to %s.", templateName.c_str(), getAccountDescription().c_str(), previousTemplateName.c_str()));
////                }
////            }
//
//            if (!previousTemplateName.equals(skillTemplate.get())) {
//
//                // TODO: Looking for group stuff
//                /*
//                final short newProfession = LfgCharacterData.convertSkillTemplateToProfession(skillTemplate.get());
//
//                final GroupObject group = owner.getGroup();
//                if (group != null) {
//                    final short currentProfessionForGroup = group.getMemberProfession(owner.getNetworkId());
//
//                    if (newProfession != currentProfessionForGroup)
//                        group.setMemberProfession(owner.getNetworkId(), newProfession);
//                }
//
//                std::map<NetworkId, LfgCharacterData> const & connectedCharacterLfgData = ServerUniverse::getConnectedCharacterLfgData();
//                std::map<NetworkId, LfgCharacterData>::const_iterator iterFind = connectedCharacterLfgData.find(owner->getNetworkId());
//                if ((iterFind != connectedCharacterLfgData.end()) && (iterFind->second.profession != static_cast<LfgCharacterData::Profession>(newProfession)))
//                    ServerUniverse::setConnectedCharacterProfessionData(owner->getNetworkId(), static_cast<LfgCharacterData::Profession>(newProfession));
//                */
//            }
//        }
//
//        return (!templateName.equals(previousTemplateName));
//    }
//
//    public boolean setWorkingSkill(final String skillName, final boolean clientRequest) {
//        final String previousWorkingSkill = workingSkill.get();
//
//        workingSkill.set(skillName);
//
//        CreatureObject owner = getCreatureObject();
//        if (owner != null) {
//
//            // TODO: Scripting Hooks
////            GameScriptObject * const script = owner->getScriptObject();
////            if(script) {
////                ScriptParams params;
////
////                params.addParam(skillName.c_str());
////                params.addParam(clientRequest);
////
////                if (script->trigAllScripts(Scripting::TRIG_WORKING_SKILL_CHANGED, params) == SCRIPT_DEFAULT)
////                {
////                    m_workingSkill.set(previousWorkingSkill);
////                    LOG("ScriptInvestigation", ("Scripts blocked setWorkingSkill( %s ) on Player( %s ). Reverting to %s.", skillName.c_str(), getAccountDescription().c_str(), previousWorkingSkill.c_str()));
////                }
////            }
//        }
//
//        return (!previousWorkingSkill.equals(skillName));
//    }

//    public CreatureObject getCreatureObject() {
//        final GameObject owner = this.getContainedByProperty().getContainedBy();
//
//        if (owner != null) {
//            return CreatureObject.asCreatureObject(owner);
//        }
//
//        return null;
//    }
}
