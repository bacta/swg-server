package io.bacta.soe.network.controller;

/**
 * Created by kyle on 7/7/2017.
 */
public @interface ControllerLifecycle {
    String[] stagesLoaded() default ControllerLifecycleStage.RUNNING;
}
