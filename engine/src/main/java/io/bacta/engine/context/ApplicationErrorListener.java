package io.bacta.engine.context;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;

@Slf4j
public class ApplicationErrorListener implements ApplicationListener<ApplicationContextEvent> {

    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        LOGGER.error("Context Event: {}", event.getSource());
    }
}
