package org.cyanotic.cx10;

import nl.craftsmen.cx10.measure.MeasuredValuesCache;
import org.cyanotic.cx10.controllers.FlyInACircle;
import org.cyanotic.cx10.controllers.Keyboard;
import org.cyanotic.cx10.framelisteners.SwingVideoPlayer;
import org.cyanotic.cx10.framelisteners.VideoRecorder;
import org.cyanotic.cx10.imagelisteners.RectangeDetector;
import org.cyanotic.cx10.patternrecognition.Square;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by Gerry on 9-3-2017.
 */
public class FlyWithDetectSquare {
    public static void main(String[] args) throws Exception {
        Square.counter = 0;
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);
        new CX10(executor, new Keyboard(), new RectangeDetector(executor, new MeasuredValuesCache()));
    }
}
