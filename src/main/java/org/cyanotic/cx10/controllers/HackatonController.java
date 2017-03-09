package org.cyanotic.cx10.controllers;
import java.util.HashMap;
import java.util.Map;

import org.bytedeco.javacpp.opencv_core;
import org.cyanotic.cx10.api.Command;
import org.cyanotic.cx10.api.Controller;

public class HackatonController implements Controller {

    private static final Command TAKEOFF_COMMAND = new Command(0, 0, 0, 0, true, false, "takeoff");
    private static final Command LAND_COMMAND = new Command(0, 0, 0, 0, false, true, "land");
    private static final Command IDLE_COMMAND = new Command(0, 0, 0, 0, true, false, "idle");
    private static final Command TURN_RIGHT_COMMAND = new Command(0, 32, 0, 0, false, false, "turnright");
    private static final Command TURN_LEFT_COMMAND = new Command(0, -64, 0, 0, false, false, "turnleft");
    private static final Command FORWARD_COMMAND = new Command(64, 0, 0, 0, false, false, "forward");
    private static final Command BACKWARD_COMMAND = new Command(-64, 0, 0, 0, false, false, "backward");
    private static final int DECELERATION = 1;
    private final Command UP_COMMAND = new Command(0, 0, 0, 30, false, false, "backward");
    private static final int MAX_FLIGHT_TIME = 250;

    private boolean takenOff = false;

    private long currentFoundSize = 0;
    private int teller = 0;
    private int throttle = 5;

    private Command currentCommand;

    @Override
    public Command getCommand() {
        teller++;
        if (!takenOff) {
            System.out.println("taking off!");
            takenOff = true;
            return TAKEOFF_COMMAND;
        } else if (teller > MAX_FLIGHT_TIME) {
            System.out.println("giving up...");
            return LAND_COMMAND;
        } else if (currentCommand != null) {
            System.out.println("current command: " + currentCommand);
            return currentCommand;
        }
        System.out.println("idle for now");
        return IDLE_COMMAND;

    }

    @Override
    public void close() {
        // do nothing
    }

    public void onReceiveImageData(opencv_core.MatVector matVector) {
        System.out.println("image data received!");
        if (matVector == null || matVector.size() == 0) {
            System.out.println("going up!");
            currentCommand = createUpCommand();
        } else if (currentFoundSize < matVector.size()) {
            currentFoundSize = matVector.size();
            currentCommand = createUpCommand();
            System.out.println("getting closer...");
            afremmen();
        } else if (currentFoundSize >= matVector.size()) {
            System.out.println("found it!");
            //takepicture
            currentCommand = LAND_COMMAND;

        }
    }

    private void afremmen() {
        throttle = throttle - DECELERATION;
        if (throttle < 0) {
            throttle = 0;
        }
    }

    private Command createUpCommand() {
        return new Command(0, 0, 0, throttle, false, false, "upCommand");

    }

}
