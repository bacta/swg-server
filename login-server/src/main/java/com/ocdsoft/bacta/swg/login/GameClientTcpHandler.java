package com.ocdsoft.bacta.swg.login;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by kyle on 5/21/2016.
 */
public final class GameClientTcpHandler extends ChannelInboundHandlerAdapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(GameClientTcpHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        LOGGER.trace("Heartbeat received");
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) {
        LOGGER.trace("Channel Active");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        LOGGER.trace("Channel Inactive");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        LOGGER.error("Disconnected from GameServer", cause);
    }
}
