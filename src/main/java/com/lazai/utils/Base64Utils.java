package com.lazai.utils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

/**
 * @author yozora
 * description Base64
 **/
public class Base64Utils {

    // base64 character table
    private static final char[] BASE64_MAP =
            {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
                    'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a',
                    'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                    'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0',
                    '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};

    /**
     * convert the platform dependent string characters to UTF8 which can also
     * be done by calling the java String method getBytes("UTF-8"),but I hope to
     * do it from the ground up.
     */

    private static byte[] toUTF8ByteArray(String s) {
        int ichar;
        byte[] buffer = new byte[3 * (s.length())];
        byte[] hold;
        int index = 0;
        // count the actual bytes in the
        int count = 0;
        // buffer array
        for (int i = 0; i < s.length(); i++) {
            ichar = (int) s.charAt(i);

            // determine the bytes for a specific character
            if ((ichar >= 0x0080) & (ichar <= 0x07FF)) {
                buffer[index++] = (byte) ((6 << 5) | ((ichar >> 6) & 31));
                buffer[index++] = (byte) ((2 << 6) | (ichar & 63));
                count += 2;
            }

            // determine the bytes for a specific character
            else if (ichar >= 0x0800) {
                buffer[index++] = (byte) ((14 << 4) | ((ichar >> 12) & 15));
                buffer[index++] = (byte) ((2 << 6) | ((ichar >> 6) & 63));
                buffer[index++] = (byte) ((2 << 6) | (ichar & 63));
                count += 3;
            }

            // determine the bytes for a specific character
            else {
                buffer[index++] = (byte) ((0) | (ichar & 127));
                count += 1;
            }
        }
        hold = new byte[count];
        // trim to size
        System.arraycopy(buffer, 0, hold, 0, count);
        return hold;
    }

    /**
     * encode
     *
     * @param encodeContent encodeContent
     * @return encode data
     */
    public static String encode(String encodeContent) {
        byte[] buf = toUTF8ByteArray(encodeContent);
        return encode(buf);
    }

    /**
     * encode
     *
     * @param encodeContentBuf encodeContent
     * @return encode data
     */
    public static String encode(byte[] encodeContentBuf) {
        StringBuilder sb = new StringBuilder();
        String padder = "";

        if (encodeContentBuf.length == 0) {
            return "";
        }

        // cope with less than 3 bytes conditions at the end of encodeContentBuf

        switch (encodeContentBuf.length % 3) {
            case 1: {
                padder += BASE64_MAP[((encodeContentBuf[encodeContentBuf.length - 1] >>> 2) & 63)];
                padder += BASE64_MAP[((encodeContentBuf[encodeContentBuf.length - 1] << 4) & 63)];
                padder += "==";
                break;
            }
            case 2: {
                padder += BASE64_MAP[(encodeContentBuf[encodeContentBuf.length - 2] >>> 2) & 63];
                padder += BASE64_MAP[(((encodeContentBuf[encodeContentBuf.length - 2] << 4) & 63))
                        | (((encodeContentBuf[encodeContentBuf.length - 1] >>> 4) & 63))];
                padder += BASE64_MAP[(encodeContentBuf[encodeContentBuf.length - 1] << 2) & 63];
                padder += "=";
                break;
            }
            default:
                break;
        }

        int temp = 0;
        int index = 0;

        // encode encodeContentBuf.length-encodeContentBuf.length%3 bytes which must be a multiply of 3

        for (int i = 0; i < (encodeContentBuf.length - (encodeContentBuf.length % 3)); ) {
            // get three bytes and encode them to four base64 characters
            temp = ((encodeContentBuf[i++] << 16) & 0xFF0000) | ((encodeContentBuf[i++] << 8) & 0xFF00)
                    | (encodeContentBuf[i++] & 0xFF);
            index = (temp >> 18) & 63;
            sb.append(BASE64_MAP[index]);
            // a Base64 encoded line is no longer than
            if (sb.length() % 76 == 0)
            // 76 characters
            {
                sb.append('\n');
            }

            index = (temp >> 12) & 63;
            sb.append(BASE64_MAP[index]);
            if (sb.length() % 76 == 0) {
                sb.append('\n');
            }

            index = (temp >> 6) & 63;
            sb.append(BASE64_MAP[index]);
            if (sb.length() % 76 == 0) {
                sb.append('\n');
            }

            index = temp & 63;
            sb.append(BASE64_MAP[index]);
            if (sb.length() % 76 == 0) {
                sb.append('\n');
            }
        }

        // add the remaining one or two bytes
        sb.append(padder);
        return sb.toString();
    }

    /**
     * decode
     *
     * @param decodeContent decodeContent
     * @return decode data
     */
    public static String decode(String decodeContent) {

        byte[] buf;
        try {
            buf = decodeToByteArray(decodeContent);
            assert buf != null;
            decodeContent = new String(buf, StandardCharsets.UTF_8);
            decodeContent = decodeContent.replaceAll("[\\n|\\r]", "");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            return "";
        }
        return decodeContent;
    }

    /**
     * decode
     *
     * @param decodeContent decodeContent
     * @return decode data
     */
    public static byte[] decodeToByteArray(String decodeContent) throws UnsupportedEncodingException {
        byte[] hold;

        if (decodeContent.isEmpty()) {
            return null;
        }
        byte[] buf = decodeContent.getBytes(StandardCharsets.ISO_8859_1);
        byte[] debuf = new byte[buf.length * 3 / 4];
        byte[] tempBuf = new byte[4];
        int index = 0;
        int index1 = 0;
        int temp;
        int count = 0;
        int count1 = 0;

        // decode to byte array
        for (byte b : buf) {
            if (b >= 65 && b < 91) {
                tempBuf[index++] = (byte) (b - 65);
            } else if (b >= 97 && b < 123) {
                tempBuf[index++] = (byte) (b - 71);
            } else if (b >= 48 && b < 58) {
                tempBuf[index++] = (byte) (b + 4);
            } else if (b == '+') {
                tempBuf[index++] = 62;
            } else if (b == '/') {
                tempBuf[index++] = 63;
            } else if (b == '=') {
                tempBuf[index++] = 0;
                count1++;
            }

            // Discard line breaks and other non-significant characters
            else {
                if (b == '\n' || b == '\r' || b == ' '
                        || b == '\t') {
                    continue;
                } else {
                    throw new RuntimeException(
                            "Illegal character found in encoded string!");
                }
            }
            if (index == 4) {
                temp = ((tempBuf[0] << 18)) | ((tempBuf[1] << 12))
                        | ((tempBuf[2] << 6)) | (tempBuf[3]);
                debuf[index1++] = (byte) (temp >> 16);
                debuf[index1++] = (byte) ((temp >> 8) & 255);
                debuf[index1++] = (byte) (temp & 255);
                count += 3;
                index = 0;
            }
        }
        hold = new byte[count - count1];
        // trim to size
        System.arraycopy(debuf, 0, hold, 0, count - count1);
        return hold;
    }
}
