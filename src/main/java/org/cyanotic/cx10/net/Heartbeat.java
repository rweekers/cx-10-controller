package org.cyanotic.cx10.net;

import org.cyanotic.cx10.utils.ByteUtils;

import java.io.*;
import java.net.Socket;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class Heartbeat implements AutoCloseable, Runnable {

    private final Socket socket;

    public Heartbeat(String host, int port) throws IOException {
        socket = new Socket(host, port);
    }

    @Override
    public void close() throws IOException {
        socket.close();
    }

    @Override
    public void run() {
        try {
            sendHeartBeat();
        } catch (IOException e) {
            System.err.println("Unable to send heartbeat");
            e.printStackTrace();
        }
    }

    private void sendHeartBeat() throws IOException {
        System.out.println("Sending heartbeat...");
        byte[] heartbeatData = ByteUtils.loadMessageFromFile("heartbeat.bin");
        int start = 0;
        int len = heartbeatData.length;
        OutputStream out = socket.getOutputStream();
        DataOutputStream dos = new DataOutputStream(out);

        if (len > 0) {
            dos.write(heartbeatData, start, len);
        }
        dos.flush();
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buf = new byte[106];
        int bytesRead;
        bytesRead = dis.read(buf);
        baos.write(buf, 0, bytesRead);
        System.out.println("The drone is alive.");
    }
}
