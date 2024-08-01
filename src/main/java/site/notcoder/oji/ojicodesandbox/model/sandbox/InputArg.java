package site.notcoder.oji.ojicodesandbox.model.sandbox;

import cn.hutool.core.codec.Base64;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class InputArg {
    String id;
    String data;

    public InputArg(String id, String data) {
        this.id = id;
        this.data = Base64.encode(data);
    }
}
