package org.jds.transport;

import java.nio.ByteBuffer;

public interface CompleteHandler {
    void handle(Transport tr, ByteBuffer bf, int len);
}
