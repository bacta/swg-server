package com.ocdsoft.bacta.swg.login;

import com.google.inject.AbstractModule;
import com.ocdsoft.bacta.soe.protocol.ServerState;
import com.ocdsoft.bacta.soe.protocol.io.udp.NetworkConfiguration;
import com.ocdsoft.bacta.soe.protocol.service.OutgoingConnectionService;

public class LoginModule extends AbstractModule {

	@Override
	protected void configure() {

		bind(ServerState.class).to(LoginServerState.class);
		bind(NetworkConfiguration.class).to(LoginNetworkConfiguration.class);
		bind(OutgoingConnectionService.class).to(Application.LoginOutgoingConnectionService.class);

	}

}
