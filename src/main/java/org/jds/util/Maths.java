package org.jds.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Maths {
    public static long toLong(ByteBuffer bf, int offset, int len) {
        long r = 0;
        if (bf.order() == ByteOrder.BIG_ENDIAN) {
            for (int i = offset, j = offset + len; i < j; ++i) {
                r = r * 256 + bf.get(i);
            }
        }
        else {
            for (int i = offset + offset - 1, j = offset; i >= j; --i) {
                r = r * 256 + bf.get(i);
            }
        }
        return r;
    }
}
