package com.ocdsoft.bacta.swg.login.service;

import com.ocdsoft.bacta.soe.service.SessionKeyService;

import java.util.Random;

public class SWGSessionKeyService implements SessionKeyService {

	private final Random random = new Random();
	
	@Override
	public int getNextKey() {
		return random.nextInt();
	}

}
