package site.notcoder.oji.ojicodesandbox.starter;

import cn.hutool.core.codec.Base64;
import org.springframework.beans.factory.annotation.Value;
import site.notcoder.oji.ojicodesandbox.config.CodeSandboxConfig;
import site.notcoder.oji.ojicodesandbox.exception.CodeSandboxExceptions;
import site.notcoder.oji.ojicodesandbox.factory.CodeSandbox;
import site.notcoder.oji.ojicodesandbox.factory.CodeSandboxFactory;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorRequest;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorResponse;

import javax.annotation.Resource;

public class CodeSandboxExecutor {
    @Resource
    private CodeSandboxConfig config;

    public ExecutorResponse exec(ExecutorRequest executorRequest) throws CodeSandboxExceptions {
        executorRequest.setCode(Base64.decodeStr(executorRequest.getCode()));
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(config.getType());
        return codeSandbox.doExecute(executorRequest);
    }
}
