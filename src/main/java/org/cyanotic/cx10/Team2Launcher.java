package org.cyanotic.cx10;

import org.cyanotic.cx10.team2.ProcessorController;
import org.cyanotic.cx10.team2.VideoProcessor;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by gerard on 5-3-17.
 */
public class Team2Launcher {
    public static void main(String[] args) throws Exception {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
        VideoProcessor frameListener = new VideoProcessor(executor);
        ProcessorController controller = new ProcessorController(frameListener, Color.RED, Color.BLUE);
        new CX10(executor, controller, frameListener);
    }
}
