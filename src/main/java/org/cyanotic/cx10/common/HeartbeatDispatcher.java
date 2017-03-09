package org.cyanotic.cx10.common;

import org.cyanotic.cx10.net.TransportConnection;
import org.cyanotic.cx10.utils.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class HeartbeatDispatcher implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final TransportConnection transportConnection;

    public HeartbeatDispatcher(TransportConnection transportConnection) throws IOException {
        this.transportConnection = transportConnection;
    }

    @Override
    public void run() {
        try {
            logger.info("Sending heartbeat...");
            transportConnection.sendMessage(ByteUtils.loadMessageFromFile("heartbeat.bin"), 106);
            logger.info("The drone is alive.");
        } catch (IOException e) {
            logger.error("Unable to send heartbeat", e);
        }
    }
}
