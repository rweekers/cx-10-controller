package org.cyanotic.cx10.team2;

/**
 * Created by gerard on 9-3-17.
 */
public interface Processor {
    Delta getDelta();

    boolean isReadyForCapture();

    void capture();
    void setColor(Color color);
}
