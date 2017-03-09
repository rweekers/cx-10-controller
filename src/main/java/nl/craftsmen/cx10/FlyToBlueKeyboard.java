package nl.craftsmen.cx10;

import static java.awt.event.KeyEvent.KEY_PRESSED;
import static java.awt.event.KeyEvent.KEY_RELEASED;
import static java.awt.event.KeyEvent.VK_DOWN;
import static java.awt.event.KeyEvent.VK_LEFT;
import static java.awt.event.KeyEvent.VK_RIGHT;
import static java.awt.event.KeyEvent.VK_UP;

import java.awt.*;
import java.awt.event.KeyEvent;

import nl.craftsmen.cx10.measure.MeasuredValuesCache;

public class FlyToBlueKeyboard extends FlyToBlueController implements KeyEventDispatcher {
    private final KeyboardFocusManager focusManager;
    MeasuredValuesCache measuredValuesCache;

    public FlyToBlueKeyboard(MeasuredValuesCache measuredValuesCache) {
        super(measuredValuesCache);
        this.measuredValuesCache = measuredValuesCache;
        this.focusManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        focusManager.addKeyEventDispatcher(this);
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

    private void onKeyEvent(KeyEvent e, boolean isPressed) {
        int value = isPressed ? 127 : 0;
        measuredValuesCache.measurementAvailable = true;
        switch (e.getKeyCode()) {
        case VK_UP:
            System.out.println("up: " + isPressed + " y: " + measuredValuesCache.y );
            measuredValuesCache.y = measuredValuesCache.y + 4;
            break;
        case VK_DOWN:
            System.out.println("down: " + isPressed + " y: " + measuredValuesCache.y);
            measuredValuesCache.y = measuredValuesCache.y - 4;
            break;
        case VK_LEFT:
            System.out.println("left: " + isPressed+  " X: " + measuredValuesCache.x) ;
            measuredValuesCache.x = measuredValuesCache.x - 4;
            break;
        case VK_RIGHT:
            System.out.println("right: " + isPressed + " X: " + measuredValuesCache.x);
            measuredValuesCache.x = measuredValuesCache.x + 4;
            break;
        }
        e.consume();
    }

}
