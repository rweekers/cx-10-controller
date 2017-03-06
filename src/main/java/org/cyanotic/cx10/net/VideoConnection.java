package org.cyanotic.cx10.net;

import org.cyanotic.cx10.utils.ByteUtils;

import java.io.IOException;

/**
 * Created by cyanotic on 27/11/2016.
 */
public class VideoConnection extends AbstractTCPConnection {

    public VideoConnection(String host, int port) throws IOException {
        super(host, port);
        initialize();
    }

    private void initialize() throws IOException {
        logger.info("Initializing video...");
        sendMessage(ByteUtils.loadMessageFromFile("video.bin"), 106);
        logger.info("Video initialized");
    }
}
