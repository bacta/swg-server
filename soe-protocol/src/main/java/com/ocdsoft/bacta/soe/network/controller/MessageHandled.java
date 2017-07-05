package com.ocdsoft.bacta.soe.network.controller;


import io.bacta.shared.GameNetworkMessage;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface MessageHandled {
    Class<? extends GameNetworkMessage>[] handles();
}