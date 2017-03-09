package org.cyanotic.cx10;

import org.cyanotic.cx10.api.Controller;
import org.cyanotic.cx10.api.FrameListener;
import org.cyanotic.cx10.controllers.FlyInACircle;
import org.cyanotic.cx10.controllers.Keyboard;
import org.cyanotic.cx10.framelisteners.SwingVideoPlayer;
import org.cyanotic.cx10.framelisteners.VideoRecorder;
import org.cyanotic.cx10.team2.ProcessorController;
import org.cyanotic.cx10.team2.VideoProcessor;
import org.cyanotic.cx10.ui.MainWindow;

import java.awt.*;
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

        Collection<Supplier<Controller>> controllers = new ArrayList<>();

        controllers.add(new Supplier<Controller>() {
            @Override
            public Controller get() {
                return new ProcessorController(
                        new VideoProcessor(executor),
                        Color.RED,
                        Color.BLUE
                );

//                return new ProcessorController(
//                        new GuiProcessor(new DeltaFrame()),
//                        Color.RED,
//                        Color.BLUE
//                );
            }

            @Override
            public String toString() {
                return "Team 2 Capture Red - Return Blue";
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

        new MainWindow(executor, controllers, frameListeners);
    }
}
