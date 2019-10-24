package io.bacta.game.object.tangible.creature;


import io.bacta.archive.delta.*;
import io.bacta.archive.delta.map.AutoDeltaIntObjectMap;
import io.bacta.archive.delta.map.AutoDeltaStringIntMap;
import io.bacta.archive.delta.map.AutoDeltaStringObjectMap;
import io.bacta.archive.delta.set.AutoDeltaObjectSet;
import io.bacta.archive.delta.set.AutoDeltaStringSet;
import io.bacta.archive.delta.vector.AutoDeltaIntVector;
import io.bacta.archive.delta.vector.AutoDeltaObjectVector;
import io.bacta.game.command.CommandQueue;
import io.bacta.game.object.tangible.TangibleObject;
import io.bacta.game.object.template.server.ServerCreatureObjectTemplate;
import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.game.object.universe.group.GroupInviter;
import io.bacta.game.object.universe.group.GroupMissionCriticalObject;
import io.bacta.shared.collision.CollisionProperty;
import io.bacta.shared.container.SlotIdManager;
import io.bacta.shared.math.Vector;
import io.bacta.shared.object.Buff;
import io.bacta.shared.object.template.SharedCreatureObjectTemplate;
import io.bacta.shared.template.ObjectTemplateList;

import javax.inject.Inject;

import static io.bacta.shared.object.template.SharedCreatureObjectTemplate.MovementTypes.MT_run;
import static io.bacta.shared.object.template.SharedCreatureObjectTemplate.MovementTypes.MT_walk;

public class CreatureObject extends TangibleObject {

    @Override
    public int getObjectType() {
        return 0x4352454F;
    } //'CREO'

    private final AutoDeltaIntVector unmodifiedMaxAttributes;
    private final AutoDeltaStringSet skills;
    private final AutoDeltaByte posture;
    private final AutoDeltaByte rank;
    private final AutoDeltaLong masterId;
    private final AutoDeltaFloat scaleFactor;
    private final AutoDeltaLong states;
    private final AutoDeltaIntVector attributeWounds;
    private final AutoDeltaFloat accelPercent;
    private final AutoDeltaFloat accelScale;
    private final AutoDeltaStringObjectMap<SkillModEntry> modMap;
    private final AutoDeltaFloat movementPercent;
    private final AutoDeltaFloat movementScale;
    private final AutoDeltaLong performanceListenTarget;
    private final AutoDeltaFloat runSpeed;
    private final AutoDeltaFloat slopeModAngle;
    private final AutoDeltaFloat slopeModPercent;
    private final AutoDeltaFloat turnScale;
    private final AutoDeltaFloat walkSpeed;
    private final AutoDeltaFloat waterModPercent;
    private final AutoDeltaObjectSet<GroupMissionCriticalObject> groupMissionCriticalObjectList;
    private final AutoDeltaShort level;
    private final AutoDeltaString animatingSkillData;
    private final AutoDeltaString animationMood;
    private final AutoDeltaLong currentWeapon;
    private final AutoDeltaLong group;
    private final AutoDeltaVariable<GroupInviter> groupInviter;
    private final AutoDeltaInt guildId;
    private final AutoDeltaLong lookAtTarget;
    private final AutoDeltaInt performanceStartTime;
    private final AutoDeltaInt performanceType;
    private final AutoDeltaIntVector attributes;
    private final AutoDeltaIntVector maxAttributes;
    private final AutoDeltaIntVector totalAttributes; //The current attributes, with all mods applied.
    private final AutoDeltaIntVector totalMaxAttributes; //The max attributes, with all mods applied.
    private final AutoDeltaIntVector attribBonus;
    private final AutoDeltaInt shockWounds;
    private final AutoDeltaObjectVector<WearableEntry> wearableData;
    private final AutoDeltaString alternateAppearanceSharedObjectTemplateName;
    private final AutoDeltaBoolean coverVisibility;
    private final AutoDeltaIntObjectMap<Buff.PackedBuff> buffs;
    private final AutoDeltaInt hologramType;
    private final AutoDeltaBoolean clientUsesAnimationLocomotion;
    private final AutoDeltaByte difficulty;
    private final AutoDeltaBoolean visibleOnMapAndRadar;
    private final AutoDeltaStringIntMap commands; //game commands a creature may execute.
    private final AutoDeltaBoolean isBeast;
    private final AutoDeltaBoolean forceShowHam;
    private final AutoDeltaObjectVector<WearableEntry> wearableAppearanceData; //Vector for our appearance items.
    private final AutoDeltaLong decoyOrigin; //The OID of the player whome we copied for this decoy creature.
    private final AutoDeltaInt totalLevelXp;
    private final AutoDeltaInt levelHealthGranted;
    private final AutoDeltaLong intendedTarget;
    private final AutoDeltaByte mood;

    private float lookAtYaw;
    private boolean useLookAtYaw;
    private int lookAtPositionSequenceId;


    @Inject
    public CreatureObject(final ObjectTemplateList objectTemplateList,
                          final SlotIdManager slotIdManager,
                          final ServerObjectTemplate template) {
        super(objectTemplateList, slotIdManager, template);

        unmodifiedMaxAttributes = new AutoDeltaIntVector();
        skills = new AutoDeltaStringSet();
        posture = new AutoDeltaByte(CreaturePosture.UPRIGHT);
        rank = new AutoDeltaByte((byte) 0);
        masterId = new AutoDeltaLong();
        scaleFactor = new AutoDeltaFloat(1.0F);
        shockWounds = new AutoDeltaInt(0);
        states = new AutoDeltaLong(0L);
        attributeWounds = new AutoDeltaIntVector();
        accelPercent = new AutoDeltaFloat(1.0F);
        accelScale = new AutoDeltaFloat(1.0F);
        attribBonus = new AutoDeltaIntVector(Attribute.SIZE);
        modMap = new AutoDeltaStringObjectMap<>(SkillModEntry::new);
        movementPercent = new AutoDeltaFloat(1.0F);
        movementScale = new AutoDeltaFloat(1.0F);
        performanceListenTarget = new AutoDeltaLong();
        runSpeed = new AutoDeltaFloat(((SharedCreatureObjectTemplate) getSharedTemplate()).getSpeed(MT_run));
        slopeModAngle = new AutoDeltaFloat((float) (((SharedCreatureObjectTemplate) getSharedTemplate()).getSlopeModAngle() * Math.PI / 180));
        slopeModPercent = new AutoDeltaFloat(1.0F);
        turnScale = new AutoDeltaFloat(1.0F);
        walkSpeed = new AutoDeltaFloat(((SharedCreatureObjectTemplate) getSharedTemplate()).getSpeed(MT_walk));
        waterModPercent = new AutoDeltaFloat(((SharedCreatureObjectTemplate) getSharedTemplate()).getWaterModPercent());
        groupMissionCriticalObjectList = new AutoDeltaObjectSet<>(GroupMissionCriticalObject::new);
        level = new AutoDeltaShort((short) -1);
        animatingSkillData = new AutoDeltaString();
        animationMood = new AutoDeltaString("neutral");
        currentWeapon = new AutoDeltaLong();
        group = new AutoDeltaLong();
        groupInviter = new AutoDeltaVariable<>(new GroupInviter(), GroupInviter::new);
        guildId = new AutoDeltaInt(0);
        lookAtTarget = new AutoDeltaLong();
        performanceStartTime = new AutoDeltaInt();
        performanceType = new AutoDeltaInt();
        attributes = new AutoDeltaIntVector(Attribute.SIZE);
        maxAttributes = new AutoDeltaIntVector(Attribute.SIZE);
        wearableData = new AutoDeltaObjectVector<>(WearableEntry::new);
        alternateAppearanceSharedObjectTemplateName = new AutoDeltaString();
        coverVisibility = new AutoDeltaBoolean(true);
        totalAttributes = new AutoDeltaIntVector();
        totalMaxAttributes = new AutoDeltaIntVector();
        buffs = new AutoDeltaIntObjectMap<>(Buff.PackedBuff::new);
        hologramType = new AutoDeltaInt(HologramType.NONE.value);
        clientUsesAnimationLocomotion = new AutoDeltaBoolean();
        difficulty = new AutoDeltaByte();
        visibleOnMapAndRadar = new AutoDeltaBoolean();
        commands = new AutoDeltaStringIntMap();
        isBeast = new AutoDeltaBoolean();
        forceShowHam = new AutoDeltaBoolean();
        wearableAppearanceData = new AutoDeltaObjectVector<>(WearableEntry::new);
        decoyOrigin = new AutoDeltaLong();
        totalLevelXp = new AutoDeltaInt();
        levelHealthGranted = new AutoDeltaInt();
        intendedTarget = new AutoDeltaLong();
        mood = new AutoDeltaByte();

        assert template instanceof ServerCreatureObjectTemplate;

        final ServerCreatureObjectTemplate serverTemplate = (ServerCreatureObjectTemplate) template;
        final SharedCreatureObjectTemplate sharedTemplate = (SharedCreatureObjectTemplate) getSharedTemplate();

        addProperty(new CommandQueue(this));

        final float scale = Math.max(0.f, sharedTemplate.getScale());
        scaleFactor.set(scale);
        setScale(Vector.XYZ111.multiply(scale));

        //Get the attribute values.

        for (int i = 0; i < Attribute.SIZE; ++i) {
            attributes.set(i, serverTemplate.getAttributes(ServerObjectTemplate.Attributes.from(i)));
            maxAttributes.set(i, attributes.get(i));
            //cachedCurrentAttributeModValues.set(i, 0);
            //cachedMaxAttributeModValues.set(i, 0);
            //regeneration[i] = 0;
        }

        //Store the default regen values in the former faucet attrib slots.
        attributes.set(Attribute.CONSTITUTION, 0); //ConfigServerGame::getDefaultHealthRegen()
        attributes.set(Attribute.STAMINA, 0); //ConfigServerGame::getDefaultActionRegen()
        attributes.set(Attribute.WILLPOWER, 0); //ConfigServerGame::getDefaultMindRegen()

        //TODO: These are server side only states that are used for NPCS: Fear, Interest, Anger, etc...

        //Fix the size of the mental state vectors.
        //maxMentalStates
        //mentalStateDecays

        //Get the mental state values.
        //for (int i = 0; i < 4; ++i) {
        //final ServerObjectTemplate.MentalStates state = ServerObjectTemplate.MentalStates.from(i);

        //maxMentalStates.set(i, serverTemplate.getMaxMentalStates(state));
        //initialState.currentValues[i] = 0;
        //mentalStateDecays.set(i, (float)(serverTemplate.getMentalStatesDecay(state)));
        //}

        final CollisionProperty collisionProperty = getCollisionProperty();

        if (collisionProperty != null)
            collisionProperty.setServerSide(true);

        addMembersToPackages();

        //setup source object for callbacks.
    }

    private void addMembersToPackages() {
        authClientServerPackage.addVariable(maxAttributes);
        authClientServerPackage.addVariable(skills);

        authClientServerPackageNp.addVariable(accelPercent);
        authClientServerPackageNp.addVariable(accelScale);
        authClientServerPackageNp.addVariable(attribBonus);
        authClientServerPackageNp.addVariable(modMap);
        authClientServerPackageNp.addVariable(movementPercent);
        authClientServerPackageNp.addVariable(movementScale);
        authClientServerPackageNp.addVariable(performanceListenTarget);
        authClientServerPackageNp.addVariable(runSpeed);
        authClientServerPackageNp.addVariable(slopeModAngle);
        authClientServerPackageNp.addVariable(slopeModPercent);
        authClientServerPackageNp.addVariable(turnScale);
        authClientServerPackageNp.addVariable(walkSpeed);
        authClientServerPackageNp.addVariable(waterModPercent);
        authClientServerPackageNp.addVariable(groupMissionCriticalObjectList);
        authClientServerPackageNp.addVariable(commands);
        authClientServerPackageNp.addVariable(totalLevelXp);

        sharedPackage.addVariable(posture);
        sharedPackage.addVariable(rank);
        sharedPackage.addVariable(masterId);
        sharedPackage.addVariable(scaleFactor);
        sharedPackage.addVariable(shockWounds);
        sharedPackage.addVariable(states);

        sharedPackageNp.addVariable(level);
        sharedPackageNp.addVariable(levelHealthGranted);
        sharedPackageNp.addVariable(animatingSkillData);
        sharedPackageNp.addVariable(animationMood);
        sharedPackageNp.addVariable(currentWeapon);
        sharedPackageNp.addVariable(group);
        sharedPackageNp.addVariable(groupInviter);
        sharedPackageNp.addVariable(guildId);
        sharedPackageNp.addVariable(lookAtTarget);
        sharedPackageNp.addVariable(intendedTarget);
        sharedPackageNp.addVariable(mood);
        sharedPackageNp.addVariable(performanceStartTime);
        sharedPackageNp.addVariable(performanceType);
        sharedPackageNp.addVariable(totalAttributes);
        sharedPackageNp.addVariable(totalMaxAttributes);
        sharedPackageNp.addVariable(wearableData);
        sharedPackageNp.addVariable(alternateAppearanceSharedObjectTemplateName);
        sharedPackageNp.addVariable(coverVisibility);
        sharedPackageNp.addVariable(buffs);
        sharedPackageNp.addVariable(clientUsesAnimationLocomotion);
        sharedPackageNp.addVariable(difficulty);
        sharedPackageNp.addVariable(hologramType);
        sharedPackageNp.addVariable(visibleOnMapAndRadar);
        sharedPackageNp.addVariable(isBeast);
        sharedPackageNp.addVariable(forceShowHam);
        sharedPackageNp.addVariable(wearableAppearanceData);
        sharedPackageNp.addVariable(decoyOrigin);
    }

//    public long getMasterId() {
//        return masterId.get();
//    }
//
//    public final void setPosture(byte posture) {
//        if (this.posture.get() == posture)
//            return;
//
//        this.posture.set(posture);
//
//        //TODO: Cleanup the sending of this.
//        final MessageQueuePosture postureMessage = new MessageQueuePosture(posture, true);
//
//        final ObjControllerMessage objc = new ObjControllerMessage(
//                GameControllerMessageFlags.SEND
//                        | GameControllerMessageFlags.RELIABLE
//                        | GameControllerMessageFlags.DEST_AUTH_CLIENT
//                        | GameControllerMessageFlags.DEST_PROXY_CLIENT,
//                GameControllerMessageType.SET_POSTURE.value,
//                getContainerNetworkId(),
//                0,
//                postureMessage);
//
//        //broadcastMessage(objc);
//    }
//
//    public final byte getPosture() {
//        return this.posture.get();
//    }
//
//    public final void addSkill(final String skill) {
//        skills.insert(skill);
//        //setDirty(true);
//    }
//
//    public final void removeSkill(final String skill) {
//        skills.erase(skill);
//        //setDirty(true);
//    }
//
//    public final boolean hasSkill(final String skill) {
//        return skills.contains(skill);
//    }
//
//    public final void setLookAtTarget(long lookAtTarget) {
//        this.lookAtTarget.set(lookAtTarget);
//        //setDirty(true);
//    }
//
//    public final int getGuildId() {
//        return guildId.get();
//    }
//
//    public final boolean isInGuild() {
//        return getGuildId() != 0;
//    }
//
//    public final long getLookAtTarget() {
//        return lookAtTarget.get();
//    }
//
//    //hamBase
//    public final int getHealthBase() {
//        return unmodifiedMaxAttributes.get(Attribute.HEALTH);
//    }
//
//    public final int getConstitutionBase() {
//        return unmodifiedMaxAttributes.get(Attribute.CONSTITUTION);
//    }
//
//    public final int getActionBase() {
//        return unmodifiedMaxAttributes.get(Attribute.ACTION);
//    }
//
//    public final int getStaminaBase() {
//        return unmodifiedMaxAttributes.get(Attribute.STAMINA);
//    }
//
//    public final int getMindBase() {
//        return unmodifiedMaxAttributes.get(Attribute.MIND);
//    }
//
//    public final int getWillpowerBase() {
//        return unmodifiedMaxAttributes.get(Attribute.WILLPOWER);
//    }
//
//    public final void setUnmodifiedMaxAttributes(TIntList values) {
//        unmodifiedMaxAttributes.set(values);
//        //setDirty(true);
//    }
//
//    public final void setHealthBase(int value) {
//        unmodifiedMaxAttributes.set(Attribute.HEALTH, value);
//        //setDirty(true);
//    }
//
//    public final void setConstitutionBase(int value) {
//        unmodifiedMaxAttributes.set(Attribute.CONSTITUTION, value);
//        //setDirty(true);
//    }
//
//    public final void setActionBase(int value) {
//        unmodifiedMaxAttributes.set(Attribute.ACTION, value);
//        //setDirty(true);
//    }
//
//    public final void setStaminaBase(int value) {
//        unmodifiedMaxAttributes.set(Attribute.STAMINA, value);
//        //setDirty(true);
//    }
//
//    public final void setMindBase(int value) {
//        unmodifiedMaxAttributes.set(Attribute.MIND, value);
//        //setDirty(true);
//    }
//
//    public final void setWillpowerBase(int value) {
//        unmodifiedMaxAttributes.set(Attribute.WILLPOWER, value);
//        //setDirty(true);
//    }
//
//    //Attribute wounds
//    public final int getHealthWounds() {
//        return attributeWounds.get(Attribute.HEALTH);
//    }
//
//    public final int getConstitutionWounds() {
//        return attributeWounds.get(Attribute.CONSTITUTION);
//    }
//
//    public final int getActionWounds() {
//        return attributeWounds.get(Attribute.ACTION);
//    }
//
//    public final int getStaminaWounds() {
//        return attributeWounds.get(Attribute.STAMINA);
//    }
//
//    public final int getMindWounds() {
//        return attributeWounds.get(Attribute.MIND);
//    }
//
//    public final int getWillpowerWounds() {
//        return attributeWounds.get(Attribute.WILLPOWER);
//    }
//
//    public final void setHealthWounds(int value) {
//        attributeWounds.set(Attribute.HEALTH, value);
//        //setDirty(true);
//    }
//
//
//    public final void setConstitutionWounds(int value) {
//        attributeWounds.set(Attribute.CONSTITUTION, value);
//        //setDirty(true);
//    }
//
//    public final void setActionWounds(int value) {
//        attributeWounds.set(Attribute.ACTION, value);
//        //setDirty(true);
//    }
//
//    public final void setStaminaWounds(int value) {
//        attributeWounds.set(Attribute.STAMINA, value);
//        //setDirty(true);
//    }
//
//    public final void setMindWounds(int value) {
//        attributeWounds.set(Attribute.MIND, value);
//        //setDirty(true);
//    }
//
//    public final void setWillpowerWounds(int value) {
//        attributeWounds.set(Attribute.WILLPOWER, value);
//        //setDirty(true);
//    }
//
public final void initializeAttribute(final int attribute, final int value) {
    attributes.set(attribute, value);
    maxAttributes.set(attribute, value);
}
//
//    //hamEncumbrance
//    public final int getHealthEncumbrance() {
//        return attribBonus.get(Attribute.HEALTH);
//    }
//
//    public final int getConstitutionEncumbrance() {
//        return attribBonus.get(Attribute.CONSTITUTION);
//    }
//
//    public final int getActionEncumbrance() {
//        return attribBonus.get(Attribute.ACTION);
//    }
//
//
//    public final int getStaminaEncumbrance() {
//        return attribBonus.get(Attribute.STAMINA);
//    }
//
//    public final int getMindEncumbrance() {
//        return attribBonus.get(Attribute.MIND);
//    }
//
//    public final int getWillpowerEncumbrance() {
//        return attribBonus.get(Attribute.WILLPOWER);
//    }
//
//    public final void setHealthEncumbrance(int value) {
//        attribBonus.set(Attribute.HEALTH, value);
//        //setDirty(true);
//    }
//
//    public final void setConstitutionEncumbrance(int value) {
//        attribBonus.set(Attribute.CONSTITUTION, value);
//        //setDirty(true);
//    }
//
//    public final void setActionEncumbrance(int value) {
//        attribBonus.set(Attribute.ACTION, value);
//        //setDirty(true);
//    }
//
//    public final void setStaminaEncumbrance(int value) {
//        attribBonus.set(Attribute.STAMINA, value);
//        //setDirty(true);
//    }
//
//    public final void setMindEncumbrance(int value) {
//        attribBonus.set(Attribute.MIND, value);
//        //setDirty(true);
//    }
//
//    public final void setWillpowerEncumbrance(int value) {
//        attribBonus.set(Attribute.WILLPOWER, value);
//        //setDirty(true);
//    }
//
//    //ham
//    public final int getHealth() {
//        return attributes.get(Attribute.HEALTH);
//    }
//
//    public final int getConstitution() {
//        return attributes.get(Attribute.CONSTITUTION);
//    }
//
//    public final int getAction() {
//        return attributes.get(Attribute.ACTION);
//    }
//
//    public final int getStamina() {
//        return attributes.get(Attribute.STAMINA);
//    }
//
//    public final int getMind() {
//        return attributes.get(Attribute.MIND);
//    }
//
//    public final int getWillpower() {
//        return attributes.get(Attribute.WILLPOWER);
//    }
//
//    public final void setAttributes(TIntList value) {
//        attributes.set(value);
//        //setDirty(true);
//    }
//
//    public final void setHealth(int value) {
//        attributes.set(Attribute.HEALTH, value);
//        //setDirty(true);
//    }
//
//    public final void setConstitution(int value) {
//        attributes.set(Attribute.CONSTITUTION, value);
//        //setDirty(true);
//    }
//
//    public final void setAction(int value) {
//        attributes.set(Attribute.ACTION, value);
//        //setDirty(true);
//    }
//
//    public final void setStamina(int value) {
//        attributes.set(Attribute.STAMINA, value);
//        //setDirty(true);
//    }
//
//    public final void setMind(int value) {
//        attributes.set(Attribute.MIND, value);
//        //setDirty(true);
//    }
//
//    public final void setWillpower(int value) {
//        attributes.set(Attribute.WILLPOWER, value);
//        //setDirty(true);
//    }
//
//    //hamMax
//    public final int getHealthMax() {
//        return maxAttributes.get(Attribute.HEALTH);
//    }
//
//    public final int getConstitutionMax() {
//        return maxAttributes.get(Attribute.CONSTITUTION);
//    }
//
//    public final int getActionMax() {
//        return maxAttributes.get(Attribute.ACTION);
//    }
//
//    public final int getStaminaMax() {
//        return maxAttributes.get(Attribute.STAMINA);
//    }
//
//    public final int getMindMax() {
//        return maxAttributes.get(Attribute.MIND);
//    }
//
//    public final int getWillpowerMax() {
//        return maxAttributes.get(Attribute.WILLPOWER);
//    }
//
//    public final void setMaxAttributes(TIntList value) {
//        maxAttributes.set(value);
//        //setDirty(true);
//    }
//
//    public final void setHealthMax(int value) {
//        maxAttributes.set(Attribute.HEALTH, value);
//        //setDirty(true);
//    }
//
//    public final void setConstitutionMax(int value) {
//        maxAttributes.set(Attribute.CONSTITUTION, value);
//        //setDirty(true);
//    }
//
//    public final void setActionMax(int value) {
//        maxAttributes.set(Attribute.ACTION, value);
//        //setDirty(true);
//    }
//
//    public final void setStaminaMax(int value) {
//        maxAttributes.set(Attribute.STAMINA, value);
//        //setDirty(true);
//    }
//
//    public final void setMindMax(int value) {
//        maxAttributes.set(Attribute.MIND, value);
//        //setDirty(true);
//    }
//
//    public final void setWillpowerMax(int value) {
//        maxAttributes.set(Attribute.WILLPOWER, value);
//        //setDirty(true);
//    }
//
//    public final float getRunSpeed() {
//        return runSpeed.get();
//    }
//
//    public final float setWalkSpeed() {
//        return walkSpeed.get();
//    }
//
//    public final float setSlopeModAngle() {
//        return slopeModAngle.get();
//    }
//
//    public final float setSlopeModPercent() {
//        return slopeModPercent.get();
//    }
//
//    public final float setWaterModPercent() {
//        return waterModPercent.get();
//    }
//
//    public final float getScaleFactor() {
//        return scaleFactor.get();
//    }
//
//    public final void setRunSpeed(float speed) {
//        runSpeed.set(speed);
//        //setDirty(true);
//    }
//
//    public final void setWalkSpeed(float speed) {
//        walkSpeed.set(speed);
//        //setDirty(true);
//    }
//
//    public final void setSlopeModAngle(float angle) {
//        slopeModAngle.set(angle);
//        //setDirty(true);
//    }
//
//    public final void setSlopeModPercent(float percent) {
//        slopeModPercent.set(percent);
//        //setDirty(true);
//    }
//
//    public final void setWaterModPercent(float percent) {
//        waterModPercent.set(percent);
//        //setDirty(true);
//    }
//
//    public final void setScaleFactor(float value) {
//        scaleFactor.set(value);
//        //setDirty(true);
//    }
//
//    public final boolean isIncapacitated() {
//        final byte posture = getPosture();
//        return (posture == CreaturePosture.INCAPACITATED /*&& !getState(CreatureState.FEIGN_DEATH)*/)
//                || posture == CreaturePosture.DEAD;
//    }
//
//    public final boolean isDead() {
//        return getPosture() == CreaturePosture.DEAD;
//    }
//
//    public boolean isDisabled() {
//        //Check if its a vehicle type or sub type, then super.isDisabled()
//        return (isIncapacitated() || isDead());
//    }
//
//
//    public void setLookAtYaw(final float lookAtYaw, final boolean useLookAtYaw) {
//        if (useLookAtYaw && (this.lookAtYaw != lookAtYaw))
//            setTransformChanged(true);
//
//        this.lookAtYaw = lookAtYaw;
//        this.useLookAtYaw = useLookAtYaw;
//    }
//
//    public float getLookAtYaw() {
//        return lookAtYaw;
//    }
//
//    public boolean getUseLookAtYaw() {
//        return useLookAtYaw;
//    }
//
//    @Override
//    protected void sendObjectSpecificBaselinesToClient(final SoeRequestContext client) {
//        super.sendObjectSpecificBaselinesToClient(client);
//
////        final Property property = getProperty(SlowDownProperty.getClassPropertyId());
////
////        if (property != null) {
////            final SlowDownProperty slowDownProperty = (SlowDownProperty)property;
////
////            final SlowDownEffectMessage message = new SlowDownEffectMessage();
////            client.sendMessage(message);
////        }
//
//        final UpdatePostureMessage updatePostureMessage = new UpdatePostureMessage(getContainerNetworkId(), getPosture());
//        client.sendMessage(updatePostureMessage);
//    }
//
//    /**
//     * Checks if the provided object is an instanceof CreatureObject, and then casts it. Otherwise returns null.
//     *
//     * @param object The object to cast to CreatureObject.
//     * @return If object is a CreatureObject, then casts it to CreatureObject. Otherwise, returns null.
//     */
//    public static CreatureObject asCreatureObject(final GameObject object) {
//        if (object instanceof CreatureObject)
//            return (CreatureObject) object;
//
//        return null;
//    }
}
