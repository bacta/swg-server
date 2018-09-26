package io.bacta.game.object.tangible;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.bacta.archive.delta.AutoDeltaBoolean;
import io.bacta.archive.delta.AutoDeltaInt;
import io.bacta.archive.delta.AutoDeltaString;
import io.bacta.archive.delta.map.AutoDeltaStringObjectMap;
import io.bacta.archive.delta.set.AutoDeltaIntSet;
import io.bacta.archive.delta.set.AutoDeltaLongSet;
import io.bacta.engine.buffer.BufferUtil;
import io.bacta.engine.buffer.ByteBufferWritable;
import io.bacta.game.message.UpdatePvpStatusMessage;
import io.bacta.game.message.UpdateTransformMessage;
import io.bacta.game.object.ServerObject;
import io.bacta.game.object.tangible.creature.CreatureObject;
import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.game.object.template.server.ServerTangibleObjectTemplate;
import io.bacta.shared.object.template.SharedTangibleObjectTemplate;
import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.swg.container.Container;
import io.bacta.swg.container.SlotIdManager;
import io.bacta.swg.math.Transform;
import io.bacta.swg.math.Vector;
import io.bacta.swg.object.GameObject;
import io.bacta.swg.property.CustomizationDataProperty;
import io.bacta.swg.template.ObjectTemplateList;
import io.bacta.swg.utility.TriggerVolumeData;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class TangibleObject extends ServerObject
        //implements SteerSubject<Vec3>
{
    @Override
    public int getObjectType() {
        return 0x54414E4F;
    } //'TANO'

    public static transient ImmutableSet<TangibleObject> NO_NEAR_OBJECTS = ImmutableSet.of();

    private transient ImmutableSet<TangibleObject> nearObjects = NO_NEAR_OBJECTS;

    @Getter
    @Setter
    private transient int movementCounter = 0;

    @Getter
    @Setter
    private transient boolean inert = true;

    private String customAppearance;
    //private LocationData locationTargets

    @Getter
    private long ownerId;

    @Getter
    private boolean hidden;

    private final AutoDeltaInt pvpFaction;
    private final AutoDeltaInt pvpType;
    private final AutoDeltaString appearanceData;
    private final AutoDeltaIntSet components;
    private final AutoDeltaInt condition;
    private final AutoDeltaInt count;
    private final AutoDeltaInt damageTaken;
    private final AutoDeltaInt maxHitPoints;
    private final AutoDeltaBoolean visible;
    private final AutoDeltaBoolean inCombat;
    private final AutoDeltaLongSet passiveRevealPlayerCharacter;
    private final AutoDeltaInt mapColorOverride;
    private final AutoDeltaLongSet accessList;
    private final AutoDeltaIntSet guildAccessList;
    private final AutoDeltaStringObjectMap<TangibleObjectEffect> effectsMap;

    /**
     * The network id of the object which created this object.
     */
    private long creatorId;

    @Inject
    public TangibleObject(final ObjectTemplateList objectTemplateList,
                          final SlotIdManager slotIdManager,
                          final ServerObjectTemplate template) {
        super(objectTemplateList, slotIdManager, template, false);

        assert template instanceof ServerTangibleObjectTemplate;

        final ServerTangibleObjectTemplate objectTemplate = (ServerTangibleObjectTemplate) template;

        //hateList.setOwner(this);

        pvpFaction = new AutoDeltaInt();
        pvpType = new AutoDeltaInt();
        appearanceData = new AutoDeltaString();
        components = new AutoDeltaIntSet();
        condition = new AutoDeltaInt(objectTemplate.getCondition());
        count = new AutoDeltaInt(objectTemplate.getCount());
        damageTaken = new AutoDeltaInt();
        maxHitPoints = new AutoDeltaInt(objectTemplate.getMaxHitPoints());
        visible = new AutoDeltaBoolean(true);
        inCombat = new AutoDeltaBoolean();
        passiveRevealPlayerCharacter = new AutoDeltaLongSet();
        mapColorOverride = new AutoDeltaInt();
        accessList = new AutoDeltaLongSet();
        guildAccessList = new AutoDeltaIntSet();
        effectsMap = new AutoDeltaStringObjectMap<>(TangibleObjectEffect::new);

        //Set to not disabled, and set invulnerable and wantSawAttack from template
        int condition = this.condition.get();
        condition &= ~(ServerTangibleObjectTemplate.Conditions.C_disabled.value
                | ServerTangibleObjectTemplate.Conditions.C_invulnerable.value
                | ServerTangibleObjectTemplate.Conditions.C_wantSawAttackTrigger.value);

        if (objectTemplate.getInvulnerable())
            condition |= ServerTangibleObjectTemplate.Conditions.C_invulnerable.value;

        if (objectTemplate.getWantSawAttackTriggers())
            condition |= ServerTangibleObjectTemplate.Conditions.C_wantSawAttackTrigger.value;

        this.condition.set(condition);


        //TODO: Attach trigger volumes to this object.
        int i, count = objectTemplate.getTriggerVolumesCount();

        for (i = 0; i < count; ++i) {
            final TriggerVolumeData trigger = objectTemplate.getTriggerVolumes(i);
            //createTriggerVolume(trigger.getRadius(), trigger.getName(), true);
        }

        //Initialize customization data property if any customization variables are declared for the shared tangible object template.
        final SharedTangibleObjectTemplate sharedObjectTemplate = (SharedTangibleObjectTemplate) getSharedTemplate();

        if (sharedObjectTemplate != null) {
            //TODO: WTF?!?
            //sharedObjectTemplate.createCustomizationDataPropertyAsNeeded(this);

            final CustomizationDataProperty cdProperty = getProperty(CustomizationDataProperty.getClassPropertyId());

            if (cdProperty != null) {
                //retrieve the customization data instance associated with the property.
                //final CustomizationData customizationData = cdProperty.fetchCustomizationData();

                //customizationData.registerModificationListener(customizaitonDataModificationCallback, this);
            }

            //CreateAppearances
        }

        //if (isWaypoint())
        //addNotification(ServerPathfindingNotification::getInstance());

        //Attach the collision property
        //final ServerCollisionProperty collision = new ServerCollisionProperty(this, getSharedTemplate());

        //addProperty(collision);

        addMembersToPackages();
    }

    private void addMembersToPackages() {
        sharedPackage.addVariable(pvpFaction);
        sharedPackage.addVariable(pvpType);
        sharedPackage.addVariable(appearanceData);
        sharedPackage.addVariable(components);
        sharedPackage.addVariable(condition);
        sharedPackage.addVariable(count);
        sharedPackage.addVariable(damageTaken);
        sharedPackage.addVariable(maxHitPoints);
        sharedPackage.addVariable(visible);

        sharedPackageNp.addVariable(inCombat);
        sharedPackageNp.addVariable(passiveRevealPlayerCharacter);
        sharedPackageNp.addVariable(mapColorOverride);
        sharedPackageNp.addVariable(accessList);
        sharedPackageNp.addVariable(guildAccessList);
        sharedPackageNp.addVariable(effectsMap);
    }


    public TangibleObject[] getNearObjects() {
        return nearObjects.toArray(new TangibleObject[nearObjects.size()]);
    }

    public String getAppearanceData() {
        return appearanceData.get();
    }

    public void setAppearanceData(final String appearanceData) {
        this.appearanceData.set(appearanceData);
        //setDirty(true);
    }

    public final void setPosition(final Transform transform, boolean updateZone) {
        super.setPositionInWorld(transform.getPositionInParent());

        if (updateZone) {
            updateZone();
        }

        final int sequenceNumber = 0; //This value comes from the MessageQueueDataTransform packet.
        final byte speed = 2; //This value comes from the MessageQueueDataTransform packet.
        final byte lookAtYaw = 1; //This value comes from the MessageQueueDataTransform packet.
        final boolean useLookAtYaw = true; //This value comes from the MessageQueueDataTransform packet.

        //TODO: We should move this to the handler for the MessageQueueDataTransform packet...
        broadcastMessage(new UpdateTransformMessage(this, sequenceNumber, transform, speed, lookAtYaw, useLookAtYaw), false);
    }

    public void updateZone() {

        final ImmutableSet<TangibleObject> newNearObjects = getUpdatedNearObjects();

        final Set<TangibleObject> newObjects = Sets.difference(newNearObjects, nearObjects);
        final Set<TangibleObject> expiredObjects = Sets.difference(nearObjects, newNearObjects);

        nearObjects = newNearObjects;

        sendCreateAndBaselinesTo(newObjects.stream().filter(t -> t.connection != null).map(t -> t.connection).collect(Collectors.toSet()));
        sendDestroyTo(expiredObjects.stream().filter(t -> t.connection != null).map(t -> t.connection).collect(Collectors.toSet()));

        //Notify Appear
        newObjects.forEach(this::addInRangeObject);

        //Notify Disappear
        expiredObjects.forEach(this::removeInRangeObject);
    }

    public void clearZone() {
        //zone = null;
        nearObjects = NO_NEAR_OBJECTS;
    }

    private ImmutableSet<TangibleObject> getUpdatedNearObjects() {
        //if (zone == null) {
        return NO_NEAR_OBJECTS;
        //}

        //final UpdateTransformCallback updateTransformCallback = new UpdateTransformCallback(this);
        //zone.contains(getPositionInParent(), 160.f, Integer.MAX_VALUE, 1, updateTransformCallback);

        //return updateTransformCallback.getNearObjects();
    }

    public void updateNearObjects() {
        nearObjects = getUpdatedNearObjects();
    }

    public void addInRangeObject(final TangibleObject tano) {
        if (tano.getConnection() != null && listeners.add(tano.getConnection()))
            tano.addInRangeObject(this);
    }

    public void removeInRangeObject(final TangibleObject tano) {
        if (tano.getConnection() != null && listeners.remove(tano.getConnection()))
            tano.removeInRangeObject(this);
    }

    public final void setCondition(ServerTangibleObjectTemplate.Conditions condition) {
        int currentCondition = this.condition.get();
        this.condition.set(currentCondition | (int) condition.value);
    }

    public final void clearCondition(ServerTangibleObjectTemplate.Conditions condition) {
        int currentCondition = this.condition.get();
        this.condition.set(currentCondition & ((int) (~condition.value)));
    }

    public boolean hasCondition(int condition) {
        return (this.condition.get() & condition) != 0;
    }

    public boolean isInvulnerable() {
        return hasCondition((int) ServerTangibleObjectTemplate.Conditions.C_invulnerable.value);
    }

    public boolean isCrafted() {
        return hasCondition((int) ServerTangibleObjectTemplate.Conditions.C_crafted.value);
    }

    public boolean isCraftingTool() {
        //return getObjVars().hasItem(OBJVAR_CRAFTING_TOOL);
        //TODO: Implement obj vars...
        return false;
    }

    public long getCraftedId() {
        //TODO: implement obj vars...
        //return getObjVars().getItem(OBJVAR_CRAFTING_SCHEMATIC);
        return 0;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public int getMaxHitPoints() {
        return maxHitPoints.get();
    }

    public int getDamageTaken() {
        return damageTaken.get();
    }

    public void setCreatorId(long networkId) {
        this.creatorId = networkId;
    }

    public int getCount() {
        return this.count.get();
    }

    @Override
    public void setOwnerId(final long id) {

        // if we are changing owners, we are no longer insured
        boolean ownerChanged = false;
        if (ownerId != id) {
            ownerChanged = true;
            setInsured(false);
        }

        ownerId = id;
        final Container container = getContainerProperty();

        if (container != null) {
            final Iterator<GameObject> i = container.iterator();

            while (i.hasNext()) {

                ServerObject content = (ServerObject) i.next();

                if (content != null && CreatureObject.asCreatureObject(content) == null) {
                    content.setOwnerId(id);
                }
            }
        }

        // if we're hidden, who can see us has changed
        if (ownerChanged && isInWorld() && isHidden()) {
            visibilityDataModified();
        }
    }

    public void setHidden(final boolean hidden) {
        if (this.hidden != hidden) {
            visibilityDataModified();
            this.hidden = hidden;
        }
    }

    public void visibilityDataModified() {
        LOGGER.error("This method is not implemented");
        //TODO: Implement visibility modification
//        if (isInWorld()) {
//            if (isVisible() && !isHidden()) {
//                // show the object
//                final TriggerVolume triggerVolume = getTriggerVolume(NetworkTriggerVolumeNamespace::NetworkTriggerVolumeName);
//                if (triggerVolume != null) {
//                    std::vector<ServerObject *> observers;
//                    ServerWorld::findPlayerCreaturesInRange(getPosition_w(), triggerVolume->getRadius(), observers);
//                    if (!observers.empty())
//                        ObserveTracker::onObjectMadeVisibleTo(*this, observers);
//                }
//            }
//            else {
//                // hide the object
//                ObserveTracker::onObjectMadeInvisible(*this);
//
//                // if the object is hidden, show the object to
//                // players who have passively revealed the object
//                if (isVisible() && isHidden() && !m_passiveRevealPlayerCharacter.empty())
//                {
//                    const TriggerVolume * triggerVolume = getTriggerVolume(NetworkTriggerVolumeNamespace::NetworkTriggerVolumeName);
//                    if (triggerVolume != NULL) {
//                        std::vector<ServerObject *> possibleObservers;
//                        ServerWorld::findPlayerCreaturesInRange(getPosition_w(), triggerVolume->getRadius(), possibleObservers);
//                        if (!possibleObservers.empty())
//                        {
//                            std::vector<ServerObject *> allowedObservers;
//                            for (std::vector<ServerObject *>::const_iterator i = possibleObservers.begin(); i != possibleObservers.end(); ++i) {
//                                if (m_passiveRevealPlayerCharacter.contains((*i)->getNetworkId()))
//                                allowedObservers.push_back(*i);
//                            }
//
//                            if (!allowedObservers.empty())
//                                ObserveTracker::onObjectMadeVisibleTo(*this, allowedObservers);
//                        }
//                    }
//                }
//            }
//        }
    }

    public boolean isVisible() {
        return visible.get();
    }

    public void setInsured(final boolean insured) {
        if (insured)
            setCondition(ServerTangibleObjectTemplate.Conditions.C_insured);
        else
            clearCondition(ServerTangibleObjectTemplate.Conditions.C_insured);
    }

    public boolean isNonPvpObject() {
        return !getLocalFlag(LocalObjectFlags.PVPABLE);
    }

    @Override
    protected void sendObjectSpecificBaselinesToClient(final SoeConnection client) {
        super.sendObjectSpecificBaselinesToClient(client);

        if (!isNonPvpObject()) {
            int flags = 0, factionsId = 0;

            //uint32 flags, factionId;
            //Pvp::getClientVisibleStatus(client, this, flags, factionId);
            final UpdatePvpStatusMessage statusMessage = new UpdatePvpStatusMessage(getNetworkId(), flags, factionsId);
            client.sendMessage(statusMessage);
            //PvpUpdateObserver::updatePvpStatusCache(client, this, flags, factionId);
        }
    }


    public static TangibleObject asTangibleObject(final GameObject object) {
        if (object instanceof TangibleObject)
            return (TangibleObject) object;

        return null;
    }

    @AllArgsConstructor
    public static final class TangibleObjectEffect implements ByteBufferWritable {
        public final String filename;
        public final String hardpoint;
        public final Vector offset;
        public final float scale;

        public TangibleObjectEffect(final ByteBuffer buffer) {
            this.filename = BufferUtil.getAscii(buffer);
            this.hardpoint = BufferUtil.getAscii(buffer);
            this.offset = new Vector(buffer);
            this.scale = buffer.getFloat();
        }

        @Override
        public void writeToBuffer(final ByteBuffer buffer) {
            BufferUtil.put(buffer, this.filename);
            BufferUtil.put(buffer, this.hardpoint);
            BufferUtil.put(buffer, this.offset);
            BufferUtil.put(buffer, this.scale);
        }
    }

    public static final class LocalObjectFlags {
        //This might cause a problem if TANO is statically initialized first.
        public static final int PVPABLE = ServerObject.LocalObjectFlags.MAX;
        public static final int CUSTOMIZATION_DATA_MODIFIED = ServerObject.LocalObjectFlags.MAX + 1;

        public static final int MAX = ServerObject.LocalObjectFlags.MAX + 2;
    }
}
