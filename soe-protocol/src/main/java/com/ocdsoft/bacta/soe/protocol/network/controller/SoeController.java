package com.ocdsoft.bacta.soe.protocol.network.controller;

import com.ocdsoft.bacta.soe.protocol.network.message.UdpPacketType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SoeController {

	UdpPacketType[] handles();
}
