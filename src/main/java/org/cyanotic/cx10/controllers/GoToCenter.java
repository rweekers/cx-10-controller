package org.cyanotic.cx10.controllers;

import org.bytedeco.javacpp.opencv_core;
import org.cyanotic.cx10.api.Command;
import org.cyanotic.cx10.api.Controller;
import org.cyanotic.cx10.framelisteners.FindColor;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by gerard on 15-2-17.
 */
public class GoToCenter implements Controller {

    private static final Command TAKEOFF_COMMAND = new Command(0, 0, 0, 0, true, false);
    private static final Command LAND_COMMAND = new Command(0, 0, 0, 0, false, true);

    private static final Command NOTHING = new Command(0, 0, 0, 0, false, false);

    private static final Command TURN_COMMAND = new Command(0, 50, 0, 1, false, false);

    private final FindColor finder;

    private boolean initialized = false;

    private int timer = 500;

    private final static int SAMPLE_RATE = 10;
    private int thingy = 0;

    public GoToCenter(FindColor finder) {
        this.finder = finder;
    }

    @Override
    public void close() {
        // do nothing
    }

    @Override
    public Command getCommand() {
        opencv_core.CvPoint toDistance = finder.getDistanceToCenter();

        if (toDistance == null) {
            return NOTHING;
        }

        if (!initialized) {
            initialized = true;
            return TAKEOFF_COMMAND;
        }

        if (thingy++ <= 10) {
            System.out.println("NOTHING");
            return NOTHING;
        }

        if (timer-- <= 0) {
            System.out.println("LAND");
            return LAND_COMMAND;
        }

        System.out.println("Timer: " + timer + ". Distance: " + toDistance);

        return new Command(0, 0, toDistance.x() > 0 ? 20 : -20, toDistance.y() > 0 ? -25 : 0, false, false);
        //}

    }
}
