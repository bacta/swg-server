package com.ocdsoft.bacta.engine.network.udp;

import com.codahale.metrics.Counter;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;

/**
 * Created by kyle on 6/30/2017.
 */
public class UdpReceiverMetrics {

    private final Counter counter;
    private final String namePrefix;

    public UdpReceiverMetrics(final MetricRegistry metricRegistry, final String namePrefix) {
        this.counter = metricRegistry.counter(namePrefix + ".messages.received");
        this.namePrefix = namePrefix;
    }

    public void inc() {
        counter.inc();
    }

    public long getMessageCount() {
        return counter.getCount();
    }
}
