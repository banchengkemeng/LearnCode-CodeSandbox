package site.notcoder.oji.ojicodesandbox.starter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Starer自动配置导入类
 */

@Configuration
public class CodeSandboxConfiguration {

    @Bean
    public CodeSandboxExecutor codeSandboxExecutor() {
        return new CodeSandboxExecutor();
    }
}
