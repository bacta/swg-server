package com.ocdsoft.bacta.swg.login;

import com.ocdsoft.bacta.swg.protocol.ServerState;
import com.ocdsoft.bacta.swg.protocol.io.udp.NetworkConfiguration;
import com.ocdsoft.bacta.swg.protocol.service.OutgoingConnectionService;

public class BaseLoginModule extends LoginModule {

	@Override
	protected void configure() {

		bind(ServerState.class).to(LoginServerState.class);
		bind(NetworkConfiguration.class).to(LoginNetworkConfiguration.class);
		bind(OutgoingConnectionService.class).to(Application.LoginOutgoingConnectionService.class);

	}

}
