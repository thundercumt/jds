package org.jds.transport;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TransportExecutor {
    ExecutorService executor;

    public TransportExecutor(int tp) {
        executor = Executors.newScheduledThreadPool(tp);
    }

    private Future<?> execute(Runnable task) {
        return executor.submit(task);
    }

}
