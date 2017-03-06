package org.cyanotic.cx10.net;

import org.cyanotic.cx10.api.Command;
import org.cyanotic.cx10.utils.ByteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class CommandConnection extends AbstractUDPConnection {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public CommandConnection(String host, int port) throws IOException {
        super(host, port);
    }

    private static byte checksum(byte[] bytes) {
        byte sum = 0;
        for (byte b : bytes) {
            sum ^= b;
        }
        return sum;
    }

    public void sendCommand(Command command) {
        sendMessage(asByteArray(command));
    }

    private byte[] asByteArray(Command command) {
        int pitch = command.getPitch() + 128;
        int yaw = command.getYaw() + 128;
        int roll = command.getRoll() + 128;
        int throttle = command.getThrottle() + 128;
        boolean takeOff = command.isTakeOff();
        boolean land = command.isLand();

        byte[] data = new byte[8];
        data[0] = (byte) 0xCC;
        data[1] = (byte) roll;
        data[2] = (byte) pitch;
        data[3] = (byte) throttle;
        data[4] = (byte) yaw;
        if (takeOff) {
            data[5] = (byte) 0x01;
        } else if (land) {
            data[5] = (byte) 0x02;
        } else {
            data[5] = (byte) 0x00;
        }

        data[6] = checksum(ByteUtils.asUnsigned(data[1], data[2], data[3], data[4], data[5]));

        data[7] = (byte) 0x33;
        return data;
    }
}
