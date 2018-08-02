package org.jds.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

public class SocketTransportListener implements TransportListener {
    private ServerSocket s;
    private ServerSocketChannel ch;
    private Selector selector = Selector.open();

    public SocketTransportListener(InetAddress addr, int port) throws IOException {
        s = new ServerSocket(port, 9999, addr);
        ch = s.getChannel();
    }

    public SocketTransportListener(InetAddress addr, int port, int backlog) throws IOException {
        s = new ServerSocket(port, backlog, addr);
        ch = s.getChannel();
    }

    @Override
    public void listen() {
        try {
            ch.register(selector, SelectionKey.OP_ACCEPT);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
    }

    public void loop() throws IOException {
        selector.select();
    }

    @Override
    public Transport onConnect() {
        // TODO Auto-generated method stub
        return null;
    }

}
