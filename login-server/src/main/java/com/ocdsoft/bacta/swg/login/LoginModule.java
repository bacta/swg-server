package com.ocdsoft.bacta.swg.login;

import com.google.inject.AbstractModule;
import com.ocdsoft.bacta.soe.protocol.network.ServerState;
import com.ocdsoft.bacta.soe.protocol.network.io.udp.SoeNetworkConfiguration;
import com.ocdsoft.bacta.soe.protocol.service.OutgoingConnectionService;

public class LoginModule extends AbstractModule {

	@Override
	protected void configure() {

		bind(ServerState.class).to(LoginServerState.class);
		bind(SoeNetworkConfiguration.class).to(LoginNetworkConfiguration.class);
		bind(OutgoingConnectionService.class).to(LoginServerApplication.LoginOutgoingConnectionService.class);

	}

}
