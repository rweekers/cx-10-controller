package org.cyanotic.cx10.team2;

import org.cyanotic.cx10.team2.ui.Delta;

import java.awt.*;

/**
 * Created by dirkluijk on 09-03-17.
 */
public class GuiProcessor implements Processor {
    private Delta deltaWindow;

    public GuiProcessor(final Delta deltaWindow) {
        this.deltaWindow = deltaWindow;
    }

    @Override
    public Point getDelta() {
        return deltaWindow.getDelta();
    }

    @Override
    public int getScale() {
        return deltaWindow.getScale();
    }
}
