package org.cyanotic.cx10;

import org.cyanotic.cx10.controllers.Keyboard;
import org.cyanotic.cx10.framelisteners.FindColor;
import org.cyanotic.cx10.framelisteners.SwingVideoPlayer;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by gerard on 5-3-17.
 */
public class KeyboardWithPlayerLauncher {
    public static void main(String[] args) throws Exception {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
        new CX10(executor, new Keyboard(), new FindColor(executor));
    }
}
