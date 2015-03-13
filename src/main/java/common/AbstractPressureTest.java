package common;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractPressureTest<Result extends FutureResult> {
    protected ExecutorService executorService;
    protected int cycleTimes;
    protected AtomicInteger executedCount = new AtomicInteger(1);

    public AbstractPressureTest(int poolSize, int cycleTimes) {
        this.executorService = Executors.newFixedThreadPool(poolSize);
        this.cycleTimes = cycleTimes;
    }

    public void execute() {
        try {
            Collection<Callable<Result>> tasks = getTasks();
            List<Future<Result>> futures = executorService.invokeAll(tasks);
            for (Future<Result> future : futures) {
                Result result = future.get();
                handleResult(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void handleResult(Result result) {

    }

    protected abstract Collection<Callable<Result>> getTasks();
}
