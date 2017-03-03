package org.cyanotic.cx10;

import org.cyanotic.cx10.io.controls.Controller;
import org.cyanotic.cx10.io.controls.FlyInACircle;
import org.cyanotic.cx10.io.controls.Keyboard;
import org.cyanotic.cx10.io.video.IVideoPlayer;
import org.cyanotic.cx10.io.video.SwingVideoPlayer;
import org.cyanotic.cx10.io.video.VideoRecorder;
import org.cyanotic.cx10.ui.MainWindow;

import java.io.IOException;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class Launcher {

    public static void main(String[] args) throws IOException {
        Controller[] controllers = {new Keyboard(), new FlyInACircle()};
        IVideoPlayer[] players = {new SwingVideoPlayer(), new VideoRecorder()};
        new MainWindow(controllers, players);
    }
}
