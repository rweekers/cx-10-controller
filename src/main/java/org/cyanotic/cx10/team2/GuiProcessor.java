package org.cyanotic.cx10.team2;

import org.cyanotic.cx10.team2.ui.DeltaFrame;

/**
 * Created by dirkluijk on 09-03-17.
 */
public class GuiProcessor implements Processor {
    private DeltaFrame deltaFrame;

    public GuiProcessor(final DeltaFrame deltaFrame) {
        this.deltaFrame = deltaFrame;
    }

    @Override
    public Delta getDelta() {
        return deltaFrame.getDelta();
    }

    @Override
    public void capture() {
        // nothing
    }

    @Override
    public void setColor(final Color color) {
        // todo
    }
}
