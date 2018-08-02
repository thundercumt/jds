package org.jds.transport;

import java.nio.ByteBuffer;

public interface SocketPacketDelimiter {
    boolean isComplete(ByteBuffer bf);
}
