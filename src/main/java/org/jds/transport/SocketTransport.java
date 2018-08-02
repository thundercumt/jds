package org.jds.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.ByteBuffer;

public class SocketTransport implements Transport {
    private Socket s;

    public SocketTransport(InetAddress addr, int port) throws IOException {
        s = new Socket(addr, port);
    }

    public SocketTransport(String host, int port) throws IOException {
        s = new Socket(host, port);
    }

    @Override
    public int write(byte[] buf, int offset, int len) throws IOException {
        return s.getChannel().write(ByteBuffer.wrap(buf, offset, len));
    }

    @Override
    public int read(byte[] buf, int offset, int len) throws IOException {
        return s.getChannel().read(ByteBuffer.wrap(buf, offset, len));
    }
}
