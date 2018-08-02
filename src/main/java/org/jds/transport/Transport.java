package org.jds.transport;

import java.io.IOException;

public interface Transport {
    int write(byte[] buf, int offset, int len) throws IOException;
    int read(byte[] buf, int offset, int len) throws IOException;
}
