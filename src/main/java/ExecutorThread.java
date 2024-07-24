import org.slf4j.Logger;

import java.net.Socket;
import java.util.concurrent.BlockingQueue;

public class ExecutorThread extends Thread{
    private Logger logger;
    private BlockingQueue<Runnable> taskQueue;
    public  volatile boolean running = true;
    public ExecutorThread( Logger logger, BlockingQueue<Runnable> taskQueue) {
        this.logger = logger;
        this.taskQueue = taskQueue;
    }
    @Override
    public void run() {
        while(running) {
            try {
                Runnable task = taskQueue.take();
                 task.run();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }






}
