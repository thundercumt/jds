package org.jds.app;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;

import org.jds.protocol.LineDelimiter;
import org.jds.transport.CompleteHandler;
import org.jds.transport.SocketTransport;
import org.jds.transport.SocketTransportListener;
import org.jds.transport.Transport;
import org.jds.util.ByteBuffers;

public class EchoServer {
    
    static class EchoHandler implements CompleteHandler {
        @Override
        public void handle(Transport tr, ByteBuffer bf, int len) {
            SocketTransport st = (SocketTransport) tr;
            try {
                String msg = ByteBuffers.toString(bf);
                System.out.println(String.format(">>>received %s\n", msg));
                st.write(bf);
                bf.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws IOException {
        CompleteHandler echo = new EchoHandler();
        SocketTransportListener listener = new SocketTransportListener.SocketTransportListenerBuilder()
                .listenOn("127.0.0.1", 17788, 800).executor(Executors.newScheduledThreadPool(200))
                .delimiter(new LineDelimiter("\r\n")).handler(echo).build();
        listener.listen();
        System.in.read();
    }
}
