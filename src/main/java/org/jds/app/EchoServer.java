package org.jds.app;

import java.io.IOException;
import java.util.concurrent.Executors;

import org.jds.protocol.LineDelimiter;
import org.jds.transport.SocketTransportListener;

public class EchoServer {

    public static void main(String[] args) throws IOException {
        SocketTransportListener listener = new SocketTransportListener.SocketTransportListenerBuilder()
                .listenOn("localhost", 17788, 800).executor(Executors.newScheduledThreadPool(200))
                .delimiter(new LineDelimiter("\r\n")).build();
        listener.listen();
        System.in.read();
    }
}
