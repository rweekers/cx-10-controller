package org.cyanotic.cx10.utils;

import java.util.concurrent.*;

/**
 * Created by gerard on 4-3-17.
 */
public class ExecutorUtils {

    private static final ThreadFactory THREAD_FACTORY = r -> new Thread(r, r.getClass().getName());
    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(2, THREAD_FACTORY);

    public static ScheduledFuture<?> scheduleHeartbeat(Runnable runnable) {
        return EXECUTOR.scheduleWithFixedDelay(runnable, 0, 5, TimeUnit.SECONDS);
    }

    public static ScheduledFuture<?> scheduleControllerDispatcher(Runnable runnable) {
        return EXECUTOR.scheduleWithFixedDelay(runnable, 0, 50, TimeUnit.MILLISECONDS);
    }

    public static ScheduledFuture<?> schedulePrintVideoStats(Runnable runnable) {
        return EXECUTOR.scheduleWithFixedDelay(runnable, 0, 1, TimeUnit.SECONDS);
    }

    public static Thread scheduleVideoDecoder(Runnable runnable) {
        return THREAD_FACTORY.newThread(runnable);
    }

}
