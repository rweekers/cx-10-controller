package org.cyanotic.cx10.team2.ui;

import org.cyanotic.cx10.team2.Delta;

import javax.swing.*;

/**
 * Created by dirkluijk on 09-03-17.
 */
public class DeltaFrame extends JFrame {
    private JSpinner xSpinner;
    private JPanel deltaPanel;
    private JSpinner ySpinner;
    private JSpinner scaleSpinner;

    public DeltaFrame() {
        add(deltaPanel);

        final SpinnerNumberModel modelX = new SpinnerNumberModel(0, -100, 100, 1);
        final SpinnerNumberModel modelY = new SpinnerNumberModel(0, -100, 100, 1);
        final SpinnerNumberModel modelScale = new SpinnerNumberModel(0, -100, 100, 1);

        xSpinner.setModel(modelX);
        ySpinner.setModel(modelY);
        scaleSpinner.setModel(modelScale);

        pack();

        setTitle("DeltaFrame Window");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationByPlatform(true);

        deltaPanel.setVisible(true);
        setVisible(true);
    }

    public Delta getDelta() {
        return new Delta((Integer) xSpinner.getValue(), (Integer) ySpinner.getValue(), (Integer) scaleSpinner.getValue());
    }
}
