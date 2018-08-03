package org.jds.transport;

import java.nio.ByteBuffer;

public interface CompleteHandler {
    void handle(ByteBuffer bf, int len);
}
