package org.cyanotic.cx10.controllers;
import org.cyanotic.cx10.api.Command;
import org.cyanotic.cx10.api.Controller;

public class HackatonController implements Controller {

    private static final Command TAKEOFF_COMMAND = new Command(0, 0, 0, 0, true, false);
    private static final Command LAND_COMMAND = new Command(0, 0, 0, 0, false, true);
    private static final Command IDLE_COMMAND = new Command(0, 0, 0, 0, false, false);

    private boolean takenOff;

    private int teller = 0;
    @Override public Command getCommand() {
        if (!takenOff) {
            takenOff = true;
            return TAKEOFF_COMMAND;
        } else if (teller < 50){
            teller++;
            return IDLE_COMMAND;
        } else {
            return LAND_COMMAND;

        }
    }

    @Override public void close() {
        //do nothing
    }
}
