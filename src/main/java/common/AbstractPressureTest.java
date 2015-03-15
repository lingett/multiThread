package common;

import entity.FutureResult;
import entity.ResultStatistics;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@NoArgsConstructor
public abstract class AbstractPressureTest<Result extends FutureResult> implements PressureTest {
    private int poolSize = 10;
    private int cycleTimes = 1;

    private int taskQueueSize = 2 << 10;
    private int taskResultQueueSize = 2 << 10;

    private AtomicInteger producedCount = new AtomicInteger(0);
    private AtomicInteger consumedCount = new AtomicInteger(0);
    private AtomicInteger handledCount = new AtomicInteger(0);

    private boolean isInit = false;
    private boolean generateTaskDone = false;
    private boolean isDone = false;
    private ResultStatistics resultStatistics;

    private ExecutorService consumerPool;
    private ExecutorService controllerPool = Executors.newFixedThreadPool(3);

    private BlockingQueue<Callable<Result>> taskQueue;
    private BlockingQueue<Future<Result>> taskResultQueue;

    /**
     * @param poolSize   线程池大小
     * @param cycleTimes 重复执行次数
     */
    public AbstractPressureTest(int poolSize, int cycleTimes) {
        this.poolSize = poolSize;
        this.cycleTimes = cycleTimes;
    }

    /**
     * 执行测试计划
     * 1.初始化线程池
     * 2.执行“生产测试用例”线程，对外扩展接口-generateTask()
     * 3.执行“消费测试用例”线程，实则将测试用例数据通过多线程交由执行队列处理
     * 4.执行“解析测试结果”线程，统计测试用例执行结果
     * 5.所有测试用例执行完毕后，关闭线程池，退出测试计划
     */
    @Override
    public void execute() {
        if (!isInit) {
            init();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        produceTask(taskQueue);
        consumeTask(taskQueue, taskResultQueue);
        handleTaskResult(taskResultQueue);

        while (true) {
            if (isDone) {
                done();
                return;
            }
        }
    }

    public void init() {
        System.out.println(String.format("测试计划[%s]开始执行", getListenerName()));
        this.resultStatistics = new ResultStatistics(getListenerName(), poolSize * cycleTimes);
        this.consumerPool = Executors.newFixedThreadPool(poolSize);
        this.taskQueue = new LinkedBlockingQueue<Callable<Result>>(taskQueueSize);
        this.taskResultQueue = new LinkedBlockingQueue<Future<Result>>(taskResultQueueSize);
        this.isInit = true;
    }

    private void produceTask(BlockingQueue<Callable<Result>> taskQueue) {
        BlockingQueueCallable producer = new BlockingQueueCallable<Result>(taskQueue) {
            @Override
            public Result call() throws Exception {
                while (!isDone) {
                    if (!generateTaskDone) {
                        generateTask((BlockingQueue<Callable<Result>>) getBlockingQueue());
                        generateTaskDone = true;
                    }
                }
                return null;
            }
        };
        controllerPool.submit(producer);
    }

    private void consumeTask(BlockingQueue<Callable<Result>> taskQueue, BlockingQueue<Future<Result>> taskResultQueue) {
        MultiBlockingQueueCallable consumer = new MultiBlockingQueueCallable<Result>(taskQueue, taskResultQueue) {
            @Override
            public Result call() throws Exception {
                while (!isDone) {
                    try {
                        Callable<Result> task = (Callable<Result>) getBlockingQueueList().get(0).take();
                        Future<Result> future = getConsumerPool().submit(task);
                        ((BlockingQueue) getBlockingQueueList().get(1)).put(future);

                        int currentConsumedCount = getConsumedCount().incrementAndGet();
                        if (currentConsumedCount == resultStatistics.getTotalCount()) {
                            return null;
                        }
                        if (generateTaskDone && currentConsumedCount == getProducedCount().get()) {
                            generateTaskDone = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };
        controllerPool.submit(consumer);
    }

    private void handleTaskResult(BlockingQueue<Future<Result>> taskResultQueue) {
        BlockingQueueCallable handler = new BlockingQueueCallable<Result>(taskResultQueue) {
            @Override
            public Result call() throws Exception {
                while (!isDone) {
                    try {
                        Future<Result> future = (Future<Result>) getBlockingQueue().take();
                        Result result = future.get();
                        handleResult(result);
                        int currentHandledCount = getHandledCount().incrementAndGet();
                        System.out.println(String.format("Task-%d done", currentHandledCount));
                        if (currentHandledCount == getConsumedCount().get()) {
                            setDone(true);
                            return null;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }
        };
        controllerPool.submit(handler);
    }

    protected String getListenerName() {
        return this.getClass().getSimpleName();
    }

    protected void done() {
        shutDownConsumerPool();
        shutDownControllerPool();
        this.resultStatistics.setEndTime(new Date());
        System.out.println(String.format("测试计划[%s]执行完毕，耗时[%d]ms", getListenerName(), this.resultStatistics.getExecuteTime()));
        System.out.println(String.format("共执行测试用例[%d]个，成功[%d]个，失败[%d]个", resultStatistics.getTotalCount(), resultStatistics.getSuccessCount(), resultStatistics.getErrorCount()));
        System.out.println(String.format("最快执行时间[%d]ms，最慢执行时间[%d]ms", this.resultStatistics.getMinExecuteTime(), this.resultStatistics.getMaxExecuteTime()));
        System.out.println(String.format("平均执行时间[%d]ms，90线执行时间[%d]ms", this.resultStatistics.getAvgExecuteTime(), this.resultStatistics.getExecuteTimeOf90Line()));
    }

    private void shutDownConsumerPool() {
        this.consumerPool.shutdown();
    }

    private void shutDownControllerPool() {
        this.controllerPool.shutdown();
    }

    protected abstract void generateTask(BlockingQueue<Callable<Result>> taskQueue);

    private void handleResult(Result result) {
        this.resultStatistics.addResult(result);
    }
}
