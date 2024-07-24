import org.slf4j.Logger;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ExecutorPool {
    private List<ExecutorThread> executors;

    private BlockingQueue<Runnable> taskQueue;
    private Logger logger;
    private int current = 0;
    private int cap;

    public ExecutorPool(int cap, Logger logger) {
        this.cap = cap;
        executors = new ArrayList<>();
        this.logger = logger;
        this.taskQueue = new LinkedBlockingQueue<>();
        for(int i = 0; i < cap; i++) {
            ExecutorThread thread = new ExecutorThread(logger, taskQueue);
            thread.start();
            executors.add(thread);
        }
    }


    public void handleTask(Socket clientSocket, MemoryMap map) {
        ClientSocketHandler handler = new ClientSocketHandler(clientSocket, this.logger, map);
        taskQueue.add(handler);

    }
}
