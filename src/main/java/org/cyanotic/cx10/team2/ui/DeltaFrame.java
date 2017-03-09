package org.cyanotic.cx10.team2.ui;

import org.cyanotic.cx10.team2.Delta;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by dirkluijk on 09-03-17.
 */
public class DeltaFrame extends JFrame {
    private JSpinner xSpinner;
    private JPanel deltaPanel;
    private JSpinner ySpinner;
    private JSpinner scaleSpinner;
    private JButton foundButton;

    private boolean found = false;

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

        foundButton.addActionListener(e -> found = true);
    }

    public Delta getDelta() {
        return new Delta((Integer) xSpinner.getValue(), (Integer) ySpinner.getValue(), (Integer) scaleSpinner.getValue());
    }

    public boolean isReadyForCapture() {
        return found;
    }
}
