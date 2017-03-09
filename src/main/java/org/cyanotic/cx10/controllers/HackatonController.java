package org.cyanotic.cx10.controllers;
import java.util.HashMap;
import java.util.Map;

import org.cyanotic.cx10.api.Command;
import org.cyanotic.cx10.api.Controller;

public class HackatonController implements Controller {

    private static final Command TAKEOFF_COMMAND = new Command(0, 0, 0, 0, true, false);
    private static final Command LAND_COMMAND = new Command(0, 0, 0, 0, false, true);
    private static final Command IDLE_COMMAND = new Command(0, 0, 0, 0, false, false);
    private static final Command TURN_RIGHT_COMMAND = new Command(0, 127, 0, 0, false, false);
    private static final Command TURN_LEFT_COMMAND = new Command(0, -64, 0, 0, false, false);
    private static final Command FORWARD_COMMAND = new Command(64, 0, 0, 0, false, false);
    private static final Command BACKWARD_COMMAND = new Command(-64, 0, 0, 0, false, false);

    private boolean takenOff = false;

    private int teller = 0;

    private static int OFFSET = 150;

    private Command currentCommand = TAKEOFF_COMMAND;

    private static Map<Integer, Command> scenario = new HashMap<>();

    static {
        scenario.put(1, IDLE_COMMAND);
        scenario.put(50, TURN_RIGHT_COMMAND);
        scenario.put(75, TURN_LEFT_COMMAND);
        scenario.put(100, FORWARD_COMMAND);
        scenario.put(125, BACKWARD_COMMAND);
        scenario.put(150, LAND_COMMAND);
    }
    @Override
    public Command getCommand() {
        int nettoTeller = getNettoTeller();
        teller++;
        if (scenario.containsKey(nettoTeller)) {
            Command command = scenario.get(nettoTeller);
            System.out.println("altering command " + command.toString() + " on teller " + getNettoTeller());
            currentCommand = command;
        }
        return currentCommand;
    }

    private int getNettoTeller() {
        return teller - OFFSET;
    }

    @Override
    public void close() {
        // do nothing
    }
}
