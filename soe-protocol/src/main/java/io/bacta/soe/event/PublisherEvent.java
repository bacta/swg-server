package io.bacta.soe.event;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PublisherEvent<T extends BactaEvent> {
    final T event;
}
