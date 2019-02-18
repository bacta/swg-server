package io.bacta.game.object;


import io.bacta.archive.OnDirtyCallbackBase;
import io.bacta.archive.delta.*;
import io.bacta.game.ObjControllerMessage;
import io.bacta.game.message.*;
import io.bacta.game.object.cell.CellObject;
import io.bacta.game.object.intangible.player.PlayerObject;
import io.bacta.game.object.tangible.TangibleObject;
import io.bacta.game.object.tangible.creature.CreatureObject;
import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.game.object.universe.group.GroupObject;
import io.bacta.game.service.container.ContainerTransferService;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.shared.container.*;
import io.bacta.shared.localization.StringId;
import io.bacta.shared.object.GameObject;
import io.bacta.shared.object.template.SharedObjectTemplate;
import io.bacta.shared.portal.PortalProperty;
import io.bacta.shared.template.ObjectTemplateList;
import io.bacta.shared.util.NetworkId;
import io.bacta.soe.context.SoeRequestContext;
import io.bacta.soe.util.SoeMessageUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

@Slf4j
@Getter
public abstract class ServerObject extends GameObject {

    private static SharedObjectTemplate DEFAULT_SHARED_TEMPLATE; //Gets set by a startup service.

    private final AutoDeltaInt bankBalance;
    private final AutoDeltaInt cashBalance;
    private final AutoDeltaFloat complexity;
    private final AutoDeltaVariable<StringId> nameStringId;
    private final AutoDeltaUnicodeString objectName;
    private final AutoDeltaInt volume;
    private final AutoDeltaInt authServerProcessId;
    private final AutoDeltaVariable<StringId> descriptionStringId;

    //private transient ServerSynchronizedUi synchornizedUi;

    @Getter
    @Setter
    private String sceneId;

    @Getter
    @Setter
    private boolean playerControlled;

    private transient boolean initialized = false;

    protected transient int movementSequenceId = 0;

    @Getter
    @Setter
    protected transient SoeRequestContext connection;

    //SOE Kept a pointer to the ScriptReference. We are going to try just the String for now.
    //Notice that this value could be null. It shouldn't be accessed directly.
    @Getter
    @Setter
    private transient Set<String> attachedScripts;

    protected transient final Set<SoeRequestContext> listeners;

    private transient int localFlags;

    protected transient final AutoDeltaByteStream authClientServerPackage = new AutoDeltaByteStream();
    protected transient final AutoDeltaByteStream authClientServerPackageNp = new AutoDeltaByteStream();
    protected transient final AutoDeltaByteStream firstParentAuthClientServerPackage = new AutoDeltaByteStream();
    protected transient final AutoDeltaByteStream firstParentAuthClientServerPackageNp = new AutoDeltaByteStream();
    protected transient final AutoDeltaByteStream sharedPackage = new AutoDeltaByteStream();
    protected transient final AutoDeltaByteStream sharedPackageNp = new AutoDeltaByteStream();
    protected transient final AutoDeltaByteStream uiPackage = new AutoDeltaByteStream();

    int gameObjectType;

    SharedObjectTemplate sharedTemplate;

    @Inject
    public ServerObject(final ObjectTemplateList objectTemplateList,
                        final SlotIdManager slotIdManager,
                        final ServerObjectTemplate template,
                        final boolean hyperspaceOnCreate) {
        super(template);

        //assert DEFAULT_SHARED_TEMPLATE != null : "The default shared template for ServerObject has not been setup.";

        bankBalance = new AutoDeltaInt(0);
        cashBalance = new AutoDeltaInt(0);
        complexity = new AutoDeltaFloat(template.getComplexity());
        nameStringId = new AutoDeltaVariable<>(StringId.INVALID, StringId::new);
        objectName = new AutoDeltaUnicodeString();
        volume = new AutoDeltaInt(template.getVolume());
        descriptionStringId = new AutoDeltaVariable<>(StringId.INVALID, StringId::new);
        authServerProcessId = new AutoDeltaInt();
        sceneId = "tatooine";

        listeners = Collections.synchronizedSet(new HashSet<>());

        setLocalFlag(ServerObject.LocalObjectFlags.HYPERSPACE_ON_CREATE, hyperspaceOnCreate);
        setLocalFlag(ServerObject.LocalObjectFlags.HYPERSPACE_ON_DESTRUCT, false);

        final String sharedTemplateName = template.getSharedTemplate();
        sharedTemplate = objectTemplateList.fetch(sharedTemplateName);

        if (sharedTemplate == null && !sharedTemplateName.isEmpty()) {
            LOGGER.warn("Template {} has an invalid shared template {}. We will use the default shared template for now.",
                    template.getResourceName(),
                    sharedTemplateName);
        }

        //Instead of calling getSharedTemplate() 100x, let's just cache the result...
        final SharedObjectTemplate localSharedTemplate = getSharedTemplate();

        if (localSharedTemplate != null) {
            nameStringId.set(localSharedTemplate.getObjectName());
            descriptionStringId.set(localSharedTemplate.getDetailedDescription());
        }

        final ContainedByProperty containedBy = new ContainedByProperty(this, null);
        addProperty(containedBy);

        final SlottedContainmentProperty slottedProperty = new SlottedContainmentProperty(this, slotIdManager);
        addProperty(slottedProperty);

        if (localSharedTemplate != null) {
            final ArrangementDescriptor arrangementDescriptor = localSharedTemplate.getArrangementDescriptor();

            if (arrangementDescriptor != null) {
                final int arrangementCount = arrangementDescriptor.getArrangementCount();

                for (int i = 0; i < arrangementCount; ++i)
                    slottedProperty.addArrangement(arrangementDescriptor.getArrangement(i));
            }
        }

        if (localSharedTemplate != null) {
            final SharedObjectTemplate.ContainerType containerType = localSharedTemplate.getContainerType();

            switch (containerType) {
                case CT_none:
                    break;

                case CT_slotted:
                case CT_ridable: {
                    final SlotDescriptor slotDescriptor = localSharedTemplate.getSlotDescriptor();

                    if (slotDescriptor != null) {
                        final SlottedContainer slottedContainer = new SlottedContainer(this, slotDescriptor.getSlots());
                        addProperty(slottedContainer);
                    }
                    break;
                }
                case CT_volume: {
                    int maxVolume = localSharedTemplate.getContainerVolumeLimit();

                    if (maxVolume <= 0)
                        maxVolume = VolumeContainer.NO_VOLUME_LIMIT;

                    final VolumeContainer volumeContainer = new TangibleVolumeContainer(this, maxVolume);
                    addProperty(volumeContainer);
                    break;
                }
                case CT_volumeIntangible: {
                    int maxVolume = localSharedTemplate.getContainerVolumeLimit();

                    if (maxVolume <= 0)
                        maxVolume = VolumeContainer.NO_VOLUME_LIMIT;

                    final VolumeContainer volumeContainer = new IntangibleVolumeContainer(this, maxVolume);
                    addProperty(volumeContainer);
                    break;
                }
                case CT_volumeGeneric: {
                    int maxVolume = localSharedTemplate.getContainerVolumeLimit();

                    if (maxVolume <= 0)
                        maxVolume = VolumeContainer.NO_VOLUME_LIMIT;

                    final VolumeContainer volumeContainer = new VolumeContainer(this, maxVolume);
                    addProperty(volumeContainer);
                    break;
                }
                default:
                    LOGGER.warn("Invalid container type specified.");
                    break;
            }

            setLocalFlag(ServerObject.LocalObjectFlags.SEND_TO_CLIENT, localSharedTemplate.getSendToClient());
            gameObjectType = (int) localSharedTemplate.getGameObjectType().value;
        }

        int vol = template.getVolume();
        final VolumeContainmentProperty volumeProperty = new VolumeContainmentProperty(this, vol < 1 ? 1 : vol);
        addProperty(volumeProperty);

        if (localSharedTemplate != null) {
            final String portalLayoutFileName = localSharedTemplate.getPortalLayoutFilename();

            if (!portalLayoutFileName.isEmpty()) {
                final PortalProperty portalProperty = new PortalProperty(this, portalLayoutFileName);
                addProperty(portalProperty);
            }
        }

        addMembersToPackages();
    }

    private void addMembersToPackages() {
        authClientServerPackage.addVariable(bankBalance);
        authClientServerPackage.addVariable(cashBalance);

        sharedPackage.addVariable(complexity);
        sharedPackage.addVariable(nameStringId);
        sharedPackage.addVariable(objectName);
        sharedPackage.addVariable(volume);

        sharedPackageNp.addVariable(authServerProcessId);
        sharedPackageNp.addVariable(descriptionStringId);
    }

    public final boolean getLocalFlag(final int flag) {
        return (localFlags & (1 << flag)) != 0;
    }

    public final void setLocalFlag(final int flag, final boolean enabled) {
        if (enabled)
            localFlags |= (1 << flag);
        else
            localFlags &= ~(1 << flag);
    }

    public final int getTotalMoney() {
        return getBalanceCash() + getBalanceBank();
    }

    public final SharedObjectTemplate getSharedTemplate() {
        return sharedTemplate != null ? sharedTemplate : getDefaultSharedTemplate();
    }

    public final void setSharedTemplate(final SharedObjectTemplate template) {
        this.sharedTemplate = template;
    }

    public final void setSceneIdOnThisAndContents(final String newSceneId) {
        setSceneId(newSceneId);

        final Container container = getContainerProperty();

        if (container != null) {
            final Iterator<GameObject> containerIterator = container.iterator();

            containerIterator.forEachRemaining(containedObject ->
                    ((ServerObject) containedObject).setSceneIdOnThisAndContents(newSceneId));
        }
    }

    public final int getBalanceBank() {
        return bankBalance.get();
    }

    public final void setBalanceBank(int value) {
        bankBalance.set(value);
        //setDirty(true);
    }

    public final int getBalanceCash() {
        return cashBalance.get();
    }

    public final void setBalanceCash(int value) {
        cashBalance.set(value);
        //setDirty(true);
    }

    public final float getComplexity() {
        return complexity.get();
    }

    public final void setComplexity(float value) {
        complexity.set(value);
        //setDirty(true);
    }

    public final StringId getNameStringId() {
        return nameStringId.get();
    }

    public final void setNameStringId(StringId value) {
        nameStringId.set(value);
        //setDirty(true);
    }

    public final String getAssignedObjectName() {
        return objectName.get();
    }

    public final String getAssignedObjectFirstName() {
        final String name = objectName.get();

        final int spacePos = name.indexOf(' ');

        if (spacePos > -1)
            return name.substring(0, spacePos);

        return name;
    }

    /**
     * This function is used to obtain a client-displayable name regardless of whether
     * the object has an assigned name or string id.
     */
    public final String getEncodedObjectName() {
        final String objName = objectName.get();

        if (!objName.isEmpty())
            return objName;
        else
            return "@" + nameStringId.get().getCanonicalRepresentation();
    }

    public final void setAssignedObjectName(final String value) {
        objectName.set(value);
        //setDirty(true);
    }

    public final int getVolume() {
        return volume.get();
    }

    public final void setVolume(int value) {
        volume.set(value);
        //setDirty(true);
    }

    public final int getAuthServerProcessId() {
        return authServerProcessId.get();
    }

    public final void setAuthServerProcessId(int value) {
        authServerProcessId.set(value);
        //setDirty(true);
    }

    private final void flushCreateMessages() {
        //Check and log invalid transform


    }

    /**
     * Send the create and baselines to each object in a set if it has a valid connection.
     *
     * @param clients The client connections that are receiving the create and baseline messages.
     */
    public final void sendCreateAndBaselinesTo(final Set<SoeRequestContext> clients) {
        LOGGER.trace("Sending create and baselines to {}.", getNetworkId());

        //Don't send anything if we are being destroyed or are not supposed to send to the client.
        if (isBeingDestroyed() || !isSendToClient())
            return;

        final SceneCreateObjectByCrc msg = new SceneCreateObjectByCrc(
                getNetworkId(),
                getTransformObjectToParent(),
                getSharedTemplate().getCrcName().getCrc(),
                getLocalFlag(LocalObjectFlags.HYPERSPACE_ON_CREATE));

        final UpdateContainmentMessage ucm = new UpdateContainmentMessage(
                getNetworkId(),
                getContainedBy(),
                getCurrentArrangement());

        final boolean sendUpdateContainment = (ucm.getContainerId() != NetworkId.INVALID || ucm.getSlotArrangement() != -1);

        final BaselinesMessage sharedBaselines = new BaselinesMessage(this, sharedPackage, BaselinesMessage.BASELINES_SHARED);
        final BaselinesMessage sharedNpBaselines = new BaselinesMessage(this, sharedPackageNp, BaselinesMessage.BASELINES_SHARED_NP);
        final BaselinesMessage authClientServerBaselines = new BaselinesMessage(this, authClientServerPackage, BaselinesMessage.BASELINES_CLIENT_SERVER);
        final BaselinesMessage authClientServerNpBaselines = new BaselinesMessage(this, authClientServerPackageNp, BaselinesMessage.BASELINES_CLIENT_SERVER_NP);
        final BaselinesMessage firstParentAuthClientServerBaselines = new BaselinesMessage(this, firstParentAuthClientServerPackage, BaselinesMessage.BASELINES_FIRST_PARENT_CLIENT_SERVER);
        final BaselinesMessage firstParentAuthClientServerNpBaselines = new BaselinesMessage(this, firstParentAuthClientServerPackageNp, BaselinesMessage.BASELINES_FIRST_PARENT_CLIENT_SERVER_NP);

        for (final SoeRequestContext client : clients) {
            client.sendMessage(msg);

            if (sendUpdateContainment)
                client.sendMessage(ucm);

            client.sendMessage(sharedBaselines);
            client.sendMessage(sharedNpBaselines);
        }

        //These only get sent to this client.
        final SoeRequestContext authClient = getConnection();

        if (authClient != null) {
            authClient.sendMessage(authClientServerBaselines);
            authClient.sendMessage(authClientServerNpBaselines);
        }

        final SoeRequestContext firstParentClient = getParentPlayerClient(this);

        if (firstParentClient != null) {
            firstParentClient.sendMessage(firstParentAuthClientServerBaselines);
            firstParentClient.sendMessage(firstParentAuthClientServerNpBaselines);
        }

        //Send create messages for contents
        final Container container = getContainerProperty();

        if (container != null) {
            final Iterator<GameObject> containerIterator = container.iterator();

            while (containerIterator.hasNext()) {
                final ServerObject object = (ServerObject) containerIterator.next();

                object.sendCreateAndBaselinesTo(clients);
            }
        }

        final SceneEndBaselines endBaselines = new SceneEndBaselines(getNetworkId());

        for (final SoeRequestContext client : clients) {
            sendObjectSpecificBaselinesToClient(client);
            client.sendMessage(endBaselines);
        }
    }

    protected void sendObjectSpecificBaselinesToClient(final SoeRequestContext client) {
        //To be implemented by sub classes to send special baseline messages.
    }

    public final void sendDestroyTo(final Set<SoeRequestContext> clients) {
        SceneDestroyObject msg = null;

        for (final SoeRequestContext client : clients) {
            //If its the first client, create the message.
            //This will prevent us from creating a message if the set is empty.
            if (msg == null)
                msg = new SceneDestroyObject(
                        getNetworkId(),
                        getLocalFlag(LocalObjectFlags.HYPERSPACE_ON_DESTRUCT));

            client.sendMessage(msg);
        }
    }

    public final void setInitialized() {
        clearDeltas(); //Clear any deltas that might've been set before initialization.

        initialized = true;
    }

    public final void sendDeltas() {
//        if (authClientServerPackage).isDirty())
//            broadcastMessage(new DeltasMessage(this, authClientServerPackage, 1));
//
//        if (sharedPackage.isDirty())
//            getConnection().sendMessage(new DeltasMessage(this, sharedPackage, 3));
//
//        if (authClientServerPackageNp.isDirty())
//            getConnection().sendMessage(new DeltasMessage(this, authClientServerPackageNp, 4));
//
//        if (sharedPackageNp.isDirty())
//            broadcastMessage(new DeltasMessage(this, sharedPackageNp, 6));
//
//        if (uiPackage.isDirty())
//            getConnection().sendMessage(new DeltasMessage(this, uiPackage, 7));
//
//        if (firstParentAuthClientServerPackage.isDirty())
//            getConnection().sendMessage(new DeltasMessage(this, firstParentAuthClientServerPackage, 8));
//
//        if (firstParentAuthClientServerPackageNp.isDirty())
//            getConnection().sendMessage(new DeltasMessage(this, firstParentAuthClientServerPackageNp, 9));

        //clearDeltas();
    }

    public final void clearDeltas() {
//        authClientServerPackage.clearDeltas();
//        authClientServerPackageNp.clearDeltas();
//        sharedPackage.clearDeltas();
//        sharedPackageNp.clearDeltas();
//        uiPackage.clearDeltas();
//        firstParentAuthClientServerPackage.clearDeltas();
//        firstParentAuthClientServerPackageNp.clearDeltas();
    }

    public void setOnDirtyCallback(final OnDirtyCallbackBase onDirtyCallback) {
        sharedPackage.addOnDirtyCallback(onDirtyCallback);
        sharedPackageNp.addOnDirtyCallback(onDirtyCallback);
        authClientServerPackage.addOnDirtyCallback(onDirtyCallback);
        authClientServerPackageNp.addOnDirtyCallback(onDirtyCallback);
        firstParentAuthClientServerPackage.addOnDirtyCallback(onDirtyCallback);
        firstParentAuthClientServerPackageNp.addOnDirtyCallback(onDirtyCallback);
        uiPackage.addOnDirtyCallback(onDirtyCallback);
    }

    public final void broadcastMessage(GameNetworkMessage message) {
        broadcastMessage(message, true);
    }

    public final void broadcastMessage(GameNetworkMessage message, boolean sendSelf) {

        for (SoeRequestContext theirConnection : listeners) {
            if (!sendSelf && theirConnection == getConnection()) {
                continue;
            }
            theirConnection.sendMessage(message);
            LOGGER.debug("Broadcasting message {} to {}", message.getClass().getSimpleName(), theirConnection.getCurrentCharName());
            System.out.println(SoeMessageUtil.bytesToHex(message));
        }
    }

    public final void broadcastMessage(ObjControllerMessage message) {
        broadcastMessage(message, false);
    }

    public final void broadcastMessage(ObjControllerMessage message, boolean changeReceiver) {
        for (final SoeRequestContext theirConnection : listeners) {

            if (changeReceiver) {
                LOGGER.error("Set receiver is disabled right now! FIX THIS.");
                //message.setReceiver(theirConnection.getCurrentNetworkId());
            }

            theirConnection.sendMessage(message);

            LOGGER.debug("Broadcasting obj controller to {}", theirConnection.getCurrentCharName());
            System.out.println(SoeMessageUtil.bytesToHex(message));
        }
    }

    /**
     * Returns a shared template if none was given for this object.
     *
     * @return The shared template
     */
    protected static final SharedObjectTemplate getDefaultSharedTemplate() {
        if (DEFAULT_SHARED_TEMPLATE != null)
            return DEFAULT_SHARED_TEMPLATE;

        throw new IllegalStateException("The default SharedObjectTemplate for ServerObject was not set. See ServerObject.setDefaultSharedTemplate.");
    }


    protected final void setInitialized(final boolean initialized) {
        setLocalFlag(LocalObjectFlags.INITIALIZED, initialized);
    }

    protected final void setBeingDestroyed(final boolean beingDestroyed) {
        setLocalFlag(LocalObjectFlags.BEING_DESTROYED, beingDestroyed);

        //if (beingDestroyed && isAuthoritative() && getScriptObject())
        //    getScriptObject().setOwnerDestroyed();

        //Stop listening to broadcast messages.
        //if (beingDestroyed && isAuthoritative())
        //    stopListeningToAllBroadcastMessages();
    }

    protected final void setPlacing(final boolean placing) {
        setLocalFlag(LocalObjectFlags.PLACING, placing);
    }

    protected final void setUnloading(final boolean unloading) {
        setLocalFlag(LocalObjectFlags.UNLOADING, unloading);
    }

    protected final void setGoingToConclude(final boolean goingToConclude) {
        setLocalFlag(LocalObjectFlags.GOING_TO_CONCLUDE, goingToConclude);
    }

    protected final void setInEndBaselines(final boolean inEndBaselines) {
        setLocalFlag(LocalObjectFlags.IN_END_BASELINES, inEndBaselines);
    }

    protected final void setNeedsPobFixup(final boolean needsPobFixup) {
        setLocalFlag(LocalObjectFlags.NEEDS_POB_FIX, needsPobFixup);
    }

    public final boolean isInitialized() {
        return getLocalFlag(LocalObjectFlags.INITIALIZED);
    }

    public final boolean isPlacing() {
        return getLocalFlag(LocalObjectFlags.PLACING);
    }

    public final boolean isUnloading() {
        return getLocalFlag(LocalObjectFlags.UNLOADING);
    }

    public final boolean isBeingDestroyed() {
        return getLocalFlag(LocalObjectFlags.BEING_DESTROYED);
    }

    public final boolean isGoingToConclude() {
        return getLocalFlag(LocalObjectFlags.GOING_TO_CONCLUDE);
    }

    public final boolean isInEndBaselines() {
        return getLocalFlag(LocalObjectFlags.IN_END_BASELINES);
    }

    public final boolean isNeedingPobFixup() {
        return getLocalFlag(LocalObjectFlags.NEEDS_POB_FIX);
    }

    public final boolean isSendToClient() {
        return getLocalFlag(LocalObjectFlags.SEND_TO_CLIENT);
    }

    public float getLocationReservationRadius() {
        return getSharedTemplate() != null ? getSharedTemplate().getLocationReservationRadius() : 0.f;
    }

    public void setTransformChanged(final boolean changed) {
        setLocalFlag(LocalObjectFlags.TRANSFORM_CHANGED, changed);

        if (changed) {
            //addObjectToConcludeList();

            //if (isAuthoritative() && isPersisted())
            //PositionUpdateTracker.positionChanged(this);
        }
    }

    public boolean getTransformChanged() {
        return getLocalFlag(LocalObjectFlags.TRANSFORM_CHANGED);
    }


    /**
     * Determines if this object is contained by another object.
     *
     * @param container       The object that might contain this one.
     * @param includeContents If true, return true if one of the container's contents contains us.
     * @return True if we are contained by the container (or its contents).
     */
    public boolean isContainedBy(final ServerObject container, final boolean includeContents) {
        final ContainedByProperty containedByProperty = getContainedByProperty();
        final GameObject test = containedByProperty != null ? containedByProperty.getContainedBy() : null;

        if (test == container)
            return true;

        if (test != null && test != this && includeContents)
            ((ServerObject) test).isContainedBy(container, true);

        return false;
    }

    public long getContainedBy() {
        final ContainedByProperty containedByProperty = getContainedByProperty();

        if (containedByProperty != null) {

            final GameObject containedBy = containedByProperty.getContainedBy();

            if (containedBy != null) {
                return containedBy.getNetworkId();
            }
        }
        return 0;
    }

    public int getCurrentArrangement() {
        final SlottedContainmentProperty slottedContainmentProperty = getProperty(SlottedContainmentProperty.getClassPropertyId());

        if (slottedContainmentProperty != null)
            return slottedContainmentProperty.getCurrentArrangement();
        return -1;
    }

    public boolean canDropInWorld() {
        return false;
    }

    public void onContainerTransferComplete(final ServerObject oldContainer, final ServerObject newContainer) {
        setTransformChanged(true);

        if ((oldContainer instanceof CellObject)
                || (newContainer instanceof CellObject)) {
            //positionUpdateTracker::flushPositionUpdate(this);
        }

        //TODO: Finish this logic?
    }

    public void setObjectName(final String newName) {

        if (isPlayerControlled()) {
            LOGGER.debug("You cannot set the name of a player controlled object directly.  Name:{}", newName);
            return;
        }

        if (newName.length() > 127) {
            LOGGER.debug("Tried to set object {} name to something too long (truncating). [{}]", getNetworkId(), newName);
            objectName.set(newName.substring(0, 127));
        } else
            objectName.set(newName);

        if (newName.startsWith("@")) {
            StringId id = new StringId(newName);
            setObjectNameStringId(id);
            setObjectName("");
        }
    }

    public void setObjectNameStringId(final StringId id) {
        nameStringId.set(id);
    }

    public void setOwnerId(final long id) {

    }

    public TangibleObject asTangibleObject() {
        return this instanceof TangibleObject ? (TangibleObject) this : null;
    }

    public CreatureObject asCreatureObject() {
        return this instanceof CreatureObject ? (CreatureObject) this : null;
    }

    public PlayerObject asPlayerObject() {
        return this instanceof PlayerObject ? (PlayerObject) this : null;
    }

    public GroupObject asGroupObject() {
        return this instanceof GroupObject ? (GroupObject) this : null;
    }

    public static final SoeRequestContext getParentPlayerClient(ServerObject obj) {
        while (obj != null && !obj.isInWorld() && !obj.isPlayerControlled())
            obj = (ServerObject) ContainerTransferService.getContainedByObject(obj);

        return obj != null ? obj.getConnection() : null;
    }

    public static class LocalObjectFlags {
        public static final int INITIALIZED = 0;
        public static final int BEING_DESTROYED = 1;
        public static final int PLACING = 2;
        public static final int TRANSFORM_CHANGED = 3;
        public static final int UNLOADING = 4;
        public static final int GOING_TO_CONCLUDE = 5;
        public static final int IN_END_BASELINES = 6;
        public static final int AUTO_DELTA_CHANGED = 7;
        public static final int SEND_TO_CLIENT = 8;
        public static final int HYPERSPACE_ON_CREATE = 9;
        public static final int HYPERSPACE_ON_DESTRUCT = 10;
        public static final int DIRTY_OBJECT_MENU_SENT = 11;
        public static final int DIRTY_ATTRIBUTES_SENT = 12;
        public static final int NEEDS_POB_FIX = 13;

        //Set this to the last flag + 1. It's used to initialize the value in TangibleObject?
        public static final int MAX = 14;
    }
}

