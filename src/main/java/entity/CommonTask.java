package entity;

public abstract class CommonTask<CallService, CallArgs, CallResult> extends AbstractTask<FutureResult, CallService, CallArgs, CallResult> {
    public CommonTask(CallService callService, CallArgs callArgs) {
        super(callService, callArgs);
    }

    @Override
    protected Class getFutureResultClass() {
        return FutureResult.class;
    }
}
