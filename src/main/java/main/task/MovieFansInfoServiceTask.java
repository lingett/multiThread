package main.task;

import entity.AbstractTask;
import entity.FutureResult;

public class MovieFansInfoServiceTask extends AbstractTask<FutureResult, Integer> {
    public MovieFansInfoServiceTask(Integer param) {
        super(param);
    }

    @Override
    protected void handle(Integer param) throws Exception {
        Thread.sleep(1000);
    }
}
