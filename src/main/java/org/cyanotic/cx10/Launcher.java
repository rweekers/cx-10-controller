package org.cyanotic.cx10;

import org.cyanotic.cx10.io.controls.FlyInACircle;
import org.cyanotic.cx10.io.controls.Keyboard;
import org.cyanotic.cx10.ui.MainWindow;

import java.awt.*;
import java.io.IOException;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class Launcher {

    public static void main(String[] args) throws IOException, InterruptedException {
        new MainWindow(new Keyboard(KeyboardFocusManager.getCurrentKeyboardFocusManager()), new FlyInACircle());
    }
}
