package com.uaepay.gateway.cgs.app.service.common;

import java.io.IOException;

/**
 * <p>描述</p>
 *
 * @author Yadong
 * @version $ Id: FastBase64, v 0.1 2019/12/1 ranber Exp $
 */
public class FastBase64 {

    private static final byte[] encodingTable = new byte[64];
    private static final byte[] decodingTable = new byte[256];
    private static final boolean[] skippingTable = new boolean[256];
    private static byte padding = 61;

    private FastBase64() {
    }

    public static final byte[] encode(byte[] data) {
        if (data != null && data.length != 0) {
            byte[] out = new byte[(data.length + 2) / 3 * 4];
            int remain = data.length % 3;
            int dataLength = data.length - remain;
            int ipx = 0;

            int var5;
            int a1;
            int a2;
            int a3;
            for(var5 = 0; ipx < dataLength; out[var5++] = encodingTable[63 & a3]) {
                a1 = 255 & data[ipx++];
                a2 = 255 & data[ipx++];
                a3 = 255 & data[ipx++];
                out[var5++] = encodingTable[63 & a1 >>> 2];
                out[var5++] = encodingTable[63 & (a1 << 4 | a2 >>> 4)];
                out[var5++] = encodingTable[63 & (a2 << 2 | a3 >>> 6)];
            }

            switch(remain) {
                case 0:
                default:
                    break;
                case 1:
                    a1 = 255 & data[ipx++];
                    out[var5++] = encodingTable[63 & a1 >>> 2];
                    out[var5++] = encodingTable[63 & a1 << 4];
                    out[var5++] = padding;
                    out[var5++] = padding;
                    break;
                case 2:
                    a1 = 255 & data[ipx++];
                    a2 = 255 & data[ipx++];
                    out[var5++] = encodingTable[63 & a1 >>> 2];
                    out[var5++] = encodingTable[63 & (a1 << 4 | a2 >>> 4)];
                    out[var5++] = encodingTable[63 & a2 << 2];
                    out[var5++] = padding;
            }

            return out;
        } else {
            return new byte[0];
        }
    }

    public static final byte[] decode(byte[] data) throws IOException {
        if (data != null && data.length != 0) {
            FastBase64.LastBlock lastBlock = findLastBlock(data);
            int finish = lastBlock.finish;
            byte[] buffer = new byte[(data.length * 3 + 3) / 4];
            int ipx = 0;
            int opx = 0;

            for(ipx = nextI(data, ipx, finish); ipx < finish; ipx = nextI(data, ipx, finish)) {
                byte b1 = decodingTable[data[ipx++]];
                ipx = nextI(data, ipx, finish);
                byte b2 = decodingTable[data[ipx++]];
                ipx = nextI(data, ipx, finish);
                byte b3 = decodingTable[data[ipx++]];
                ipx = nextI(data, ipx, finish);
                byte b4 = decodingTable[data[ipx++]];
                if ((b1 | b2 | b3 | b4) < 0) {
                    throw new IOException("invalid characters encountered in base64 data");
                }

                buffer[opx++] = (byte)(b1 << 2 | b2 >> 4);
                buffer[opx++] = (byte)(b2 << 4 | b3 >> 2);
                buffer[opx++] = (byte)(b3 << 6 | b4);
            }

            opx += decodeLastBlock(buffer, opx, lastBlock.c1, lastBlock.c2, lastBlock.c3, lastBlock.c4);
            byte[] out = new byte[opx];
            System.arraycopy(buffer, 0, out, 0, out.length);
            return out;
        } else {
            return new byte[0];
        }
    }

    private static int decodeLastBlock(byte[] out, int opx, int c1, int c2, int c3, int c4) throws IOException {
        byte b1;
        byte b2;
        if (c3 == padding) {
            b1 = decodingTable[c1];
            b2 = decodingTable[c2];
            if ((b1 | b2) < 0) {
                throw new IOException("invalid characters encountered at end of base64 data");
            } else {
                out[opx++] = (byte)(b1 << 2 | b2 >>> 4);
                return 1;
            }
        } else {
            byte b3;
            if (c4 == padding) {
                b1 = decodingTable[c1];
                b2 = decodingTable[c2];
                b3 = decodingTable[c3];
                if ((b1 | b2 | b3) < 0) {
                    throw new IOException("invalid characters encountered at end of base64 data");
                } else {
                    out[opx++] = (byte)(b1 << 2 | b2 >>> 4);
                    out[opx++] = (byte)(b2 << 4 | b3 >>> 2);
                    return 2;
                }
            } else {
                b1 = decodingTable[c1];
                b2 = decodingTable[c2];
                b3 = decodingTable[c3];
                byte b4 = decodingTable[c4];
                if ((b1 | b2 | b3 | b4) < 0) {
                    throw new IOException("invalid characters encountered at end of base64 data");
                } else {
                    out[opx++] = (byte)(b1 << 2 | b2 >>> 4);
                    out[opx++] = (byte)(b2 << 4 | b3 >>> 2);
                    out[opx++] = (byte)(b3 << 6 | b4);
                    return 3;
                }
            }
        }
    }

    private static int nextI(byte[] data, int ipx, int finish) throws IOException {
        while(ipx < finish && skippingTable[data[ipx]]) {
            ++ipx;
        }

        return ipx;
    }

    private static FastBase64.LastBlock findLastBlock(byte[] data) throws IOException {
        int end;
        for(end = data.length; end > 0 && skippingTable[data[end - 1]]; --end) {
        }

        int[] lastBlock = new int[4];
        int num = 0;
        --end;

        while(end >= 0 && num < 4) {
            int value = 255 & data[end];
            if (skippingTable[value]) {
                --end;
            } else {
                if (value != 61 && decodingTable[value] < 0) {
                    throw new IOException("invalid characters encountered in base64 data: " + value);
                }

                lastBlock[num++] = (byte)value;
                --end;
            }
        }

        ++end;
        FastBase64.LastBlock LastBlock = new FastBase64.LastBlock(lastBlock, end);
        return LastBlock;
    }

    static {
        int ipx = 0;

        char c;
        for(c = 'A'; c <= 'Z'; ++c) {
            encodingTable[ipx++] = (byte)c;
        }

        for(c = 'a'; c <= 'z'; ++c) {
            encodingTable[ipx++] = (byte)c;
        }

        for(c = '0'; c <= '9'; ++c) {
            encodingTable[ipx++] = (byte)c;
        }

        encodingTable[ipx++] = 43;
        encodingTable[ipx++] = 47;

        int i;
        for(i = 0; i < decodingTable.length; ++i) {
            decodingTable[i] = -1;
        }

        for(i = 0; i < encodingTable.length; ++i) {
            decodingTable[encodingTable[i]] = (byte)i;
        }

        for(i = 0; i < skippingTable.length; ++i) {
            skippingTable[i] = false;
        }

        skippingTable[10] = true;
        skippingTable[13] = true;
        skippingTable[9] = true;
        skippingTable[32] = true;
    }

    private static class LastBlock {
        final int c1;
        final int c2;
        final int c3;
        final int c4;
        final int finish;

        LastBlock(int[] lastBlock, int finish) {
            this.finish = finish;
            this.c4 = lastBlock[0];
            this.c3 = lastBlock[1];
            this.c2 = lastBlock[2];
            this.c1 = lastBlock[3];
        }
    }

}
