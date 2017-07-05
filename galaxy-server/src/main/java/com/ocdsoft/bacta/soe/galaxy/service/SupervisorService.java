package com.ocdsoft.bacta.soe.galaxy.service;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Created by kyle on 7/2/2017.
 */
@Component
public class SupervisorService implements ApplicationListener<ApplicationReadyEvent> {





    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        // Notify Login we are up and running
    }
}
