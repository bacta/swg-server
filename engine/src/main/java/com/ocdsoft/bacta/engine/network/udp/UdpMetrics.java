package com.ocdsoft.bacta.engine.network.udp;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import lombok.Getter;

/**
 * Created by kyle on 6/30/2017.
 */
public class UdpMetrics {

    private final MetricRegistry metricRegistry;
    private final String namePrefix;
    private final Counter sentMessages;
    private final Counter channelsRegistered;
    private final Counter receivedMessages;

    private final TIntObjectMap<Counter> counterMap;

    public UdpMetrics(final MetricRegistry metricRegistry, final String namePrefix) {
        this.metricRegistry = metricRegistry;
        this.namePrefix = namePrefix;
        counterMap = new TIntObjectHashMap<>();
        sentMessages = metricRegistry.counter(namePrefix + ".messages.sent");
        channelsRegistered = metricRegistry.counter(namePrefix + ".channels.registered");
        this.receivedMessages = metricRegistry.counter(namePrefix + ".messages.received");
    }

    public void registerChannel(int channel) {
        channelsRegistered.inc();
        counterMap.put(channel, metricRegistry.counter(namePrefix + ".messages.sent." + channel));
    }

    public void sendMessage(int channel) {
        sentMessages.inc();
        Counter counter = counterMap.get(channel);
        if(counter != null) {
            counter.inc();
        }
    }

    public void receiveMessage() {
        receivedMessages.inc();
    }
}
