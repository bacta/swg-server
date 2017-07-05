/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package bacta.io.network.udp;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

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
