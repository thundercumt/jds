package org.jds.util;

import java.nio.ByteBuffer;

public class ByteBuffers {
    public static String toString(ByteBuffer  bf) {
        byte[] bytes = new byte[bf.remaining()];
        bf.get(bytes);
        return new String(bytes);
    }
}
