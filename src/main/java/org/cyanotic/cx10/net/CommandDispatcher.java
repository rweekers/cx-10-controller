package org.cyanotic.cx10.net;

import org.cyanotic.cx10.api.Controller;
import org.cyanotic.cx10.utils.ExecutorUtils;

import java.util.concurrent.ScheduledFuture;

public class CommandDispatcher implements AutoCloseable {
    private final CommandConnection commandConnection;
    private final Controller controller;
    private final ScheduledFuture<?> future;

    public CommandDispatcher(CommandConnection commandConnection, Controller controller) {
        this.commandConnection = commandConnection;
        this.controller = controller;
        this.future = ExecutorUtils.scheduleControllerDispatcher(this::sendCommand);
    }

    @Override
    public void close() throws Exception {
        future.cancel(false);
        commandConnection.close();
        controller.close();
    }

    private void sendCommand() {
        commandConnection.sendCommand(controller.getCommand());
    }
}
