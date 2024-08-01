package site.notcoder.oji.ojicodesandbox.model.sandbox;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.notcoder.oji.ojicodesandbox.model.exec.SandboxExecResponse;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecutorResponse {
    private Boolean success;
    private String message;
    private List<SandboxExecResponse> responses;

    public static ExecutorResponse success(List<SandboxExecResponse> responses) {
        return new ExecutorResponse(true, "执行成功", responses);
    }

    public static ExecutorResponse errorForCompile(String message) {
        return new ExecutorResponse(false, message, null);
    }

    public static ExecutorResponse errorForExec(String message, List<SandboxExecResponse> responses) {
        return new ExecutorResponse(false, message, responses);
    }
}
