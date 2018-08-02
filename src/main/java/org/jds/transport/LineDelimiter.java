package org.jds.transport;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;

public class LineDelimiter implements SocketPacketDelimiter {

    @Override
    public boolean isComplete(ByteBuffer bf) {
        CharBuffer cb = bf.asCharBuffer();
        for (int i = 0; i < cb.limit(); ++i)
            if (cb.get(i) == '\n')
                return true;
        return false;
    }
}
