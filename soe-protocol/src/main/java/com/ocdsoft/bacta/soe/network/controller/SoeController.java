package com.ocdsoft.bacta.soe.network.controller;

import com.ocdsoft.bacta.soe.network.message.SoeMessageType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SoeController {

	SoeMessageType[] handles();
}
