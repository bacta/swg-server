package io.bacta.game.object;

import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import akka.persistence.*;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.bacta.game.message.*;
import io.bacta.shared.util.NetworkId;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ServerObjectActor<T extends ServerObject> extends AbstractPersistentActor {
    /**
     * The underlying object that this actor manages. When this object mutates, the actor
     * is configured to send out deltas to nearby objects. It will also periodically send
     * serialize requests to the database.
     */
    @Getter
    private T object;
    /**
     * The id of the object, cached as a string to serve as the persistence id for the actor
     * persistence framework.
     */
    private String objectIdString;
    /**
     * Other actors who are interested in receiving baselines and deltas from the object this actor controls.
     */
    protected final Set<ActorRef> listeners;
    /**
     * Stores a local cache of objects that this object is observing.
     */
    protected final TLongObjectMap<ServerObject> localObjectCache;

    /**
     * This constructor is for injecting an object that has not yet been persisted to the
     * data store. It's object id should already be set.
     * @param serverObject
     */
    protected ServerObjectActor(T serverObject) {
        this();

        if (serverObject.getNetworkId() == NetworkId.INVALID)
            throw new IllegalArgumentException("The server object must already have its network id set so that it may be persisted.");

        this.setObject(serverObject);

        //TODO: We should go ahead and save a snapshot immediately for this object.
    }

    protected ServerObjectActor(long objectId) {
        this();

        if (objectId == NetworkId.INVALID)
            throw new IllegalArgumentException("The object id must already be a persistent id so that the snapshot may be loaded.");

        this.objectIdString = String.valueOf(objectId);
    }

    private ServerObjectActor() {
        this.listeners = new HashSet<>();
        this.localObjectCache = new TLongObjectHashMap<>(100);
    }

    protected void setObject(T object) {
        this.object = object;
        this.objectIdString = String.valueOf(object.getNetworkId());
    }

    @Override
    public String persistenceId() {
        return objectIdString;
    }

    @Override
    public final Receive createReceiveRecover() {
        return this.appendReceiveRecoverHandlers(receiveBuilder()).build();
    }

    @Override
    public final Receive createReceive() {
        return this.appendReceiveHandlers(receiveBuilder()).build();
    }

    protected ReceiveBuilder appendReceiveHandlers(ReceiveBuilder receiveBuilder) {
        return receiveBuilder
                .match(SaveSnapshotSuccess.class, this::snapshotSuccess)
                .match(SaveSnapshotFailure.class, this::snapshotFailure)
                .match(SceneCreateObjectByCrc.class, this::sceneCreateObjectByCrc)
                .match(UpdateContainmentMessage.class, this::updateContainment)
                .match(BaselinesMessage.class, this::applyBaselines)
                .match(SceneEndBaselines.class, this::endBaselines)
                .match(DeltasMessage.class, this::applyDeltas);

    }

    @SuppressWarnings("unchecked")
    protected ReceiveBuilder appendReceiveRecoverHandlers(ReceiveBuilder receiveBuilder) {
        return receiveBuilder
                .match(RecoveryCompleted.class, this::recoveryCompleted)
                .match(SnapshotOffer.class, snapshotState -> this.setObject((T) snapshotState.snapshot()));
    }

    /**
     * Override this method to define what happens when recovery completes. Don't forget to call
     * super.
     * @param recoveryCompleted The recover completed message.
     */
    protected void recoveryCompleted(RecoveryCompleted recoveryCompleted) {
        LOGGER.trace("Recovery completed.");
    }

    protected void applyBaselines(BaselinesMessage msg) {
        final ByteBuffer buffer = msg.getPackageBuffer();

        switch (msg.getPackageId()) {
            case BaselinesMessage.BASELINES_CLIENT_SERVER:
                this.object.authClientServerPackage.unpack(buffer);
                break;
            case BaselinesMessage.BASELINES_SERVER:
                this.object.serverPackage.unpack(buffer);
                break;
            case BaselinesMessage.BASELINES_SHARED:
                this.object.sharedPackage.unpack(buffer);
                break;
            case BaselinesMessage.BASELINES_CLIENT_SERVER_NP:
                this.object.authClientServerPackageNp.unpack(buffer);
                break;
            case BaselinesMessage.BASELINES_SERVER_NP:
                this.object.serverPackageNp.unpack(buffer);
                break;
            case BaselinesMessage.BASELINES_SHARED_NP:
                this.object.sharedPackageNp.unpack(buffer);
                break;
            case BaselinesMessage.BASELINES_FIRST_PARENT_CLIENT_SERVER:
                this.object.firstParentAuthClientServerPackage.unpack(buffer);
                break;
            case BaselinesMessage.BASELINES_FIRST_PARENT_CLIENT_SERVER_NP:
                this.object.firstParentAuthClientServerPackageNp.unpack(buffer);
                break;
            case BaselinesMessage.BASELINES_UI:
                //Check if there is any synchronized ui ...if so apply baselines to them.
                break;
            default:
                //Unknown package.
        }
    }

    protected void applyDeltas(DeltasMessage msg) {
        final ByteBuffer buffer = msg.getPackageBuffer();

        switch (msg.getPackageId()) {
            case DeltasMessage.DELTAS_CLIENT_SERVER:
                this.object.authClientServerPackage.unpackDeltas(buffer);
                break;
            case DeltasMessage.DELTAS_SERVER:
                this.object.serverPackage.unpackDeltas(buffer);
                break;
            case DeltasMessage.DELTAS_SHARED:
                this.object.sharedPackage.unpackDeltas(buffer);
                break;
            case DeltasMessage.DELTAS_CLIENT_SERVER_NP:
                this.object.authClientServerPackageNp.unpackDeltas(buffer);
                break;
            case DeltasMessage.DELTAS_SERVER_NP:
                this.object.serverPackageNp.unpackDeltas(buffer);
                break;
            case DeltasMessage.DELTAS_SHARED_NP:
                this.object.sharedPackageNp.unpackDeltas(buffer);
                break;
            case DeltasMessage.DELTAS_FIRST_PARENT_CLIENT_SERVER:
                this.object.firstParentAuthClientServerPackage.unpackDeltas(buffer);
                break;
            case DeltasMessage.DELTAS_FIRST_PARENT_CLIENT_SERVER_NP:
                this.object.firstParentAuthClientServerPackageNp.unpackDeltas(buffer);
                break;
            case DeltasMessage.DELTAS_UI:
                //Check if there is any synchronized ui ...if so apply baselines to them.
                break;
            default:
                //Unknown package.
        }
    }

    protected void updateContainment(UpdateContainmentMessage msg) {
        //Update containment settings.
    }

    protected void sceneCreateObjectByCrc(SceneCreateObjectByCrc msg) {
        //Starts initializing an object
    }

    protected void endBaselines(SceneEndBaselines msg) {
        //Marks the object as initialized
    }

    private void snapshotSuccess(SaveSnapshotSuccess msg) {
        LOGGER.trace("Snapshot saved successfully.");
    }

    private void snapshotFailure(SaveSnapshotFailure msg) {
        LOGGER.error("Snapshot failed to save.", msg.cause());
    }

}
