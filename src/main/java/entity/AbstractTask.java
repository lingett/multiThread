package entity;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.Callable;

public abstract class AbstractTask<Result extends FutureResult, CallService, CallArgs, CallResult> implements Callable<Result> {
    private CallService callService;
    private CallArgs callArgs;

    public AbstractTask(CallService callService, CallArgs callArgs) {
        this.callService = callService;
        this.callArgs = callArgs;
    }

    @Override
    public Result call() throws Exception {
        Class clazz = getFutureResultClass();
        Result result = getFutureResultClass().newInstance();
        long startTime = new Date().getTime();
        CallResult callResult = null;

        try {
            callResult = executeCall(callService, callArgs);
        } catch (Exception e) {
            result.setSuccess(false);
            result.setErrorMsg(e.getMessage());
        } finally {
            long endTime = new Date().getTime();
            result.setExecuteTime(endTime - startTime);
        }

        if (callResult != null) {
            try {
                handleCallResult(callResult);
            } catch (Error e) {
                result.setSuccess(false);
                result.setErrorMsg(e.getMessage());
            }
        }
        return result;
    }

    protected Class<Result> getFutureResultClass() {
        Type type = this.getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            return (Class<Result>) ((ParameterizedType) type).getActualTypeArguments()[0];
        }
        throw new RuntimeException("没有找到类信息");
    }

    protected void isTrue(boolean expression, String errorMsg) {
        if (!expression) {
            throw new Error(errorMsg);
        }
    }

    protected abstract CallResult executeCall(CallService callService, CallArgs callArgs);

    protected abstract void handleCallResult(CallResult callResult) throws Error;
}