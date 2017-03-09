package org.cyanotic.cx10;

import org.cyanotic.cx10.api.Controller;
import org.cyanotic.cx10.api.FrameListener;
import org.cyanotic.cx10.common.CommandDispatcher;
import org.cyanotic.cx10.common.FrameDispatcher;
import org.cyanotic.cx10.common.HeartbeatDispatcher;
import org.cyanotic.cx10.common.PrintStatsFrameListener;
import org.cyanotic.cx10.net.CommandConnection;
import org.cyanotic.cx10.net.TransportConnection;
import org.cyanotic.cx10.net.VideoConnection;
import org.cyanotic.cx10.net.decoder.CX10NalDecoder;
import org.cyanotic.cx10.net.decoder.H264Decoder;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.*;

/**
 * Created by cyanotic on 28/11/2016.
 */
public class CX10 implements Closeable {
    private static final String HOST = "172.16.10.1";
    private static final int TRANSPORT_PORT = 8888;
    private static final int COMMAND_PORT = 8895;

    private final TransportConnection transportConnection;
    private final CommandConnection commandConnection;
    private final VideoConnection videoConnection;
    private final H264Decoder h264Decoder;

    private final Future<?> heartbeatFuture;
    private final Future<?> commandDispatcherFuture;
    private final Future<?> frameDispatcherFuture;
    private final Future<?> printStatsFuture;

    public CX10(ScheduledExecutorService executor, Controller controller, FrameListener frameListener) throws Exception {
        transportConnection = new TransportConnection(HOST, TRANSPORT_PORT);
        HeartbeatDispatcher heartbeatDispatcher = new HeartbeatDispatcher(transportConnection);
        heartbeatFuture = executor.scheduleWithFixedDelay(heartbeatDispatcher, 0, 5, TimeUnit.SECONDS);

        commandConnection = new CommandConnection(HOST, COMMAND_PORT);
        CommandDispatcher commandDispatcher = new CommandDispatcher(commandConnection, controller);
        commandDispatcherFuture = executor.scheduleWithFixedDelay(commandDispatcher, 0, 50, TimeUnit.MILLISECONDS);

        videoConnection = new VideoConnection(HOST, TRANSPORT_PORT);
        CX10NalDecoder cx10NalDecoder = new CX10NalDecoder(videoConnection.getInputStream());
        h264Decoder = new H264Decoder(cx10NalDecoder);

        PrintStatsFrameListener wrappedFrameListener = new PrintStatsFrameListener(frameListener);
        FrameDispatcher frameDispatcher = new FrameDispatcher(h264Decoder, wrappedFrameListener);
        frameDispatcherFuture = executor.submit(frameDispatcher);
        printStatsFuture = executor.scheduleWithFixedDelay(wrappedFrameListener, 0, 1, TimeUnit.SECONDS);
    }

    @Override
    public void close() throws IOException {
        printStatsFuture.cancel(false);
        frameDispatcherFuture.cancel(false);
        commandDispatcherFuture.cancel(false);
        heartbeatFuture.cancel(false);

        h264Decoder.close();
        videoConnection.close();
        commandConnection.close();
        transportConnection.close();
    }

}
