package org.jds.transport;

import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ReaderTask extends SelectorTask {
    private final Channel ch;

    public ReaderTask(SocketChannel channel, Selector selector) {
        super(selector);
        this.ch = channel;
        if (!channel.isRegistered()) {
            try {
                channel.register(selector, SelectionKey.OP_READ);
            } catch (ClosedChannelException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while (!isCanceled()) {
            int n = 0;
            try {
                n = selector.select();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (n == 0)
                continue;

            List<SocketChannel> ready = new LinkedList<>();
            Set<SelectionKey> keys = selector.selectedKeys();
            Iterator<SelectionKey> iter = keys.iterator();
            while(iter.hasNext()) {
                SelectionKey key = iter.next();
                if (key.isValid() && key.isReadable()) {
                    iter.remove();
                    //key.cancel();
                    SocketChannel ch = (SocketChannel) key.channel();
                    SocketTransport st = ((SocketTransport) key.attachment());
                    try {
                        int r = ch.read(st.readBuffer());
                        
                        if (r==-1) {
                            key.un
                        }
                    }
                }
                
            }
        }

    }

}
