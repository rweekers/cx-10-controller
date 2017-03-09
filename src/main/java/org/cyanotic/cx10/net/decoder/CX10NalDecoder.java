package org.cyanotic.cx10.net.decoder;


import org.cyanotic.cx10.utils.ByteUtils;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * Created by cyanotic on 27/11/2016.
 */
public class CX10NalDecoder extends InputStream {

    private static final byte[] PARTIAL_HEADER = ByteUtils.asUnsigned(
            0x00, 0x00, 0x00, 0x19, 0xD0,
            0x02, 0x40, 0x02, 0x00, 0xBF,
            0x8A, 0x00, 0x01, 0x5D, 0x03,
            0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00, 0x00, 0x00, 0x00,
            0x00, 0x00);
    private static final byte[] PARAMS = ByteUtils.asUnsigned(0x01, 0x00, 0x00, 0x19, 0xD0, 0x02, 0x40, 0x02);
    private InputStream in;
    private byte[] savedHeader;
    private byte[] buffer;
    private int bufferReadPointer;
    private int dataAvailable;

    public CX10NalDecoder(InputStream inputStream) throws IOException {
        this.in = inputStream;
    }

    @Override
    public int read() throws IOException {
        if (buffer != null && bufferReadPointer < buffer.length) {
            return buffer[bufferReadPointer++];
        } else if (dataAvailable > 0) {
            dataAvailable--;
            return in.read();
        }

        NalHeader nalHeader = NalHeader.readNalHeader(in);
        if (nalHeader.getNalType() == 0xA0 && nalHeader.getHeaderType() == 0x03) {
            buffer = transformNalA0Header03(nalHeader.getHeaderData());
            bufferReadPointer = 0;
            dataAvailable = nalHeader.getDataLength();
        } else if (nalHeader.getNalType() == 0xA0 && nalHeader.getHeaderType() == 0x01) {
            dataAvailable = nalHeader.getDataLength();
        } else if (nalHeader.getNalType() == 0xA0 && nalHeader.getHeaderType() == 0x02) {
            dataAvailable = nalHeader.getDataLength();
        } else if (nalHeader.getNalType() == 0xA1 && nalHeader.getHeaderType() == 0x03) {
            dataAvailable = nalHeader.getDataLength();
        } else if (nalHeader.getNalType() == 0xA1 && nalHeader.getHeaderType() == 0x02) {
            savedHeader = transformNalA1Header02(nalHeader.getHeaderData());
            dataAvailable = nalHeader.getDataLength();
        } else if (nalHeader.getNalType() == 0xA1 && nalHeader.getHeaderType() == 0x01) {
            if (savedHeader != null) {
                buffer = savedHeader;
                bufferReadPointer = 0;
                savedHeader = null;
            }
            dataAvailable = nalHeader.getDataLength();
        } else {
            throw new UnknownNalHeaderException(nalHeader.getNalType(), nalHeader.getHeaderType());
        }
        return read();
    }

    public byte[] readNalPacket() throws IOException {
        NalHeader nalHeader = NalHeader.readNalHeader(in);

        if (nalHeader.getNalType() == 0xA0 && nalHeader.getHeaderType() == 0x03) {
            byte[] newHeader = transformNalA0Header03(nalHeader.getHeaderData());
            byte[] data = readData(nalHeader.getDataLength());
            return ByteBuffer.allocate(newHeader.length + data.length).put(newHeader).put(data).array();
        } else if (nalHeader.getNalType() == 0xA0 && nalHeader.getHeaderType() == 0x01) {
            return readData(nalHeader.getDataLength());
        } else if (nalHeader.getNalType() == 0xA0 && nalHeader.getHeaderType() == 0x02) {
            return readData(nalHeader.getDataLength());
        } else if (nalHeader.getNalType() == 0xA1 && nalHeader.getHeaderType() == 0x03) {
            return readData(nalHeader.getDataLength());
        } else if (nalHeader.getNalType() == 0xA1 && nalHeader.getHeaderType() == 0x02) {
            savedHeader = transformNalA1Header02(nalHeader.getHeaderData());
            return readData(nalHeader.getDataLength());
        } else if (nalHeader.getNalType() == 0xA1 && nalHeader.getHeaderType() == 0x01) {
            byte[] data = readData(nalHeader.getDataLength());
            if (savedHeader != null) {
                data = ByteBuffer.allocate(savedHeader.length + data.length).put(savedHeader).put(data).array();
                savedHeader = null;
            }
            return data;
        } else {
            throw new UnknownNalHeaderException(nalHeader.getNalType(), nalHeader.getHeaderType());
        }
    }

    private byte[] readData(int length) throws IOException {
        int read = 0;
        ByteBuffer byteBuffer = ByteBuffer.allocate(length);
        while (read < length) {
            byte[] buffer = new byte[length - read];
            int lastRead = in.read(buffer);
            if (lastRead == -1) {
                throw new EOFException();
            }
            byteBuffer.put(buffer, 0, lastRead);
            read += lastRead;
        }
        return byteBuffer.array();
    }

    private static byte[] transformNalA0Header03(byte[] fullNalHeader) throws IOException {
        // original comment: replace A003 in the header
        byte[] newHeader = new byte[32];
        System.arraycopy(PARAMS, 0, newHeader, 0, PARAMS.length);
        System.arraycopy(fullNalHeader, 12, newHeader, 8, 8);
        newHeader[16] = fullNalHeader[5];
        newHeader[18] = fullNalHeader[9];
        newHeader[19] = fullNalHeader[8];
        return newHeader;
    }

    private static byte[] transformNalA1Header02(byte[] fullNalHeader) throws IOException {
        byte[] newHeader = new byte[32];
        System.arraycopy(PARTIAL_HEADER, 0, newHeader, 0, 8);
        newHeader[8] = fullNalHeader[8];
        newHeader[9] = fullNalHeader[9];
        newHeader[10] = fullNalHeader[10];
        newHeader[11] = fullNalHeader[11];
        System.arraycopy(PARTIAL_HEADER, 12, newHeader, 12, 4);
        newHeader[16] = fullNalHeader[5];
        newHeader[17] = fullNalHeader[4];
        newHeader[18] = fullNalHeader[33];
        newHeader[19] = fullNalHeader[32];
        return newHeader;
    }

    static class NalHeader {
        int nalType;
        int sequence;
        int headerType;
        int headerSize;
        int dataLength;
        byte[] headerData;

        public NalHeader(int nalType, int sequence, int headerType, int headerSize, int dataLength, byte[] headerData) {
            this.nalType = nalType;
            this.sequence = sequence;
            this.headerType = headerType;
            this.headerSize = headerSize;
            this.dataLength = dataLength;
            this.headerData = headerData;
        }

        public static NalHeader readNalHeader(InputStream inputStream) throws IOException {
            byte[] headerData = new byte[10];
            int read = inputStream.read(headerData);
            if (read == -1) {
                throw new EOFException("No nal header data available");
            } else if (read != headerData.length) {
                throw new IllegalStateException("Failed to read the nal header data");
            }
            int nalType = headerData[3] & 0xFF;
            int sequence = headerData[5] & 0xFF;
            int headerType = headerData[7] & 0XFF;
            int headerSize = determineHeaderSize(headerType);
            int dataLength = ((headerData[9] & 0xff) << 8) | (headerData[8] & 0xff);

            byte[] fullHeaderData = new byte[headerData.length + headerSize];
            System.arraycopy(headerData, 0, fullHeaderData, 0, headerData.length);
            read = inputStream.read(fullHeaderData, 10, headerSize);
            if (read == -1) {
                throw new EOFException();
            } else if (read != headerSize) {
                throw new IllegalStateException("Failed to read the full nal header data");
            }
            return new NalHeader(nalType, sequence, headerType, headerSize, dataLength, fullHeaderData);
        }

        public int getNalType() {
            return nalType;
        }

        public int getSequence() {
            return sequence;
        }

        public int getHeaderType() {
            return headerType;
        }

        public int getHeaderSize() {
            return headerSize;
        }

        public int getDataLength() {
            return dataLength;
        }

        public byte[] getHeaderData() {
            return headerData;
        }

        private static int determineHeaderSize(int headerType) {
            switch (headerType) {
                case 0x01:
                    return 2;
                case 0x02:
                case 0x03:
                    return 30;
                default:
                    throw new UnknownNalHeaderException(headerType);
            }
        }
    }

    static class UnknownNalHeaderException extends IllegalArgumentException {
        public UnknownNalHeaderException(int headerType) {
            super("Unknown header type " + headerType);
        }

        public UnknownNalHeaderException(int nalType, int headerType) {
            super("Unknown combination of nal type " + nalType + " and header type " + headerType);
        }
    }
}
