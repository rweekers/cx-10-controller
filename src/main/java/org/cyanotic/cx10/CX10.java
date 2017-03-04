package org.cyanotic.cx10;

import org.bytedeco.javacv.Frame;
import org.cyanotic.cx10.io.controls.CommandDispatcher;
import org.cyanotic.cx10.io.controls.Controller;
import org.cyanotic.cx10.io.video.IVideoPlayer;
import org.cyanotic.cx10.net.*;
import org.cyanotic.cx10.utils.ByteUtils;

import java.io.IOException;

/**
 * Created by cyanotic on 28/11/2016.
 */
public class CX10 {
    public static final String HOST = "172.16.10.1";

    private TransportConnection transportConnection;
    private IVideoPlayer currentPlayer;
    private CommandDispatcher commandDispatcher;
    private Heartbeat heartbeat;
    private H264Decoder decoder;

    public void connect() throws IOException {
        if (transportConnection != null) {
            transportConnection.disconnect();
        }
        transportConnection = new TransportConnection(HOST, 8888);
        transportConnection.connect();
        transportConnection.setName("Transport Connection");
        transportConnection.sendMessage(ByteUtils.loadMessageFromFile("message1.bin"), 106);
        transportConnection.sendMessage(ByteUtils.loadMessageFromFile("message2.bin"), 106);
        transportConnection.sendMessage(ByteUtils.loadMessageFromFile("message3.bin"), 170);
        transportConnection.sendMessage(ByteUtils.loadMessageFromFile("message4.bin"), 106);
        transportConnection.sendMessage(ByteUtils.loadMessageFromFile("message5.bin"), 106);
        heartbeat = new Heartbeat(HOST, 8888);
        heartbeat.start();
    }

    public void disconnect() {
        if (heartbeat != null) {
            heartbeat.interrupt();
        }

        stopControls();
        stopVideo();

        if (transportConnection != null) {
            try {
                transportConnection.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void startControls(Controller inputDevice) throws IOException {
        stopControls();
        commandDispatcher = new CommandDispatcher(inputDevice, new CommandConnection(HOST, 8895));
        commandDispatcher.start();
    }

    public void stopControls() {
        if (commandDispatcher != null) {
            commandDispatcher.interrupt();
            commandDispatcher = null;
        }
    }

    public void startVideo(IVideoPlayer player) throws IOException {
        stopVideo();
        currentPlayer = player;
        player.start();
        startVideoDecoder();
    }

    public void stopVideo() {
        if (currentPlayer != null) {
            currentPlayer.stop();
            currentPlayer = null;
        }
    }

    private void startVideoDecoder() throws IOException {
        if (decoder != null) {
            return;
        }

        decoder = new H264Decoder(new CX10NalDecoder(HOST, 8888));
        decoder.connect();
        new Thread(() -> {
            boolean error = false;
            while (decoder.isConnected() && !error) {
                try {
                    final Frame frame = decoder.readFrame();
                    if (frame != null && currentPlayer != null && currentPlayer.isActive()) {
                        currentPlayer.imageReceived(frame);
                    } else {
                        decoder.close();
                    }
                } catch (IOException e) {
                    error = true;
                    e.printStackTrace();
                }
            }
            try {
                decoder.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            decoder = null;
        }, decoder.toString()).start();
    }
}
