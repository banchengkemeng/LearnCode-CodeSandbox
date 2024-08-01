package site.notcoder.oji.ojicodesandbox.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "codesandbox")
public class CodeSandboxConfig {

    private String type;
    private Long maxTimeLimit;
    private Long maxMemoryLimit;
    private Boolean debug;

    @NestedConfigurationProperty
    private CodeSandboxDockerConfig docker;
}




