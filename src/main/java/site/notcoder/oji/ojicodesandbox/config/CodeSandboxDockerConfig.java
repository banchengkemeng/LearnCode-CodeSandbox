package site.notcoder.oji.ojicodesandbox.config;

import lombok.Data;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
public class CodeSandboxDockerConfig {
    private String host;

    @NestedConfigurationProperty
    private CodeSandboxPoolConfig pool;
}