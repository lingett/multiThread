package main;

import common.AbstractPressureTest;
import common.FutureResult;
import main.task.MovieFansInfoServiceTask;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.Callable;

public class MovieFansInfoServicePressureTest extends AbstractPressureTest<FutureResult> {
    public MovieFansInfoServicePressureTest(int poolSize, int cycleTimes) {
        super(poolSize, cycleTimes);
    }

    @Override
    protected Collection<Callable<FutureResult>> getTasks() {
        Collection<Callable<FutureResult>> result = new ArrayList<Callable<FutureResult>>();
        for (int i = 1; i <= 100; i++) {
            result.add(new MovieFansInfoServiceTask(i));
        }
        return result;
    }

    public static void main(String... args) {
        int poolSize = 100;
        int cycleTimes = 1;
        MovieFansInfoServicePressureTest instance = new MovieFansInfoServicePressureTest(poolSize, cycleTimes);
        instance.execute();
    }
}
