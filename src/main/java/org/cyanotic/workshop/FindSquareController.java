package org.cyanotic.workshop;

import java.io.IOException;

import org.cyanotic.cx10.api.Command;
import org.cyanotic.cx10.api.Controller;

public class FindSquareController implements Controller {

    private static final Command TAKEOFF_COMMAND = new Command(0, 0, 0, 0, true, false);
    private static final Command TURN_COMMAND = new Command(0, 0, 0, 2, false, false);
    private boolean initialized = false;

    @Override
    public Command getCommand() {
        if (!initialized) {
            initialized = true;
            return TAKEOFF_COMMAND;
        }
        return TURN_COMMAND;
    }

    @Override
    public void close() throws IOException {
        // do nothing
    }
}
