package org.jds.protocol;

import java.nio.ByteBuffer;

public interface Delimiter {
    int complete(ByteBuffer bf);
}
