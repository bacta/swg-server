package bacta.io.service.scheduler;

import java.util.concurrent.TimeUnit;

/**
 * Created by kburkhardt on 2/22/14.
 */
public interface SchedulerService  {
    void execute(Task task);
    void schedule(Task task, long delay, TimeUnit unit);

    void scheduleAtFixedRate(Task task, int initialDelay, int period, TimeUnit unit);
}
