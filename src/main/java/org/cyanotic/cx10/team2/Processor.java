package org.cyanotic.cx10.team2;

import java.awt.*;

/**
 * Created by gerard on 9-3-17.
 */
public interface Processor {
    Delta getDelta();
    void capture();
    void setColor(Color color);
    void setThreshold(int threshold);
}