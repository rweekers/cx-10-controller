package org.cyanotic.cx10.ui;

import org.cyanotic.cx10.CX10;
import org.cyanotic.cx10.io.controls.Controller;
import org.cyanotic.cx10.io.video.IVideoPlayer;

import javax.swing.*;
import java.io.IOException;
import java.util.stream.Stream;

/**
 * Created by orfeo.ciano on 29/11/2016.
 */
public class MainWindow extends JFrame {
    private final CX10 cx10;

    private JComboBox<Controller> cmbControllers;
    private JComboBox<IVideoPlayer> cmbPlayers;
    private JButton btnConnect;
    private JButton btnControls;
    private JButton btnVideo;
    private JLabel lblStatus;
    private JPanel panel;

    private boolean isConnected = false;
    private boolean isPlaying = false;
    private boolean isControlled = false;

    public MainWindow(Controller[] controllers, IVideoPlayer[] players) {
        this.cx10 = new CX10();

        Stream.of(controllers).forEach(cmbControllers::addItem);
        Stream.of(players).forEach(cmbPlayers::addItem);

        btnConnect.setEnabled(true);
        btnControls.setEnabled(false);
        btnVideo.setEnabled(false);

        btnConnect.addActionListener(e -> onConnectClicked());
        btnControls.addActionListener(e -> onControlsClicked());
        btnVideo.addActionListener(e -> onVideoClicked());

        add(panel);
        pack();
        setTitle("CX-10WD CommandDispatcher");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        panel.setVisible(true);
        setVisible(true);
    }

    private void onConnectClicked() {
        MainWindowModel model;
        if (!isConnected) {
            try {
                model = getModel();
                model.setBtnConnectEnabled(false);
                model.setBtnConnectText("Connecting...");

                setModel(model);

                cx10.connect();
                isConnected = true;

                model = getModel();
                model.setBtnConnectEnabled(true);
                model.setBtnConnectText("Disconnect");
                model.setBtnVideoEnabled(true);
                model.setBtnControlsEnabled(true);

                setModel(model);
            } catch (IOException e) {
                e.printStackTrace();

                model = getModel();
                model.setLblStatusText(e.getMessage());
                model.setBtnConnectEnabled(true);
                model.setBtnConnectText("Connect");
                setModel(model);
            }
        } else {
            cx10.disconnect();
            isConnected = false;

            model = getModel();
            model.setBtnConnectEnabled(true);
            model.setBtnConnectText("Connect");
            model.setBtnVideoEnabled(false);
            model.setBtnControlsEnabled(false);

            setModel(model);
        }

    }

    private void onControlsClicked() {
        MainWindowModel model;
        if (!isControlled) {
            try {
                Controller controller = (Controller) cmbControllers.getSelectedItem();
                if (controller == null) {
                    return;
                }

                model = getModel();
                model.setBtnVideoText("Init Controller...");
                model.setBtnVideoEnabled(false);
                setModel(model);

                cx10.startControls(controller);
                isControlled = true;

                model = getModel();
                model.setBtnControlsText("Stop Controller");
                setModel(model);

            } catch (IOException e) {
                e.printStackTrace();
                model = getModel();
                model.setLblStatusText(e.getMessage());
                setModel(model);
            }

        } else {
            cx10.stopControls();
            isControlled = false;

            model = getModel();
            model.setBtnControlsText("Start Controller");
            setModel(model);
        }
    }

    private void onVideoClicked() {
        MainWindowModel model;
        if (!isPlaying) {
            try {
                IVideoPlayer player = (IVideoPlayer) cmbPlayers.getSelectedItem();
                if (player == null) {
                    return;
                }

                model = getModel();
                model.setBtnVideoText("Init Video...");
                model.setBtnVideoEnabled(false);
                setModel(model);

                cx10.startVideo(player);
                isPlaying = true;

                model = getModel();
                model.setBtnVideoText("Stop Video");
                model.setBtnVideoEnabled(true);
                setModel(model);
            } catch (IOException e) {
                e.printStackTrace();
                model = getModel();
                model.setLblStatusText(e.getMessage());
                model.setBtnVideoEnabled(true);
                model.setBtnVideoText("Start Video");
                setModel(model);
            }
        } else {
            cx10.stopVideo();
            isPlaying = false;

            model = getModel();
            model.setBtnVideoText("Start Video");

            setModel(model);
        }
    }

    private MainWindowModel getModel() {
        MainWindowModel model = new MainWindowModel();
        model.setBtnConnectEnabled(btnConnect.isEnabled());
        model.setBtnControlsEnabled(btnControls.isEnabled());
        model.setBtnVideoEnabled(btnVideo.isEnabled());

        model.setBtnConnectText(btnConnect.getText());
        model.setBtnControlsText(btnControls.getText());
        model.setBtnVideoText(btnVideo.getText());
        model.setLblStatusText(lblStatus.getText());
        return model;
    }

    private void setModel(final MainWindowModel model) {
        SwingUtilities.invokeLater(() -> {
            btnConnect.setEnabled(model.isBtnConnectEnabled());
            btnConnect.setText(model.getBtnConnectText());

            btnControls.setEnabled(model.isBtnControlsEnabled());
            btnControls.setText(model.getBtnControlsText());

            btnVideo.setEnabled(model.isBtnVideoEnabled());
            btnVideo.setText(model.getBtnVideoText());

            lblStatus.setText(model.getLblStatusText());
        });
    }

}
