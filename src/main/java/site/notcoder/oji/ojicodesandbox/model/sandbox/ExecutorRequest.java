package site.notcoder.oji.ojicodesandbox.model.sandbox;

import cn.hutool.core.codec.Base64;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
public class ExecutorRequest {
    private String code;
    private String lang;
    private List<InputArg> inputs;

    public ExecutorRequest(String code, String lang, List<InputArg> inputs) {
        this.code = Base64.encode(code);
        this.lang = lang;
        this.inputs = inputs;
    }
}
