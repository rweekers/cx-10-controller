package org.cyanotic.cx10.net;

import org.cyanotic.cx10.utils.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class AbstractUDPConnection implements AutoCloseable {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final DatagramSocket socket;
    private final InetAddress host;
    private final int port;

    public AbstractUDPConnection(String host, int port) throws IOException {
        this.host = InetAddress.getByName(host);
        this.port = port;
        this.socket = new DatagramSocket();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public void sendMessage(byte[] bytes) {
        logger.debug("Message: {}", ByteUtils.lazyBytesToHex(bytes));
        DatagramPacket packet = new DatagramPacket(bytes, 0, bytes.length, host, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            logger.error("Unable to send packet", e);
        }
    }
}
