package org.cyanotic.cx10.controllers;

import org.cyanotic.cx10.api.Command;
import org.cyanotic.cx10.api.Controller;

/**
 * Created by gerard on 15-2-17.
 */
public class FlyInACircle implements Controller {

    private static final Command TAKEOFF_COMMAND = new Command(0, 0, 0, 0, true, false, "takeoff");
    private static final Command TURN_COMMAND = new Command(0, 50, 0, 1, false, false, "crice");
    private boolean initialized = false;

    @Override
    public void close() {
        // do nothing
    }

    @Override
    public Command getCommand() {
        if (!initialized) {
            initialized = true;
            return TAKEOFF_COMMAND;
        }
        return TURN_COMMAND;
    }
}
