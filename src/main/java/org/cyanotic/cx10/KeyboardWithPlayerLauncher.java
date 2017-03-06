package org.cyanotic.cx10;

import org.cyanotic.cx10.controllers.Keyboard;
import org.cyanotic.cx10.framelisteners.SwingVideoPlayer;

/**
 * Created by gerard on 5-3-17.
 */
public class KeyboardWithPlayerLauncher {
    public static void main(String[] args) throws Exception {
        new CX10(new Keyboard(), new SwingVideoPlayer());
    }
}
