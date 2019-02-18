package io.bacta.soe.event;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Address;
import akka.cluster.Cluster;
import akka.cluster.ClusterEvent;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class PublisherActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), PublisherActor.class);

    private final ApplicationEventPublisher publisher;
    private final Cluster cluster = Cluster.get(getContext().getSystem());
    private final List<ActorRef> members;

    @Inject
    public PublisherActor(final ApplicationEventPublisher publisher) {
        this.publisher = publisher;
        this.members = new ArrayList<>();
    }

    @Override
    public void preStart() throws Exception {

        log.info("Publisher Actor starting");
        cluster.subscribe(getSelf(),
                ClusterEvent.initialStateAsEvents(),
                ClusterEvent.MemberEvent.class,
                ClusterEvent.UnreachableMember.class);

        super.preStart();
    }

//    @EventListener
//    public void handleBactaEvents(BactaEvent event) {
//        PublisherEvent publisherEvent = new PublisherEvent(event);
//        members.forEach(member ->  member.tell(publisherEvent, getSelf()));
//    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ClusterEvent.MemberUp.class, this::memberUp)
                .match(ClusterEvent.UnreachableMember.class, this::memberDown)
                .match(PublisherEvent.class, this::handlePublishedEvent)
                .build();
    }

    private void memberDown(ClusterEvent.UnreachableMember memberDown) {
        Address address = memberDown.member().address();
        //TODO: Finish addressing other nodes
    }

    private void memberUp(ClusterEvent.MemberUp memberUp) {
        Address address = memberUp.member().address();
        //TODO: Finish addressing other nodes
    }

    private void handlePublishedEvent(PublisherEvent event) {
        BactaEvent bactaEvent = event.getEvent();
        publisher.publishEvent(bactaEvent);
    }
}
