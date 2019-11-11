package io.bacta.engine.utils;

import akka.actor.ActorRef;

public class SenderUtil {

    private SenderUtil() {}

    /**
     * This method determines if the sender is only a single level
     * below the initial user actor.  Used to filter messages from
     * lower actors to higher level actor
     *
     * Acceptable Path
     * ex: akka://GalaxyCluster/user/galaxyManager/loginDelegate
     *
     * Unacceptable Path
     * ex: akka://GalaxyCluster/user/galaxyManager/loginDelegate/child
     *
     * @param sender
     * @return if there are 3 or less path parameters
     */
    public static boolean isPrivileged(ActorRef sender) {
        return sender.path().elements().size() <= 3;
    }
}
