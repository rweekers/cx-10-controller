package org.cyanotic.cx10.io.controls;

import org.cyanotic.cx10.model.Command;
import org.cyanotic.cx10.net.CommandConnection;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class CommandDispatcher extends Thread implements Controller.CommandListener {

    private final Controller controller;
    private final CommandConnection dataConnection;
    private Command lastCommand;

    public CommandDispatcher(Controller controller, CommandConnection dataConnection) {
        this.controller = controller;
        this.dataConnection = dataConnection;
        lastCommand = new Command();
    }

    @Override
    public void interrupt() {
        controller.setListener(null);
        controller.stop();
        super.interrupt();
    }

    @Override
    public void run() {
        controller.setListener(this);
        controller.start();

        while (!isInterrupted()) {
            dataConnection.sendCommand(lastCommand);
            hold();
        }
    }

    private void hold() {
        try {
            sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void onCommandReceived(Command command) {
        if (command == null) {
            lastCommand = new Command();
        } else {
            lastCommand = command;
        }
    }
}
