package org.cyanotic.cx10.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by cyanotic on 19/11/2016.
 */
public class ByteUtils {

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();
    
    public static Object lazyBytesToHex(byte[] bytes) {
        return new Object() {
            @Override
            public String toString() {
                return bytesToHex(bytes);
            }
        };
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder output = new StringBuilder();
        for (byte aByte : bytes) {
            int v = aByte & 0xFF;
            output.append(hexArray[v >>> 4]);
            output.append(hexArray[v & 0x0F]);
            output.append(" ");
        }
        return output.toString();
    }

    public static byte[] intToBytes(int value) {
        return new byte[]{(byte) (value >> 24), (byte) (value >> 16), (byte) (value >> 8), (byte) value};
    }

    public static byte[] asUnsigned(int... values) {
        byte[] bytes = new byte[values.length];
        for (int i = 0; i < values.length; i++) {
            int value = values[i];
            if (value > Byte.MAX_VALUE) {
                bytes[i] = (byte) value;
            } else {
                bytes[i] = (byte) (value & 0xff);
            }
        }
        return bytes;
    }

    public static byte[] loadMessageFromFile(String fileName) throws IOException {
        InputStream resourceAsStream = ByteUtils.class.getResourceAsStream("/" + fileName);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[4096];

        while ((nRead = resourceAsStream.read(data, 0, data.length)) > 0) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();

        return buffer.toByteArray();

    }
}
