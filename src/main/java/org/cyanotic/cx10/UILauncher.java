package org.cyanotic.cx10;

import nl.craftsmen.cx10.FlyToBlueController;
import nl.craftsmen.cx10.measure.IMeasuredValues;
import nl.craftsmen.cx10.measure.MeasureValuesCache;
import org.cyanotic.cx10.api.Controller;
import org.cyanotic.cx10.controllers.FlyInACircle;
import org.cyanotic.cx10.controllers.Keyboard;
import org.cyanotic.cx10.api.FrameListener;
import org.cyanotic.cx10.framelisteners.SwingVideoPlayer;
import org.cyanotic.cx10.framelisteners.VideoRecorder;
import org.cyanotic.cx10.imagelisteners.RectangeDetector;
import org.cyanotic.cx10.ui.MainWindow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class UILauncher {

    public static void main(String[] args) throws IOException {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);

        MeasureValuesCache measureValuesCache = new MeasureValuesCache();

        Collection<Supplier<Controller>> controllers = new ArrayList<>();
        controllers.add(new Supplier<Controller>() {
            @Override
            public Controller get() {
                return new FlyToBlueController(measureValuesCache);
            }
            @Override
            public String toString() {
                return "FlyToBlueController";
            }
        });
        controllers.add(new Supplier<Controller>() {
            @Override
            public Controller get() {
                return new Keyboard();
            }

            @Override
            public String toString() {
                return "Keyboard";
            }
        });
        controllers.add(new Supplier<Controller>() {
            @Override
            public Controller get() {
                return new FlyInACircle();
            }

            @Override
            public String toString() {
                return "FlyInACircle";
            }
        });


        Collection<Supplier<FrameListener>> frameListeners = new ArrayList<>();
        frameListeners.add(new Supplier<FrameListener>() {
            @Override
            public FrameListener get() {
                return new SwingVideoPlayer(executor);
            }

            @Override
            public String toString() {
                return "SwingVideoPlayer";
            }
        });
        frameListeners.add(new Supplier<FrameListener>() {
            @Override
            public FrameListener get() {
                return new VideoRecorder();
            }

            @Override
            public String toString() {
                return "VideoRecorder";
            }
        });
        frameListeners.add(new Supplier<FrameListener>() {
            @Override
            public FrameListener get() {
                return new RectangeDetector(executor,measureValuesCache);
            }

            @Override
            public String toString() {
                return "RectangleDetector";
            }
        });

        new MainWindow(executor, controllers, frameListeners);
    }
}
