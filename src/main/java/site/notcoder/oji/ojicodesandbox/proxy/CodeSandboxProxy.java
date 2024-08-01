package site.notcoder.oji.ojicodesandbox.proxy;

import lombok.extern.slf4j.Slf4j;
import site.notcoder.oji.ojicodesandbox.exception.CodeSandboxExceptions;
import site.notcoder.oji.ojicodesandbox.factory.CodeSandbox;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorRequest;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorResponse;

/**
 * 静态代理模式处理日志
 */

@Slf4j
public class CodeSandboxProxy implements CodeSandbox {

    private final CodeSandbox codeSandbox;

    public CodeSandboxProxy(CodeSandbox codeSandbox) {
        this.codeSandbox = codeSandbox;
    }

    @Override
    public ExecutorResponse doExecute(ExecutorRequest executorRequest) throws CodeSandboxExceptions {
        log.info("将要执行代码沙箱, 请求信息: {}", executorRequest);
        ExecutorResponse executorResponse = codeSandbox.doExecute(executorRequest);
        log.info("代码沙箱执行完毕, 响应信息: {}", executorResponse);
        return executorResponse;
    }
}
