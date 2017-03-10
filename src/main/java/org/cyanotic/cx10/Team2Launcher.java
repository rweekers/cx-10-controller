package org.cyanotic.cx10;

import org.cyanotic.cx10.framelisteners.CompositeFrameListener;
import org.cyanotic.cx10.framelisteners.SwingVideoPlayer;
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
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(4);
        VideoProcessor videoProcessor = new VideoProcessor();
        SwingVideoPlayer videoPlayer = new SwingVideoPlayer();
        CompositeFrameListener frameListener = new CompositeFrameListener(videoPlayer, videoProcessor);
        ProcessorController controller = new ProcessorController(videoProcessor);
        new CX10(executor, controller, frameListener);
    }
}
