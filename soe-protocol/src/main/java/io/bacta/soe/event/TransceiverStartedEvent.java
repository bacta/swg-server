package io.bacta.soe.event;

import org.springframework.context.ApplicationEvent;

public class TransceiverStartedEvent extends ApplicationEvent {
    public TransceiverStartedEvent(Object source) {
        super(source);
    }
}
