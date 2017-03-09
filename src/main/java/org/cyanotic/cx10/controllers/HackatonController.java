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
    private static final int MAX_FLIGHT_TIME = 250;

    private boolean takenOff = false;

    private boolean readyForStart = false;
    private int teller = 0;

    private static int OFFSET = 150;

    private Command currentCommand = TAKEOFF_COMMAND;

    private static Map<Integer, Command> scenario = new HashMap<>();

    private int index = 0;
    public HackatonController() {
//        addStep(TAKEOFF_COMMAND, 0);
//        addStep(TURN_RIGHT_COMMAND, 25);
//        addStep(IDLE_COMMAND, 25);
//        addStep(TURN_LEFT_COMMAND, 25);
//        addStep(IDLE_COMMAND, 25);
//        addStep(FORWARD_COMMAND, 25);
//        addStep(IDLE_COMMAND, 25);
//        addStep(BACKWARD_COMMAND, 25);
//        addStep(LAND_COMMAND, 1);
    }

    private void addStep(Command command, int duur) {
        index = index + duur;
        scenario.put(index, command);
    }

    @Override
    public Command getCommand() {
        teller++;
        if (!takenOff) {
            System.out.println("taking off!");
            takenOff = true;
            return TAKEOFF_COMMAND;
        } else if (teller > MAX_FLIGHT_TIME) {
            return LAND_COMMAND;
        } else if (currentCommand != null) {
            return currentCommand;
        }
        return IDLE_COMMAND;

//        if (readyForStart && !takenOff) {
//            takenOff = true;
//            System.out.println("taken off!");
//            return TAKEOFF_COMMAND;
//        } else if (teller  > 100) {
//            return LAND_COMMAND;
//        } else {
//            teller++;
//            return currentCommand != null ? currentCommand : IDLE_COMMAND;
//        }
    }

    private int getNettoTeller() {
        return teller - OFFSET;
    }

    @Override
    public void close() {
        // do nothing
    }

    public void onReceiveImageData(opencv_core.KeyPointVector keyPointVector) {
        if (keyPointVector == null || keyPointVector.size() == 0) {
            currentCommand = TURN_RIGHT_COMMAND;
        } else {
            currentCommand = IDLE_COMMAND;
        }
    }

}
