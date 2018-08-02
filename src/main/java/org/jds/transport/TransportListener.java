package org.jds.transport;

public interface TransportListener {
    Transport onConnect();
    void listen();
}
