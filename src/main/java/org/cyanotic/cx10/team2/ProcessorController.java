package org.cyanotic.cx10.team2;

import org.cyanotic.cx10.api.Command;
import org.cyanotic.cx10.api.Controller;

import java.awt.*;
import java.io.IOException;

/**
 * Created by gerard on 9-3-17.
 */
public class ProcessorController implements Controller {

    private static final int YAW_CORRECTION_VALUE = 60;
    private static final int THROTTLE_CORRECTION_VALUE = 60;
    private static final int PITCH_CORRECTION_VALUE = 80;

    private static final Command TAKEOFF_COMMAND = new Command(0, 0, 0, 0, true, false);

    private boolean initialized = false;

    private final Processor processor;

    private Command command = new Command(0, 0, 0, 0, false, false);

    public ProcessorController(Processor processor) {
        this.processor = processor;
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public Command getCommand() {
        if (!initialized) {
            initialized = true;
            return TAKEOFF_COMMAND;
        }

        Point delta = processor.getDelta();

        // todo: zet de delta om in een beweging
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
            command.setThrottle(THROTTLE_CORRECTION_VALUE);
        }

        // if need to come closer
        if (processor.getScale() < 0 && Math.abs(delta.getX()) < 5) {
            // need to come closer
            command.setPitch(PITCH_CORRECTION_VALUE);

        } else if (processor.getScale() > 0) {
            // need to go backwards
            command.setPitch(-PITCH_CORRECTION_VALUE);
        }

//        command.setLand(takeOffLandingProvider.shouldLand());
//        command.setTakeOff(takeOffLandingProvider.shouldTakeOff());

        return command;
    }
}
