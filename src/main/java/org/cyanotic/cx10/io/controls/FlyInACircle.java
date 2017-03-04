package org.cyanotic.cx10.io.controls;

import org.cyanotic.cx10.model.Command;

import java.util.concurrent.*;

/**
 * Created by gerard on 15-2-17.
 */
public class FlyInACircle extends AbstractController {

    private static final ScheduledExecutorService SCHEDULER = Executors.newSingleThreadScheduledExecutor();
    private static final Command COMMAND = new Command(0, 50, 0, 1);

    private ScheduledFuture future;

    @Override
    public void start() {
        super.start();
        future = SCHEDULER.scheduleAtFixedRate(this::sendNextCommand, 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public void stop() {
        future.cancel(false);
        super.stop();
    }

    private void sendNextCommand() {
        sendCommand(COMMAND);
    }
}
