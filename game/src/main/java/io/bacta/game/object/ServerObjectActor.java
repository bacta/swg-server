package io.bacta.game.object;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.japi.pf.ReceiveBuilder;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import io.bacta.game.message.*;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;

@Getter
public abstract class ServerObjectActor<T extends ServerObject> extends AbstractActor {
    /**
     * The underlying object that this actor manages. When this object mutates, the actor
     * is configured to send out deltas to nearby objects. It will also periodically send
     * serialize requests to the database.
     */
    protected final T object;
    /**
     * Other actors who are interested in receiving baselines and deltas from the object this actor controls.
     */
    protected final Set<ActorRef> listeners;
    /**
     * Stores a local cache of objects that this object is observing.
     */
    protected TLongObjectMap<ServerObject> localObjectCache;

    protected ServerObjectActor(T serverObject) {
        this.object = serverObject;
        this.listeners = new HashSet<>();
        this.localObjectCache = new TLongObjectHashMap<>(100);
    }

    @Override
    public final Receive createReceive() {
        return this.appendReceiveHandlers(receiveBuilder())
                .build();
    }

    protected ReceiveBuilder appendReceiveHandlers(ReceiveBuilder receiveBuilder) {
        return receiveBuilder
                .match(SceneCreateObjectByCrc.class, this::sceneCreateObjectByCrc)
                .match(UpdateContainmentMessage.class, this::updateContainment)
                .match(BaselinesMessage.class, this::applyBaselines)
                .match(SceneEndBaselines.class, this::endBaselines)
                .match(DeltasMessage.class, this::applyDeltas);

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

}
