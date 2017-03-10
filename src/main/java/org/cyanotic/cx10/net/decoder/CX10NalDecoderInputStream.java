package org.cyanotic.cx10.net.decoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class CX10NalDecoderInputStream implements Closeable, Runnable {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final CX10NalDecoder cx10NalDecoder;
    private final PipedInputStream inputStream;
    private final PipedOutputStream outputStream;

    public CX10NalDecoderInputStream(CX10NalDecoder cx10NalDecoder) throws IOException {
        this.cx10NalDecoder = cx10NalDecoder;
        this.outputStream = new PipedOutputStream();
        this.inputStream = new PipedInputStream(outputStream);
    }

    public PipedInputStream getInputStream() {
        return inputStream;
    }

    @Override
    public void close() throws IOException {
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
}
