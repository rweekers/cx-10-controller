package org.cyanotic.cx10.controllers;
import org.cyanotic.cx10.api.Command;
import org.cyanotic.cx10.api.Controller;

public class HackatonController implements Controller {

    private static final Command TAKEOFF_COMMAND = new Command(0, 0, 0, 0, true, false);
    private static final Command LAND_COMMAND = new Command(0, 0, 0, 0, false, true);
    private static final Command IDLE_COMMAND = new Command(0, 0, 0, 0, false, false);
    private static final Command TURN_RIGHT_COMMAND = new Command(0, 64, 0, 0, false, false);
    private static final Command TURN_LEFT_COMMAND = new Command(0, -64, 0, 0, false, false);
    private static final Command FORWARD_COMMAND = new Command(64, 0, 0, 64, false, false);
    private static final Command BACKWARD_COMMAND = new Command(-64, 0, 0, 64, false, false);

    private boolean takenOff = false;

    private int teller = 0;

    @Override public Command getCommand() {
        teller++;
        System.out.println("teller:" + teller);
        if (!takenOff) {
            takenOff = true;
            System.out.println("taking off");
            return TAKEOFF_COMMAND;
        } else if (teller < 50 || (teller > 60 && teller < 100) ){
            System.out.println("idle");
            return IDLE_COMMAND;
        } else if (teller >= 50 && teller <= 60) {
            System.out.println("turning right");
            return TURN_RIGHT_COMMAND;
        } else {
            System.out.println("landing");
            return LAND_COMMAND;
        }
    }

    @Override public void close() {
        //do nothing
    }
}
