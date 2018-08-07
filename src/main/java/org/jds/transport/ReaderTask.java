package org.jds.transport;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ReaderTask extends SelectorTask {
    private final SocketTransport st;

    public ReaderTask(SocketTransport transport, Selector selector) {
        super(selector);
        this.st = transport;
        if (!st.channel().isRegistered()) {
            try {
                st.channel().configureBlocking(false);
                st.channel().register(selector, SelectionKey.OP_READ, transport);
            } catch (IOException e) {
                e.printStackTrace(System.err);
            }
        }
    }

    @Override
    public void run() {
        while (!isCanceled()) {
            int n = 0;
            try {
                n = selector.select();
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (n == 0)
                continue;

            List<SocketChannel> ready = new LinkedList<>();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iter = keys.iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                if (key.isValid() && key.isReadable()) {
                    iter.remove();
                    // key.cancel();
                    SocketChannel ch = (SocketChannel) key.channel();
                    SocketTransport st = ((SocketTransport) key.attachment());
                    ByteBuffer rb = st.readBuffer();
                    try {
                        int r = ch.read(rb);
                        if (r == -1) {
                            key.cancel();
                            continue;
                        }
                        rb.flip();
                        int end = st.delimiter().complete(rb);
                        if (end != -1)
                            st.handle(rb, end);
                        else {
                            rb.position(rb.limit());
                            rb.limit(rb.capacity());
                        }
                    } catch (Exception e) {
                        key.cancel();
                        e.printStackTrace();
                    }
                }

            }
        }

    }

}
