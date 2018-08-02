package org.jds.transport;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SocketTransportListener implements TransportListener {
    private ServerSocket s;
    private ServerSocketChannel ch;
    private Selector selector = Selector.open();
    private ExecutorService executor;

    public SocketTransportListener(InetAddress addr, int port, int tp) throws IOException {
        s = new ServerSocket(port, 9999, addr);
        ch = s.getChannel();
        executor = Executors.newScheduledThreadPool(tp);
    }

    public SocketTransportListener(InetAddress addr, int port, int backlog, int tp) throws IOException {
        s = new ServerSocket(port, backlog, addr);
        ch = s.getChannel();
        executor = Executors.newScheduledThreadPool(tp);
    }

    @Override
    public void listen() {
        try {
            ch.register(selector, SelectionKey.OP_ACCEPT);
            AccepterTask accepter = new AccepterTask(this, selector);
            executor.submit(accepter);
        } catch (ClosedChannelException e) {
            e.printStackTrace();
        }
    }

    public Future<?> execute(Runnable task) {
        return executor.submit(task);
    }

    @Override
    public void onConnect(Transport transport) throws IOException {
        SocketTransport st = (SocketTransport) transport;
        st.channel().register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
    }

    @Override
    public void interrupt() {
        this.selector.wakeup();
    }

    static class batchConnectTask implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub

        }

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
                    key.cancel();
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
                            SocketTransport st = new SocketTransport.SocketTransportBuilder().channel(ch).buffer(1024).build();
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
