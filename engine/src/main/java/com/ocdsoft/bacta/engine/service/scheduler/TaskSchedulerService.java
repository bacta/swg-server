package com.ocdsoft.bacta.engine.service.scheduler;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.engine.conf.BactaConfiguration;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by kburkhardt on 2/22/14.
 */
@Singleton
public class TaskSchedulerService implements SchedulerService {

    private final ScheduledExecutorService executor;

    @Inject
    public TaskSchedulerService(BactaConfiguration configuration) {
        //TODO: What should the default really be?
        executor = Executors.newScheduledThreadPool(configuration.getIntWithDefault("Bacta/Services/Scheduler", "ThreadCount", 4));
    }

    @Override
    public void execute(Task task) {
        executor.execute(task);
    }

    @Override
    public void schedule(Task task, long delay, TimeUnit unit) {
        Future<?> future = executor.schedule(task, delay, unit);
        task.setFuture(future);
    }

    @Override
    public void scheduleAtFixedRate(Task task, int initialDelay, int period, TimeUnit unit) {
        Future<?> future = executor.scheduleAtFixedRate(task, initialDelay, period, unit);
        task.setFuture(future);
    }
}
