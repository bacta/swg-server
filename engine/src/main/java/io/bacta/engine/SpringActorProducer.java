package io.bacta.engine;

import akka.actor.Actor;
import akka.actor.IndirectActorProducer;
import org.springframework.context.ApplicationContext;

public class SpringActorProducer implements IndirectActorProducer {

    private final ApplicationContext applicationContext;
    private final String actorBeanName;
    private final Object[] arguments;

    public SpringActorProducer(ApplicationContext applicationContext,
                               String actorBeanName,
                               Object... arguments) {
        this.applicationContext = applicationContext;
        this.actorBeanName = actorBeanName;
        this.arguments = arguments;
    }

    @Override
    public Actor produce() {
        //TODO: Not sure if this is needed. Test...
        if (arguments != null) {
            return (Actor) applicationContext.getBean(actorBeanName, arguments);
        } else {
            return (Actor) applicationContext.getBean(actorBeanName);
        }
    }

    @Override
    public Class<? extends Actor> actorClass() {
        return (Class<? extends Actor>) applicationContext.getType(actorBeanName);
    }
}