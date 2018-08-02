package org.jds.transport;

import java.io.IOException;

public interface TransportListener {
    void onConnect(Transport transport) throws IOException;
    void listen() throws IOException;
    void interrupt();
}
