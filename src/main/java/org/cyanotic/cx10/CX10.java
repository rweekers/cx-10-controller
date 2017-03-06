package org.cyanotic.cx10;

import org.cyanotic.cx10.api.Controller;
import org.cyanotic.cx10.api.FrameListener;
import org.cyanotic.cx10.net.*;
import org.cyanotic.cx10.net.decoder.CX10NalDecoder;
import org.cyanotic.cx10.net.decoder.FrameDecoder;
import org.cyanotic.cx10.net.decoder.H264Decoder;

/**
 * Created by cyanotic on 28/11/2016.
 */
public class CX10 implements AutoCloseable {
    private static final String HOST = "172.16.10.1";
    private static final int TRANSPORT_PORT = 8888;
    private static final int COMMAND_PORT = 8895;

    private final Heartbeat heartbeat;
    private final CommandDispatcher commandDispatcher;
    private final FrameDecoder decoder;

    public CX10(Controller controller, FrameListener frameListener) throws Exception {
        heartbeat = new Heartbeat(new TransportConnection(HOST, TRANSPORT_PORT));
        commandDispatcher = new CommandDispatcher(new CommandConnection(HOST, COMMAND_PORT), controller);
        decoder = new FrameDecoder(new H264Decoder(new CX10NalDecoder(new VideoConnection(HOST, TRANSPORT_PORT))), frameListener);
    }

    @Override
    public void close() throws Exception {
        decoder.close();
        commandDispatcher.close();
        heartbeat.close();
    }

}
