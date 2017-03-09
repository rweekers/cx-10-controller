package org.cyanotic.cx10.net.decoder;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

/**
 * @deprecated use {@link H264Decoder} instead
 */
@Deprecated
public class H264DecoderWithThread implements Closeable, Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final CX10NalDecoder cx10NalDecoder;
    private final PipedInputStream inputStream;
    private final PipedOutputStream outputStream;
    private final FrameGrabber grabber;
    private boolean initialized = false;

    public H264DecoderWithThread(CX10NalDecoder cx10NalDecoder) throws IOException {
        this.cx10NalDecoder = cx10NalDecoder;
        this.outputStream = new PipedOutputStream();
        this.inputStream = new PipedInputStream(outputStream);
        this.grabber = new FFmpegFrameGrabber(inputStream);
    }

    @Override
    public void close() throws IOException {
        System.exit(1);
//        grabber.close(); // causes a SIGTERM in the JVM
        inputStream.close();
        outputStream.close();
    }

    @Override
    public void run() {
        try {
            while (true) {
                final byte[] data = cx10NalDecoder.readNalPacket();
                if (data != null) {
                    outputStream.write(data);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to decode frame", e);
        }
    }

    public Frame readFrame() throws IOException {
        if (!initialized) {
            initialized = true;
            grabber.start();
        }
        return grabber.grab();
    }
}
