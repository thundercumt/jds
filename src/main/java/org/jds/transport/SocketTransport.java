package org.jds.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.jds.protocol.Delimiter;

public class SocketTransport implements Transport {
    private SocketChannel ch;
    private ByteBuffer rb;
    private ByteBuffer wb;
    private Delimiter dl;
    private CompleteHandler handler;

    public static class SocketTransportBuilder {
        private SocketTransport st = new SocketTransport();

        public SocketTransport build() {
            return st;
        }

        public SocketTransportBuilder connect(String host, int port) {
            try {
                st.ch = SocketChannel.open(new InetSocketAddress(host, port));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return this;
        }

        public SocketTransportBuilder connect(InetAddress addr, int port) {
            try {
                st.ch = SocketChannel.open(new InetSocketAddress(addr, port));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return this;
        }
        
        public SocketTransportBuilder channel(SocketChannel ch) {
            st.ch = ch;
            return this;
        }

        public SocketTransportBuilder buffer(int n) {
            return buffer(n, false);
        }

        public SocketTransportBuilder buffer(int n, boolean direct) {
            if (direct) {
                st.rb = ByteBuffer.allocateDirect(n);
                st.wb = ByteBuffer.allocateDirect(n);
            } else {
                st.rb = ByteBuffer.allocate(n);
                st.wb = ByteBuffer.allocate(n);
            }
            return this;
        }

        public SocketTransportBuilder buffer(int nr, int nw, boolean direct) {
            if (direct) {
                st.rb = ByteBuffer.allocateDirect(nr);
                st.wb = ByteBuffer.allocateDirect(nw);
            } else {
                st.rb = ByteBuffer.allocate(nr);
                st.wb = ByteBuffer.allocate(nw);
            }
            return this;
        }

        public SocketTransportBuilder delimiter(Delimiter delimiter) {
            st.dl = delimiter;
            return this;
        }

        public SocketTransportBuilder completeHandler(CompleteHandler handler) {
            st.handler = handler;
            return this;
        }
    }

    private SocketTransport() {
    }

    public SocketChannel channel() {
        return ch;
    }

    public ByteBuffer readBuffer() {
        return rb;
    }

    public ByteBuffer writeBuffer() {
        return wb;
    }

    public Delimiter delimiter() {
        return dl;
    }

    @Override
    public int write(byte[] buf, int offset, int len) throws IOException {
        return ch.write(ByteBuffer.wrap(buf, offset, len));
    }

    public int write(byte[] buf, int offset) throws IOException {
        return ch.write(ByteBuffer.wrap(buf, offset, buf.length));
    }
    
    public int write(byte[] buf) throws IOException {
        return ch.write(ByteBuffer.wrap(buf, 0, buf.length));
    }

    public int write(ByteBuffer bf) throws IOException {
        return ch.write(bf);
    }

    @Override
    public int read(byte[] buf, int offset, int len) throws IOException {
        return ch.read(ByteBuffer.wrap(buf, offset, len));
    }

    public void handle(ByteBuffer bf, int len) {
        handler.handle(this, bf, len);
    }
}
