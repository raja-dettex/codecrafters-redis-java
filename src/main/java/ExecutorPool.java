import org.slf4j.Logger;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ExecutorPool {
    private List<ExecutorThread> executors;

    private BlockingQueue<Runnable> taskQueue;

    private MemoryMap map;
    private Logger logger;
    private int current = 0;
    private int cap;

    public ExecutorPool(int cap, Logger logger, MemoryMap map) {
        this.cap = cap;
        executors = new ArrayList<>();
        this.logger = logger;
        this.map = map;
        this.taskQueue = new LinkedBlockingQueue<>();
        for(int i = 0; i < cap; i++) {
            ExecutorThread thread = new ExecutorThread(logger, taskQueue);
            thread.start();
            executors.add(thread);
        }
    }

    public void handleCacheValidation() {
        Runnable validationTask = new Runnable() {
            @Override
            public void run() {
                try {
                    map.EvictKeys();
                } catch (InterruptedException e) {
                    logger.error(e.getMessage());
                }
            }
        };
        taskQueue.add(validationTask);
    }

    public void handleTask(Socket clientSocket) {
        ClientSocketHandler handler = new ClientSocketHandler(clientSocket, this.logger, this.map);
        taskQueue.add(handler);

    }
}
