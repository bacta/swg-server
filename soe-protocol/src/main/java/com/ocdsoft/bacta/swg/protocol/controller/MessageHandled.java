package com.ocdsoft.bacta.swg.protocol.controller;

import com.ocdsoft.bacta.swg.protocol.ServerType;
import com.ocdsoft.bacta.swg.protocol.message.GameNetworkMessage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MessageHandled {
    Class<? extends GameNetworkMessage>[] handles();
    ServerType[] type() default ServerType.GAME;
}