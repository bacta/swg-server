package bacta.io.soe.network.controller;

import bacta.io.soe.network.message.SoeMessageType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface SoeController {

	SoeMessageType[] handles();
}
