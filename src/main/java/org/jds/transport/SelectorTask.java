package org.jds.transport;

import java.nio.channels.Selector;

public abstract class SelectorTask implements Runnable {
    protected Selector selector;
    private volatile boolean canceled = false;

    protected boolean isCanceled() {
        return canceled;
    }

    public void cancel() {
        canceled = true;
        selector.wakeup();
    }

    public SelectorTask(Selector selector) {
        this.selector = selector;
    }

}
