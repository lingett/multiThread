package main;

import common.AbstractPressureTest;
import common.BlockingQueueCallable;
import common.MultiBlockingQueueCallable;
import common.PressureTest;
import entity.FutureResult;
import main.task.MovieFansInfoServiceTask;

import java.util.concurrent.*;

public class MovieFansInfoServicePressureTest extends AbstractPressureTest<FutureResult> {
    public MovieFansInfoServicePressureTest(int poolSize, int cycleTimes) {
        super(poolSize, cycleTimes);
    }

    @Override
    protected void generateTask(BlockingQueue<Callable<FutureResult>> taskQueue) {
        for (int i = 1; i <= 20; i++) {
            try {
                taskQueue.put(new MovieFansInfoServiceTask(i));
                getProducedCount().incrementAndGet();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String... args) {
        int poolSize = 100;
        int cycleTimes = 3;
        PressureTest instance = new MovieFansInfoServicePressureTest(poolSize, cycleTimes);
        instance.execute();
        System.out.println("end");
    }
}
