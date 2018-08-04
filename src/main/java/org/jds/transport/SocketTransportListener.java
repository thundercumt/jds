package org.jds.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.jds.protocol.Delimiter;

public class SocketTransportListener implements TransportListener {
    private ServerSocketChannel ch;
    private Selector selector;
    private ExecutorService executor;
    private Delimiter delimiter;
    private CompleteHandler handler;

    public static class SocketTransportListenerBuilder {
        private SocketTransportListener obj = new SocketTransportListener();

        public SocketTransportListener build() {
            if (obj.executor == null)
                throw new IllegalArgumentException("null executor");
            if (obj.ch == null)
                throw new IllegalArgumentException("null channel");
            if (obj.delimiter == null)
                throw new IllegalArgumentException("null delimiter");
            return obj;
        }

        public SocketTransportListenerBuilder listenOn(InetAddress addr, int port) {
            try {
                obj.selector = Selector.open();
                obj.ch = ServerSocketChannel.open();
                obj.ch.bind(new InetSocketAddress(addr, port));
                return this;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public SocketTransportListenerBuilder listenOn(InetAddress addr, int port, int backlog) {
            try {
                obj.selector = Selector.open();
                obj.ch = ServerSocketChannel.open();
                obj.ch.bind(new InetSocketAddress(addr, port), backlog);
                return this;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public SocketTransportListenerBuilder listenOn(String host, int port, int backlog) {
            try {
                obj.selector = Selector.open();
                obj.ch = ServerSocketChannel.open();
                InetAddress addr = InetAddress.getByName(host);
                obj.ch.bind(new InetSocketAddress(addr, port), backlog);
                return this;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public SocketTransportListenerBuilder executor(ExecutorService exec) {
            obj.executor = exec;
            return this;
        }

        public SocketTransportListenerBuilder delimiter(Delimiter del) {
            obj.delimiter = del;
            return this;
        }

        public SocketTransportListenerBuilder handler(CompleteHandler hdl) {
            obj.handler = hdl;
            return this;
        }
    }

    private SocketTransportListener() {
    }

    @Override
    public void listen() {
        try {
            ch.configureBlocking(false);
            ch.register(selector, SelectionKey.OP_ACCEPT);
            AccepterTask accepter = new AccepterTask(this, selector);
            executor.submit(accepter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Future<?> execute(Runnable task) {
        return executor.submit(task);
    }

    @Override
    public void onConnect(Transport transport) throws IOException {
        SocketTransport st = (SocketTransport) transport;
        st.channel().configureBlocking(false);
        st.channel().register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }

    @Override
    public void interrupt() {
        this.selector.wakeup();
    }

    static class AccepterTask extends SelectorTask {

        private final SocketTransportListener listener;

        public AccepterTask(SocketTransportListener listener, Selector selector) {
            super(selector);
            this.listener = listener;
        }

        @Override
        public void run() {
            while (!isCanceled()) {
                int n = 0;
                try {
                    n = selector.select();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if (n == 0)
                    continue;

                List<SocketChannel> ready = new LinkedList<>();
                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iter = keys.iterator();
                while (iter.hasNext()) {
                    SelectionKey key = iter.next();
                    iter.remove();
                    // key.cancel();
                    if (key.isValid() && key.isAcceptable()) {
                        SocketChannel sc = null;
                        try {
                            sc = ((ServerSocketChannel) key.channel()).accept();
                            ready.add(sc);
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                            continue;
                        }
                    }
                }

                listener.execute(new Runnable() {
                    @Override
                    public void run() {
                        for (SocketChannel ch : ready) {
                            SocketTransport st = new SocketTransport.SocketTransportBuilder().channel(ch).buffer(1024)
                                    .delimiter(listener.delimiter).completeHandler(listener.handler).build();
                            try {
                                listener.onConnect(st);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        }

    }
}
