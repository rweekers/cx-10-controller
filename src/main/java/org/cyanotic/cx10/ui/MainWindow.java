package org.cyanotic.cx10.ui;

import org.cyanotic.cx10.CX10;
import org.cyanotic.cx10.api.Controller;
import org.cyanotic.cx10.api.FrameListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Collection;
import java.util.function.Supplier;

/**
 * Created by orfeo.ciano on 29/11/2016.
 */
public class MainWindow extends JFrame {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private JPanel panel;
    private JComboBox<Supplier<Controller>> cmbControllers;
    private JComboBox<Supplier<FrameListener>> cmbFrameListeners;
    private JButton btnConnect;
    private JButton btnDisconnect;
    private JLabel lblStatus;

    private CX10 cx10;

    public MainWindow(Collection<Supplier<Controller>> controllers, Collection<Supplier<FrameListener>> frameListeners) {
        controllers.forEach(cmbControllers::addItem);
        frameListeners.forEach(cmbFrameListeners::addItem);

        updateUI("");

        btnConnect.addActionListener(e -> onConnectClicked());
        btnDisconnect.addActionListener(e -> onDisconnectClicked());

        add(panel);
        pack();
        setTitle("CX-10WD CommandDispatcher");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        panel.setVisible(true);
        setVisible(true);
    }

    public boolean isConnected() {
        return cx10 != null;
    }

    private void onConnectClicked() {
        if (!isConnected()) {
            try {
                Supplier<Controller> controllerSupplier = (Supplier<Controller>) cmbControllers.getSelectedItem();
                Supplier<FrameListener> frameListenerSupplier = (Supplier<FrameListener>) cmbFrameListeners.getSelectedItem();
                cx10 = new CX10(controllerSupplier.get(), frameListenerSupplier.get());
                updateUI("Connected...");
            } catch (Exception e) {
                logger.error("Failed to connect", e);
                updateUI(e.getMessage());
            }
        }
    }

    private void onDisconnectClicked() {
        if (isConnected()) {
            try {
                cx10.close();
                cx10 = null;
                updateUI("Disconnected...");
            } catch (Exception e) {
                logger.error("Failed to disconnect", e);
                updateUI(e.getMessage());
            }
        }

    }

    private void updateUI(String status) {
        SwingUtilities.invokeLater(() -> {
            btnConnect.setEnabled(!isConnected());
            btnDisconnect.setEnabled(isConnected());
            lblStatus.setText(status);
        });
    }

}
