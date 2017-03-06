package org.cyanotic.cx10.net;

import org.cyanotic.cx10.utils.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public abstract class AbstractTCPConnection implements AutoCloseable {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;

    public AbstractTCPConnection(String host, int port) throws IOException {
        InetAddress address = InetAddress.getByName(host);
        socket = new Socket(address, port);
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public void sendMessage(byte[] bytes, int responseSize) throws IOException {
        logger.debug("Message: {}", ByteUtils.lazyBytesToHex(bytes));
        outputStream.write(bytes);
        byte[] response = new byte[responseSize];
        inputStream.read(response);
        logger.debug("Response: {}", ByteUtils.lazyBytesToHex(response));
    }
}
