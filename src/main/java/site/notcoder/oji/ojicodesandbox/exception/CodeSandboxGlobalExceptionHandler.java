package site.notcoder.oji.ojicodesandbox.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorResponse;

/**
 * 全局异常处理器
 *
 *  
 *   
 */
@RestControllerAdvice
@Slf4j
public class CodeSandboxGlobalExceptionHandler {
    @ExceptionHandler(CodeSandboxExceptions.class)
    public ExecutorResponse runtimeExceptionHandler(CodeSandboxExceptions e) {
        log.error("CodeSandboxExceptions", e);
        return ExecutorResponse.errorForCompile(e.toString());
    }
}
