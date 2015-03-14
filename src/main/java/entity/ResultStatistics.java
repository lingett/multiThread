package entity;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
public class ResultStatistics {
    private String testPlanName;
    private Date beginTime = new Date();
    private Date endTime = new Date();

    private long totalCount;
    private long successCount;
    private long errorCount;

    private long maxExecuteTime;
    private long minExecuteTime;
    private long executeTimeOf90Line;

    private int executeTimeListCapacity;
    private List<Long> executeTimeListOf10Line = Lists.newArrayList();

    public ResultStatistics(String testPlanName, long totalCount) {
        this.testPlanName = testPlanName;
        this.totalCount = totalCount;
        this.executeTimeListCapacity = (int) Math.floor(totalCount * 0.1);
    }

    public long getExecuteTime() {
        return endTime.getTime() - beginTime.getTime();
    }

    public synchronized void addResult(FutureResult result) {
        if (result.getExecuteTime() > maxExecuteTime) {
            maxExecuteTime = result.getExecuteTime();
        }
        if (minExecuteTime == 0 || minExecuteTime > result.getExecuteTime()) {
            minExecuteTime = result.getExecuteTime();
        }
        if (result.isSuccess()) {
            successCount++;
        } else {
            errorCount++;
        }
        addExecuteTimeListOf10Line(result.getExecuteTime());
    }

    private synchronized void addExecuteTimeListOf10Line(long executeTime) {
        if (executeTimeOf90Line < executeTime) {
            if (executeTimeListOf10Line.size() < executeTimeListCapacity) {
                executeTimeListOf10Line.add(executeTime);
            } else {
                Collections.sort(executeTimeListOf10Line);
                executeTimeListOf10Line.set(0, executeTime);
                executeTimeOf90Line = executeTime < executeTimeListOf10Line.get(1) ? executeTime : executeTimeListOf10Line.get(1);
            }
        }
    }

    public static void main(String... args) {
        ResultStatistics result = new ResultStatistics("a", 10000);
        for (int i = 1; i < 10000; i++) {
            FutureResult r = new FutureResult();
            r.setExecuteTime(i);
            result.addResult(r);
        }
        System.out.println(result.getExecuteTimeOf90Line());
    }
}
