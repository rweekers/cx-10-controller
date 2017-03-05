package org.cyanotic.cx10.net.decoder;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.cyanotic.cx10.utils.ByteUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Video format:
 * Stream #0:0: Video: h264 (Main), yuv420p(progressive), 720x576, 25 fps, 25 tbr, 1200k tbn, 50 tbc
 */
public class CX10H264Decoder implements AutoCloseable {
    private final Socket socket;
    private final InputStream inputStream;
    private final OutputStream outputStream;
    private final FrameGrabber grabber;
    private boolean initialized = false;
    private boolean closed = false;

    public CX10H264Decoder(String host, int port) throws IOException {
        InetAddress address = InetAddress.getByName(host);
        socket = new Socket(address, port);
        outputStream = socket.getOutputStream();
        inputStream = socket.getInputStream();
        grabber = new FFmpegFrameGrabber(inputStream);
    }

    @Override
    public void close() throws IOException {
        if (!isConnected()) {
            throw new IOException("Not connected");
        }
        if (closed) {
            throw new IOException("Already closed!");
        }
        closed = true;
        grabber.close();
        outputStream.close();
        inputStream.close();
        socket.close();
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public Frame readFrame() throws IOException {
        if (!isConnected()) {
            return null;
        }
        if (!initialized) {
            initialized = true;
            byte[] bytes = ByteUtils.loadMessageFromFile("video.bin");
            outputStream.write(bytes);
            byte[] response = new byte[106];
            inputStream.read(response);
            grabber.start();
        }
        return grabber.grab();
    }
}
