package org.cyanotic.cx10.team2;

import java.awt.*;

/**
 * Created by dirkluijk on 09-03-17.
 */
public class Delta extends Point {
    private final int scale;

    public Delta(final int x, final int y, final int scale) {
        super(x, y);
        this.scale = scale;
    }

    public int getScale() {
        return scale;
    }

    @Override
    public String toString() {
        return "Delta{" +
                "scale=" + scale +
                "} " + super.toString();
    }
}
