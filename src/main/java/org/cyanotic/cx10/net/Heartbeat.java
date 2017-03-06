package org.cyanotic.cx10.net;

import org.cyanotic.cx10.utils.ByteUtils;
import org.cyanotic.cx10.utils.ExecutorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class Heartbeat implements AutoCloseable {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final TransportConnection transportConnection;
    private final ScheduledFuture<?> future;

    public Heartbeat(TransportConnection transportConnection) throws IOException {
        this.transportConnection = transportConnection;
        this.future = ExecutorUtils.scheduleHeartbeat(this::sendHeartBeat);
    }

    @Override
    public void close() throws IOException {
        future.cancel(false);
        transportConnection.close();
    }

    private void sendHeartBeat() {
        try {
            logger.info("Sending heartbeat...");
            transportConnection.sendMessage(ByteUtils.loadMessageFromFile("heartbeat.bin"), 106);
            logger.info("The drone is alive.");
        } catch (IOException e) {
            logger.error("Unable to send heartbeat", e);
        }
    }
}
