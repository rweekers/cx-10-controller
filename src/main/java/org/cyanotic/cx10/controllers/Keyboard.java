package org.cyanotic.cx10.controllers;

import org.cyanotic.cx10.api.Command;
import org.cyanotic.cx10.api.Controller;

import java.awt.*;
import java.awt.event.KeyEvent;

import static java.awt.event.KeyEvent.*;

/**
 * Created by orfeo.ciano on 29/11/2016.
 */
public class Keyboard implements KeyEventDispatcher, Controller {
    private final KeyboardFocusManager focusManager;
    private final Command command = new Command();

    public Keyboard() {
        this(KeyboardFocusManager.getCurrentKeyboardFocusManager());
    }

    public Keyboard(KeyboardFocusManager focusManager) {
        this.focusManager = focusManager;
        focusManager.addKeyEventDispatcher(this);
    }

    @Override
    public void close() {
        focusManager.removeKeyEventDispatcher(this);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getID() == KEY_PRESSED) {
            onKeyEvent(e, true);
        } else if (e.getID() == KEY_RELEASED) {
            onKeyEvent(e, false);
        }
        return false;
    }

    @Override
    public Command getCommand() {
        return command;
    }

    private void onKeyEvent(KeyEvent e, boolean isPressed) {
        int value = isPressed ? 127 : 0;
        switch (e.getKeyCode()) {
            case VK_W:
                command.setThrottle(value);
                break;
            case VK_S:
                command.setThrottle(-value);
                break;
            case VK_D:
                command.setYaw(value);
                break;
            case VK_A:
                command.setYaw(-value);
                break;
            case VK_I:
                command.setPitch(value);
                break;
            case VK_K:
                command.setPitch(-value);
                break;
            case VK_L:
                command.setRoll(value);
                break;
            case VK_J:
                command.setRoll(-value);
                break;
            case VK_UP:
                command.setTakeOff(isPressed);
                break;
            case VK_DOWN:
                command.setLand(isPressed);
                break;
        }
        e.consume();
    }
}
