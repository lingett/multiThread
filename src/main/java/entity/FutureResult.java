package entity;

import lombok.Data;

@Data
public class FutureResult {
    private long executeTime;
    private boolean isSuccess = true;
    private String errorMsg;
}
