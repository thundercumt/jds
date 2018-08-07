package org.jds.app;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executors;

import org.jds.app.EchoServer.EchoHandler;
import org.jds.protocol.LineDelimiter;
import org.jds.transport.CompleteHandler;
import org.jds.transport.SocketTransport;
import org.jds.transport.Transport;

public class EchoClient implements CompleteHandler {

    @Override
    public void handle(Transport tr, ByteBuffer bf, int len) {
        SocketTransport st = (SocketTransport) tr;
        try {
            st.write(bf);
            bf.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        CompleteHandler echo = new EchoHandler();
        SocketTransport connector = new SocketTransport.SocketTransportBuilder().connect("127.0.0.1", 17788).buffer(512)
                .completeHandler(echo).delimiter(new LineDelimiter("\r\n")).build();
        connector.write("hello world\r\n".getBytes());
        System.in.read();
    }
}
