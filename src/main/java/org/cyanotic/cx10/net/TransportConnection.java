package org.cyanotic.cx10.net;

import org.cyanotic.cx10.utils.ByteUtils;

import java.io.IOException;

/**
 * Created by orfeo.ciano on 18/11/2016.
 */
public class TransportConnection extends AbstractTCPConnection {

    public TransportConnection(String host, int port) throws IOException {
        super(host, port);
        initialize();
    }

    private void initialize() throws IOException {
        logger.info("Initializing CX10...");
        sendMessage(ByteUtils.loadMessageFromFile("message1.bin"), 106);
        sendMessage(ByteUtils.loadMessageFromFile("message2.bin"), 106);
        sendMessage(ByteUtils.loadMessageFromFile("message3.bin"), 170);
        sendMessage(ByteUtils.loadMessageFromFile("message4.bin"), 106);
        sendMessage(ByteUtils.loadMessageFromFile("message5.bin"), 106);
        logger.info("CX10 initialized");
    }
}
