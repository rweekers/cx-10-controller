package org.cyanotic.cx10;

import org.cyanotic.cx10.api.Controller;
import org.cyanotic.cx10.api.FrameListener;
import org.cyanotic.cx10.net.CommandConnection;
import org.cyanotic.cx10.net.Heartbeat;
import org.cyanotic.cx10.net.TransportConnection;
import org.cyanotic.cx10.net.decoder.FrameDecoder;
import org.cyanotic.cx10.utils.ByteUtils;
import org.cyanotic.cx10.utils.ExecutorUtils;

import java.util.concurrent.ScheduledFuture;

/**
 * Created by cyanotic on 28/11/2016.
 */
public class CX10 implements AutoCloseable {
    public static final String HOST = "172.16.10.1";

    private final Controller controller;
    private final FrameListener frameListener;
    private final TransportConnection transportConnection;
    private final CommandConnection commandConnection;
    private final Heartbeat heartbeat;
    private final FrameDecoder decoder;
    private final ScheduledFuture heartbeatFuture;
    private final ScheduledFuture controllerFuture;
    private final ScheduledFuture printStatsFuture;

    public CX10(Controller controller, FrameListener frameListener) throws Exception {
        transportConnection = new TransportConnection(HOST, 8888);
        transportConnection.sendMessage(ByteUtils.loadMessageFromFile("message1.bin"), 106);
        transportConnection.sendMessage(ByteUtils.loadMessageFromFile("message2.bin"), 106);
        transportConnection.sendMessage(ByteUtils.loadMessageFromFile("message3.bin"), 170);
        transportConnection.sendMessage(ByteUtils.loadMessageFromFile("message4.bin"), 106);
        transportConnection.sendMessage(ByteUtils.loadMessageFromFile("message5.bin"), 106);
        commandConnection = new CommandConnection(HOST, 8895);
        heartbeat = new Heartbeat(HOST, 8888);
        heartbeatFuture = ExecutorUtils.scheduleHeartbeat(heartbeat);
        this.controller = controller;
        controllerFuture = ExecutorUtils.scheduleControllerDispatcher(() -> commandConnection.sendCommand(controller.getCommand()));
        this.frameListener = frameListener;
        decoder = new FrameDecoder(HOST, 8888, frameListener);
        printStatsFuture = ExecutorUtils.schedulePrintVideoStats(decoder::printStats);
    }

    @Override
    public void close() throws Exception {
        printStatsFuture.cancel(false);
        decoder.close();
        frameListener.close();
        controllerFuture.cancel(false);
        controller.close();
        heartbeatFuture.cancel(false);
        heartbeat.close();
        commandConnection.close();
        transportConnection.close();
    }

}
