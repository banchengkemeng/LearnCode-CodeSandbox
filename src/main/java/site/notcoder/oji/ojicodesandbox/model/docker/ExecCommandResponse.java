package site.notcoder.oji.ojicodesandbox.model.docker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExecCommandResponse {

    /**
     * 执行成功
     */
    Boolean success;

    /**
     * 返回信息
     */
    String message;

    /**
     * 时间占用(ms)
     */
    Long time;

    /**
     * 内存占用(k)
     */
    Long memory;

    public static ExecCommandResponse success(String  message, Long time, Long memory) {
        return new ExecCommandResponse(true, message, time, memory);
    }

    public static ExecCommandResponse error(String  message, Long time, Long memory) {
        return new ExecCommandResponse(false, message, time, memory);
    }
}
