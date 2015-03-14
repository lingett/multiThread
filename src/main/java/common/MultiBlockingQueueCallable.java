package common;

import com.google.common.collect.Lists;
import lombok.Data;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;

@Data
public abstract class MultiBlockingQueueCallable<T> implements Callable<T> {
    private List<BlockingQueue<?>> blockingQueueList = Lists.newArrayList();

    public MultiBlockingQueueCallable(BlockingQueue<?>... blockingQueueList) {
        for (BlockingQueue<?> blockingQueue : blockingQueueList) {
            this.blockingQueueList.add(blockingQueue);
        }
    }
}
