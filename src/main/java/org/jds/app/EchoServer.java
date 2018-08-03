package org.jds.app;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;

import org.jds.transport.SocketTransportListener;

public class EchoServer {

    public static void main(String[] args) throws IOException {
        SocketTransportListener listener = new SocketTransportListener("localhost", 17788, 500, 200);
        listener.listen();
        System.in.read();
    }
    
    public static void main2(String[] args) throws IOException {
        Selector s = Selector.open();
        ServerSocketChannel ch = ServerSocketChannel.open();
        ch.bind(new InetSocketAddress("localhost", 17788), 200);
        ch.configureBlocking(false);
        System.in.read();
    }

}
