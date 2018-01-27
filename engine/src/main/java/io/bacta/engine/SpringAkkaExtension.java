package io.bacta.engine;

import akka.actor.Extension;
import akka.actor.Props;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.beans.Introspector;

@Component
public class SpringAkkaExtension implements Extension {

    private ApplicationContext applicationContext;

    public void initialize(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public Props props(String actorBeanName) {
        return Props.create(SpringActorProducer.class,
                applicationContext, actorBeanName);
    }

    public Props props(Class clazz) {
        return Props.create(SpringActorProducer.class,
                applicationContext, Introspector.decapitalize(clazz.getSimpleName()));
    }
}
