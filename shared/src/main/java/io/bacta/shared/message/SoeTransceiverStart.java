package io.bacta.shared.message;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.net.InetAddress;

@Data
@AllArgsConstructor
public class SoeTransceiverStart {
    private final String name;
    private final InetAddress bindAddress;
    private int bindPort;
}
