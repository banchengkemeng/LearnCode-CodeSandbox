package site.notcoder.oji.ojicodesandbox.starter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CodeSandboxConfiguration {

    @Bean
    public CodeSandboxExecutor codeSandboxExecutor() {
        return new CodeSandboxExecutor();
    }
}
