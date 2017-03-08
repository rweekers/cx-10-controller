package org.cyanotic.cx10;

import org.cyanotic.cx10.controllers.Keyboard;
import org.cyanotic.cx10.framelisteners.VideoRecorder;

import java.util.concurrent.Executors;

/**
 * Created by gerard on 5-3-17.
 */
public class KeyboardWithRecorderLauncher {
    public static void main(String[] args) throws Exception {
        new CX10(Executors.newScheduledThreadPool(3), new Keyboard(), new VideoRecorder());
    }
}
