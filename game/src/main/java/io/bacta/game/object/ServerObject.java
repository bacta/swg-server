package io.bacta.game.object;


import io.bacta.archive.OnDirtyCallback;
import io.bacta.archive.delta.*;
import io.bacta.game.object.template.server.ServerObjectTemplate;
import io.bacta.shared.container.*;
import io.bacta.shared.localization.StringId;
import io.bacta.shared.object.GameObject;
import io.bacta.shared.object.template.SharedObjectTemplate;
import io.bacta.shared.portal.PortalProperty;
import io.bacta.shared.template.ObjectTemplateList;
import io.bacta.soe.context.SoeRequestContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashSet;
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

    private final AutoDeltaBoolean playerControlled;
    private final AutoDeltaBoolean persisted;
    private final AutoDeltaString sceneId;

    //private transient ServerSynchronizedUi synchornizedUi;

    private transient boolean initialized = false;

    protected transient final Set<SoeRequestContext> listeners;

    private transient int localFlags;

    protected transient final AutoDeltaByteStream authClientServerPackage = new AutoDeltaByteStream();
    protected transient final AutoDeltaByteStream authClientServerPackageNp = new AutoDeltaByteStream();
    protected transient final AutoDeltaByteStream firstParentAuthClientServerPackage = new AutoDeltaByteStream();
    protected transient final AutoDeltaByteStream firstParentAuthClientServerPackageNp = new AutoDeltaByteStream();
    protected transient final AutoDeltaByteStream serverPackage = new AutoDeltaByteStream();
    protected transient final AutoDeltaByteStream serverPackageNp = new AutoDeltaByteStream();
    protected transient final AutoDeltaByteStream sharedPackage = new AutoDeltaByteStream();
    protected transient final AutoDeltaByteStream sharedPackageNp = new AutoDeltaByteStream();
    //protected transient final AutoDeltaByteStream uiPackage = new AutoDeltaByteStream();
    protected transient final OnDirtyCallback<ServerObject> dirtyCallback = new OnDirtyCallback<>();

    int gameObjectType;

    SharedObjectTemplate sharedTemplate;

    @Inject
    public ServerObject(final ObjectTemplateList objectTemplateList,
                        final SlotIdManager slotIdManager,
                        final ServerObjectTemplate template,
                        final boolean hyperspaceOnCreate) {
        super(template);

        setupPackages();

        //assert DEFAULT_SHARED_TEMPLATE != null : "The default shared template for ServerObject has not been setup.";

        bankBalance = new AutoDeltaInt(0);
        cashBalance = new AutoDeltaInt(0);
        complexity = new AutoDeltaFloat(template.getComplexity());
        nameStringId = new AutoDeltaVariable<>(StringId.INVALID, StringId::new);
        objectName = new AutoDeltaUnicodeString();
        volume = new AutoDeltaInt(template.getVolume());
        descriptionStringId = new AutoDeltaVariable<>(StringId.INVALID, StringId::new);
        authServerProcessId = new AutoDeltaInt();

        playerControlled = new AutoDeltaBoolean();
        persisted = new AutoDeltaBoolean();
        sceneId = new AutoDeltaString("tatooine");

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
                        final SlottedContainer slottedContainer = new SlottedContainer(slotIdManager, this, slotDescriptor.getSlots());
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

    public String getObjectName() {
        return this.objectName.get();
    }

    public boolean isPlayerControlled() {
        return this.playerControlled.get();
    }

    public void setObjectName(final String name) {
        this.objectName.set(name);
        setDirty(true);
    }

    public void setPlayerControlled(boolean playerControlled) {
        this.playerControlled.set(playerControlled);
        setDirty(true);
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


    private void setupPackages() {
        dirtyCallback.set(this, this::deltaChanged);
        authClientServerPackage.addOnDirtyCallback(dirtyCallback);
        authClientServerPackageNp.addOnDirtyCallback(dirtyCallback);
        firstParentAuthClientServerPackage.addOnDirtyCallback(dirtyCallback);
        firstParentAuthClientServerPackageNp.addOnDirtyCallback(dirtyCallback);
        serverPackage.addOnDirtyCallback(dirtyCallback);
        serverPackageNp.addOnDirtyCallback(dirtyCallback);
        sharedPackage.addOnDirtyCallback(dirtyCallback);
        sharedPackageNp.addOnDirtyCallback(dirtyCallback);
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

        serverPackage.addVariable(persisted);
        serverPackage.addVariable(playerControlled);
        serverPackage.addVariable(sceneId);
    }

    private void deltaChanged() {
        dirty = true;
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

