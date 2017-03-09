package org.cyanotic.cx10;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import nl.craftsmen.cx10.FlyToBlueKeyboard;
import nl.craftsmen.cx10.measure.MeasuredValuesCache;
import org.cyanotic.cx10.framelisteners.SwingVideoPlayer;

public class KeyboardWithPlayerLauncher {
    public static void main(String[] args) throws Exception {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
        MeasuredValuesCache measuredValuesCache = new MeasuredValuesCache();
        measuredValuesCache.x = 325;
        measuredValuesCache.y = 288;
        measuredValuesCache.hoogteL = 250;
        measuredValuesCache.hoogteR = 250;
        measuredValuesCache.breedte = 100;
        new CX10(executor, new FlyToBlueKeyboard(measuredValuesCache), new SwingVideoPlayer(executor));
    }
}
