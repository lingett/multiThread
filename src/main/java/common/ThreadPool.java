package common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadPool {
    protected ExecutorService executorService;

    public ThreadPool(int poolSize) {
        executorService = Executors.newFixedThreadPool(poolSize);
    }
}
