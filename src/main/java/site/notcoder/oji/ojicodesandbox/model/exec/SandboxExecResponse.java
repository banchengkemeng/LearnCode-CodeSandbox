package site.notcoder.oji.ojicodesandbox.model.exec;

import cn.hutool.core.codec.Base64;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.notcoder.oji.ojicodesandbox.model.sandbox.InputArg;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SandboxExecResponse {

    Boolean success;
    String message;
    InputArg input;
    String decodeInput;
    String output;
    Long time;
    Long memory;

    public static SandboxExecResponse success(
            InputArg input,
            String output,
            Long time,
            Long memory
    ) {
        return new SandboxExecResponse(
                true,
                "执行成功",
                input,
                Base64.decodeStr(input.getData()),
                output,
                time,
                memory
        );
    }

    public static SandboxExecResponse error(
            String message,
            InputArg input,
            Long time,
            Long memory
    ) {
        return new SandboxExecResponse(
                false,
                message,
                input,
                Base64.decodeStr(input.getData()),
                "",
                time,
                memory
        );
    }
}
