package com.ocdsoft.bacta.swg.protocol.controller;

import com.ocdsoft.bacta.swg.protocol.message.UdpPacketType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SoeController {

	UdpPacketType[] handles();
}
