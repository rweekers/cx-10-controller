package org.cyanotic.cx10.team2;

import org.cyanotic.cx10.api.Command;
import org.cyanotic.cx10.api.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by gerard on 9-3-17.
 */
public class ProcessorController implements Controller {

    final static Logger LOGGER = LoggerFactory.getLogger(ProcessorController.class);

    private static final int YAW_CORRECTION_VALUE = 50;
    private static final int THROTTLE_CORRECTION_VALUE = 50;
    private static final int PITCH_CORRECTION_VALUE = 50;

    private static final Command TAKEOFF_COMMAND = new Command(0, 0, 0, 0, true, false);
    private static final Command LAND_COMMAND = new Command(0, 0, 0, 0, false, true);
    private static final Command IDLE_COMMAND = new Command(0, 0, 0, 0, false, false);

    private final Color captureColor;
    private final Color landColor;

    private boolean initialized = false;
    private boolean captured = false;
    private boolean hasLanded = false;

    private final Processor processor;

    private Command command = new Command(0, 0, 0, 0, false, false);

    public ProcessorController(Processor processor, Color captureColor, Color landColor) {
        this.processor = processor;
        this.captureColor = captureColor;
        this.landColor = landColor;

        this.processor.setColor(this.captureColor);
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public Command getCommand() {
        // not init
        if (!initialized) {
            LOGGER.info("INITIALIZING...");

            initialized = true;

            return TAKEOFF_COMMAND;
        }

        if (captured && hasLanded) {
            LOGGER.info("NOTHING TO DO...");

            return IDLE_COMMAND;
        }

        if (!searchCompleted()) {
            LOGGER.info("SEARCHING...");

            return createSearchCommand();
        }

        if (!captured) {
            LOGGER.info("CAPTURE!");
            playSound("shutter.wav");

            processor.capture();
            processor.setColor(landColor);

            captured = true;

            return IDLE_COMMAND;
        }

        if (!hasLanded) {
            LOGGER.info("LANDING!");

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

        // temp code
//        if (captured) {
//            return false;
//        }

        return processor.isReadyForCapture();

        //return Math.abs(delta.getX()) == 50 && Math.abs(delta.getY()) == 50 && Math.abs(delta.getScale()) == 50;

        // todo:
        //return Math.abs(delta.getX()) < 5 && Math.abs(delta.getY()) < 5 && Math.abs(delta.getScale()) < 5;
    }

    private void playSound(String fileName) {
        try {
            File soundFile = new File(getClass().getClassLoader().getResource(fileName).getFile());

            AudioInputStream stream;
            AudioFormat format;
            DataLine.Info info;
            Clip clip;

            stream = AudioSystem.getAudioInputStream(soundFile);
            format = stream.getFormat();
            info = new DataLine.Info(Clip.class, format);
            clip = (Clip) AudioSystem.getLine(info);
            clip.open(stream);
            clip.start();
        }
        catch (Exception e) {
            //whatevers
        }
    }
}
