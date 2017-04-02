package com.ocdsoft.bacta.engine.network.controller;


public interface MessageController<C, D> extends Controller { //<SoeUdpClient, D> {
	void handleIncoming(C client, D data) throws Exception;
}
