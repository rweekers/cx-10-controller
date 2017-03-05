package org.cyanotic.cx10.net;

import org.cyanotic.cx10.utils.ByteUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Created by orfeo.ciano on 18/11/2016.
 */
public class TransportConnection implements AutoCloseable {

    private final Socket socket;

    public TransportConnection(String host, int port) throws IOException {
        InetAddress address = InetAddress.getByName(host);
        socket = new Socket(address, port);
    }

    public void close() throws IOException {
        socket.close();
    }

    public void sendMessage(byte[] bytes, int responseSize) throws IOException {
        System.out.println(ByteUtils.bytesToHex(bytes));
        DataOutputStream output = new DataOutputStream(socket.getOutputStream());
        output.write(bytes);
        byte[] buffer = new byte[responseSize];
        DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
        dataInputStream.read(buffer);
        System.out.println(ByteUtils.bytesToHex(buffer));
    }
}
