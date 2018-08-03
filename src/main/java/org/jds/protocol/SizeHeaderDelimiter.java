package org.jds.protocol;

import java.nio.ByteBuffer;

import org.jds.util.Maths;

public class SizeHeaderDelimiter implements Delimiter {
    private int offset;
    private int len;

    public SizeHeaderDelimiter(int offset, int len) {
        this.offset = offset;
        this.len = len;
    }

    @Override
    public int complete(ByteBuffer bf) {
        if (bf.limit() >= offset + len) {
            long size = Maths.toLong(bf, offset, len);
            if (bf.limit() >= size) return (int) size;
        }
        return -1;
    }
    
}
