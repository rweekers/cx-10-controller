package org.cyanotic.cx10.common;

import org.cyanotic.cx10.api.Command;
import org.cyanotic.cx10.api.Controller;
import org.cyanotic.cx10.net.CommandConnection;

public class CommandDispatcher implements Runnable {
    private final CommandConnection commandConnection;
    private final Controller controller;

    public CommandDispatcher(CommandConnection commandConnection, Controller controller) {
        this.commandConnection = commandConnection;
        this.controller = controller;
    }

    @Override
    public void run() {
        Command command = controller.getCommand();
        if (command != null) commandConnection.sendCommand(command);
    }
}
