/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.bacta.service.scheduler;

import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by kburkhardt on 2/22/14.
 */
@Component
public class TaskSchedulerService implements SchedulerService {

    private final ScheduledExecutorService executor;

    @Inject
    public TaskSchedulerService() {
        //TODO: What should the default really be?
        executor = Executors.newScheduledThreadPool(4);
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
