package org.cyanotic.cx10;

import org.cyanotic.cx10.controllers.FlyInACircle;
import org.cyanotic.cx10.framelisteners.SwingVideoPlayer;

/**
 * Created by gerard on 5-3-17.
 */
public class FlyInACircleWithPlayerLauncher {
    public static void main(String[] args) throws Exception {
        new CX10(new FlyInACircle(), new SwingVideoPlayer());
    }
}
