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

package io.bacta.soe.network.controller;

import io.bacta.soe.config.SoeNetworkConfiguration;
import io.bacta.soe.network.connection.IncomingMessageProcessor;
import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.network.dispatch.SoeMessageHandler;
import io.bacta.soe.network.message.SoeMessageType;
import io.bacta.soe.network.relay.GameNetworkMessageRelay;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.nio.ByteBuffer;

@Slf4j
@Component
@SoeController(handles = {
        SoeMessageType.cUdpPacketReliable1,
        SoeMessageType.cUdpPacketReliable2,
        SoeMessageType.cUdpPacketReliable3,
        SoeMessageType.cUdpPacketReliable4,
        SoeMessageType.cUdpPacketFragment1,
        SoeMessageType.cUdpPacketFragment2,
        SoeMessageType.cUdpPacketFragment3,
        SoeMessageType.cUdpPacketFragment4})
public class ReliableMessageController implements SoeMessageController {

    private final SoeNetworkConfiguration networkConfiguration;
    private SoeMessageHandler soeMessageHandler;

    @Inject
    public ReliableMessageController(final SoeNetworkConfiguration networkConfiguration) {
        this.networkConfiguration = networkConfiguration;
    }

    @Override
    public void setSoeHandler(final SoeMessageHandler soeMessageHandler) {
        this.soeMessageHandler = soeMessageHandler;
    }

    @Override
    public void handleIncoming(final byte zeroByte,
                               final SoeMessageType type,
                               final SoeUdpConnection connection,
                               final ByteBuffer buffer,
                               final GameNetworkMessageRelay processor) {

        IncomingMessageProcessor incomingMessageProcessor = connection.getIncomingMessageProcessor();

        long nextClientReliableStamp = incomingMessageProcessor.getReliableStamp();
        short reliableStamp = buffer.getShort();
        long reliableId = getReliableIncomingId(reliableStamp, nextClientReliableStamp);

        if (reliableId >= nextClientReliableStamp + networkConfiguration.getMaxInstandingPackets()) {
            return;        // if we do not have buffer space to hold onto this packet, then we simply must pretend like it was lost
        }

        ReliablePacketMode mode = ReliablePacketMode.values()[((type.getValue() - SoeMessageType.cUdpPacketReliable1.getValue()) / networkConfiguration.getReliableChannelCount())];
        
        if (reliableId >= nextClientReliableStamp) {

            // is this the packet we are waiting for
            if (nextClientReliableStamp == reliableId)  {
                // if so, process it immediately
                processPacket(mode, connection, buffer, processor);
                incomingMessageProcessor.incrementNextReliable();

                // process other packets that have arrived
                ByteBuffer pendingBuffer;
                while ((pendingBuffer = incomingMessageProcessor.nextReliable(nextClientReliableStamp)) != null) {

                    SoeMessageType nextType = SoeMessageType.values()[pendingBuffer.get(1)];
                    mode = ReliablePacketMode.values()[((nextType.getValue() - SoeMessageType.cUdpPacketReliable1.getValue()) / networkConfiguration.getReliableChannelCount())];

                    processPacket(mode, connection, pendingBuffer, processor);

                    incomingMessageProcessor.incrementNextReliable();
                }
            }
            // not the one we need next, but it is later than the one we need , so store it in our buffer until it's turn comes up
            else {
                incomingMessageProcessor.addReliable(reliableId, buffer);
            }
        }
        
        LOGGER.trace("{} Receiving Reliable Message Sequence {} {}", connection.getId(), reliableStamp, buffer.order());
        if (nextClientReliableStamp > reliableId) {
            connection.ackAllFromClient(reliableStamp);
        } else {
            connection.ackClient(reliableStamp);
        }
    }

    private void processPacket(final ReliablePacketMode mode, final SoeUdpConnection connection, final ByteBuffer buffer, final GameNetworkMessageRelay processor) {

        IncomingMessageProcessor incomingMessageProcessor = connection.getIncomingMessageProcessor();

        if(mode == ReliablePacketMode.cReliablePacketModeReliable) {

            soeMessageHandler.handleMessage(connection, buffer, processor);

        } else if (mode == ReliablePacketMode.cReliablePacketModeFragment) {

            ByteBuffer completeFragment = incomingMessageProcessor.addIncomingFragment(buffer);
            if(completeFragment != null) {
                soeMessageHandler.handleMessage(connection, buffer, processor);
            }
        }
    }

    private long getReliableIncomingId(int reliableStamp, long nextClientReliableStamp) {

        // since we can never have anywhere close to 65000 packets outstanding, we only need to
        // to send the low order word of the reliableId in the UdpPacketReliable and UdpPacketAck
        // packets, because we can reconstruct the full id from that, we just need to take
        // into account the wrap around issue.  We basically prepend the last-known
        // high-order word.  If we end up significantly below the head of our chain, then we
        // know we need to pick the entry 0x10000 higher.  If we fall significantly above
        // our previous high-end, then we know we need to go the other way.
        long reliableId = reliableStamp | (nextClientReliableStamp & (~(long)0xffff));
        if (reliableId < nextClientReliableStamp - networkConfiguration.getHardMaxOutstandingPackets())
            reliableId += 0x10000;
        if (reliableId > nextClientReliableStamp + networkConfiguration.getHardMaxOutstandingPackets())
            reliableId -= 0x10000;
        return(reliableId);
    }

    private enum ReliablePacketMode {
        cReliablePacketModeReliable,
        cReliablePacketModeFragment,
        cReliablePacketModeDelivered
    }
}