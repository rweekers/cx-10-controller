package org.cyanotic.cx10;

import org.cyanotic.cx10.api.Controller;
import org.cyanotic.cx10.controllers.FlyInACircle;
import org.cyanotic.cx10.controllers.Keyboard;
import org.cyanotic.cx10.api.FrameListener;
import org.cyanotic.cx10.framelisteners.SwingVideoPlayer;
import org.cyanotic.cx10.framelisteners.VideoRecorder;
import org.cyanotic.cx10.ui.MainWindow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class UILauncher {

    public static void main(String[] args) throws IOException {
        Collection<Supplier<Controller>> controllers = new ArrayList<>();
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
                return new SwingVideoPlayer();
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

        new MainWindow(controllers, frameListeners);
    }
}
