package org.cyanotic.cx10;

import org.cyanotic.cx10.controllers.FlyInACircle;
import org.cyanotic.cx10.framelisteners.VideoRecorder;

import java.util.concurrent.Executors;

/**
 * Created by gerard on 5-3-17.
 */
public class FlyInACircleWithRecorderLauncher {
    public static void main(String[] args) throws Exception {
        new CX10(Executors.newScheduledThreadPool(3), new FlyInACircle(), new VideoRecorder());
    }
}
