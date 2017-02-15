package org.cyanotic.cx10.io.controls;

import org.cyanotic.cx10.model.Command;

public abstract class AbstractController implements Controller {

    private CommandListener commandListener;

    @Override
    public void start() {
        // Send a takeoff command
        Command takeoffCommand = new Command();
        takeoffCommand.setTakeOff(true);
        sendCommand(takeoffCommand);
    }

    @Override
    public void stop() {
        // Send a land command to ensure that the drone stops moving and lands safely
        Command landCommand = new Command();
        landCommand.setLand(true);
        sendCommand(landCommand);
    }

    protected void sendCommand(Command command) {
        if (commandListener != null) {
            commandListener.onCommandReceived(command);
        }
    }

    @Override
    public boolean isAvailable() {
        return false;
    }

    @Override
    public void setListener(CommandListener controlListener) {
        this.commandListener = controlListener;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }
}
