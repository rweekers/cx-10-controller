package org.cyanotic.cx10.team2.ui;

import javax.swing.*;
import java.awt.*;

/**
 * Created by dirkluijk on 09-03-17.
 */
public class Delta extends JFrame {
    private JSpinner xSpinner;
    private JPanel deltaPanel;
    private JSpinner ySpinner;
    private JSpinner scaleSpinner;

    public Delta() {
        add(deltaPanel);

        final SpinnerNumberModel modelX = new SpinnerNumberModel(0, -100, 100, 1);
        final SpinnerNumberModel modelY = new SpinnerNumberModel(0, -100, 100, 1);
        final SpinnerNumberModel modelScale = new SpinnerNumberModel(0, -100, 100, 1);

        xSpinner.setModel(modelX);
        ySpinner.setModel(modelY);
        scaleSpinner.setModel(modelScale);

        pack();

        setTitle("Delta Window");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationByPlatform(true);

        deltaPanel.setVisible(true);
        setVisible(true);
    }

    public Point getDelta() {
        return new Point((Integer) xSpinner.getValue(), (Integer) ySpinner.getValue());
    }


    public int getScale() {
        return (Integer) scaleSpinner.getValue();
    }
}
