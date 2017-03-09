package org.cyanotic.cx10.team2;

import org.cyanotic.cx10.api.Command;
import org.cyanotic.cx10.api.Controller;

import java.io.IOException;

/**
 * Created by gerard on 9-3-17.
 */
public class ProcessorController implements Controller {

    private static final int YAW_CORRECTION_VALUE = 50;
    private static final int THROTTLE_CORRECTION_VALUE = 50;
    private static final int PITCH_CORRECTION_VALUE = 50;

    private static final Command TAKEOFF_COMMAND = new Command(0, 0, 0, 0, true, false);
    private static final Command LAND_COMMAND = new Command(0, 0, 0, 0, false, true);
    private static final Command IDLE_COMMAND = new Command(0, 0, 0, 0, false, false);

    private static final String CAPTURE_COLOR = "red";
    private static final String LAND_COLOR = "blue";

    private boolean initialized = false;
    private boolean captured = false;
    private boolean hasLanded = false;

    private final Processor processor;
    private final Capturer capturer;

    private Command command = new Command(0, 0, 0, 0, false, false);

    public ProcessorController(Processor processor, Capturer capturer) {
        this.processor = processor;
        this.capturer = capturer;

        this.processor.setColor(CAPTURE_COLOR);
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public Command getCommand() {

        // not init
        if (!initialized) {
            initialized = true;

            return TAKEOFF_COMMAND;
        }

        if (captured && hasLanded) {
            return IDLE_COMMAND;
        }

        if (!searchCompleted()) {
            return createSearchCommand();
        }

        if (!captured) {
            capturer.capture();
            processor.setColor(LAND_COLOR);
            captured = true;

            return IDLE_COMMAND;
        }

        if (!hasLanded) {
            hasLanded = true;

            return LAND_COMMAND;
        }

        // don't know that to do next
        return IDLE_COMMAND;
    }

    private Command createSearchCommand() {
        Delta delta = processor.getDelta();

        if (delta.getX() < 0) {
            // rotate left
            command.setYaw(-YAW_CORRECTION_VALUE);
        } else if (delta.getX() > 0) {
            // rotate right
            command.setYaw(YAW_CORRECTION_VALUE);
        }

        // if no image found, just rotate
        if (delta.getX() == 0 && delta.getY() == 0) {
            command.setYaw(YAW_CORRECTION_VALUE);
        }

        if (delta.getY() < 0) {
            // go lower
            command.setThrottle(-THROTTLE_CORRECTION_VALUE);
        } else if (delta.getY() > 0) {
            // go higher
            command.setThrottle(THROTTLE_CORRECTION_VALUE + 20);
        }

        // if need to come closer
        if (delta.getScale() < 0 && Math.abs(delta.getX()) < 5) {
            // need to come closer
            command.setPitch(PITCH_CORRECTION_VALUE);

        } else if (delta.getScale() > 0) {
            // need to go backwards
            command.setPitch(-PITCH_CORRECTION_VALUE);
        }

        return command;
    }

    private boolean searchCompleted() {
        Delta delta = processor.getDelta();

        return false;
        //return Math.abs(delta.getX()) < 5 && Math.abs(delta.getY()) < 5 && Math.abs(delta.getScale()) < 5;
    }
}
