package site.notcoder.oji.ojicodesandbox.model.sandbox;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompileInfo implements Serializable {
    private Boolean success;

    private String message;

    private static final long serialVersionUID = 1L;
}
