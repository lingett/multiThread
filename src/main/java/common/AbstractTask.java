package common;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.Callable;

public abstract class AbstractTask<Result extends FutureResult, Param> implements Callable<Result> {
    private Param param;

    public AbstractTask(Param param) {
        this.param = param;
    }

    @Override
    public Result call() throws Exception {
        Result result = getFutureResultClass().newInstance();
        long startTime = new Date().getTime();
        try {
            handle(param);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        } finally {
            long endTime = new Date().getTime();
            result.setExecuteTime((endTime - startTime) / 1000);
        }
        return result;
    }

    private Class<Result> getFutureResultClass() {
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            return (Class<Result>) ((ParameterizedType) type).getActualTypeArguments()[0];
        }
        throw new RuntimeException("没有找到类信息");
    }

    protected abstract void handle(Param param) throws Exception;
}