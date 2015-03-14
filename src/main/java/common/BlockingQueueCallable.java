package common;

import lombok.Data;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

@Data
public abstract class BlockingQueueCallable<T> implements Callable<T> {
    private BlockingQueue<?> blockingQueue;

    public BlockingQueueCallable(BlockingQueue<?> blockingQueue) {
        this.blockingQueue = blockingQueue;
    }
}
