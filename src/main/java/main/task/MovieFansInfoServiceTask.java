package main.task;

import common.AbstractTask;
import common.FutureResult;

public class MovieFansInfoServiceTask extends AbstractTask<FutureResult, Integer> {
    public MovieFansInfoServiceTask(Integer param) {
        super(param);
    }

    @Override
    protected void handle(Integer param) throws Exception {
        System.out.println(param);
    }
}
