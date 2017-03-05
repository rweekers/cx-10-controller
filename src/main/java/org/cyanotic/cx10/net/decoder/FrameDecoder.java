package org.cyanotic.cx10.net.decoder;

import org.bytedeco.javacv.Frame;
import org.cyanotic.cx10.api.FrameListener;
import org.cyanotic.cx10.utils.ExecutorUtils;

import java.io.IOException;
import java.io.StringWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by gerard on 5-3-17.
 */
public class FrameDecoder implements AutoCloseable {
    private static final int DURATION_THRESHOLD = 1000 / 25;
    private final H264Decoder h264Decoder;
    private final AtomicBoolean isProcessing = new AtomicBoolean();
    private final AtomicLong framesGrabbed = new AtomicLong();
    private final AtomicLong framesProcessed = new AtomicLong();
    private final AtomicLong framesDropped = new AtomicLong();

    public FrameDecoder(String host, int port, FrameListener frameListener) throws IOException {
        h264Decoder = new H264Decoder(new CX10NalDecoder(host, port));
        ExecutorUtils.scheduleVideoDecoder(() -> {
            try {
                while (h264Decoder.isConnected()) {
                    final Frame frame = h264Decoder.readFrame();
                    if (frame != null) {
                        framesGrabbed.incrementAndGet();
                        if (isProcessing.compareAndSet(false, true)) {
                            Instant before = Instant.now();
                            frameListener.frameReceived(frame);
                            long duration = Duration.between(before, Instant.now()).toMillis();
                            if (duration > DURATION_THRESHOLD) {
                                System.out.println("Processing took " + duration + " ms!");
                            }
                            framesProcessed.incrementAndGet();
                            isProcessing.set(false);
                        } else {
                            framesDropped.incrementAndGet();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void close() throws Exception {
        h264Decoder.close();
    }

    public void printStats() {
        StringWriter writer = new StringWriter();
        writer.append("Video player stats\n");
        writer.append("FPS: ").append(String.valueOf(framesGrabbed.getAndSet(0))).append(" (grabbed)").append('\n');
        writer.append("FPS: ").append(String.valueOf(framesProcessed.getAndSet(0))).append(" (processed)").append('\n');
        writer.append("Frames not processed: ").append(String.valueOf(framesDropped.get())).append('\n');
        System.out.println(writer.toString());
    }
}
