package com.ocdsoft.bacta.soe.service;

import java.util.Random;

/**
 * Created by kyle on 7/2/2017.
 */
public class RandomIntSessionKeyService implements SessionKeyService {
    private final Random random = new Random();

    @Override
    public int getNextKey() {
        return random.nextInt();
    }

}
