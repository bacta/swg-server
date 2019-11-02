package io.bacta.soe.event;

import io.bacta.soe.context.SoeSessionContext;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class DisconnectEvent implements BactaEvent {
    private SoeSessionContext context;
}
