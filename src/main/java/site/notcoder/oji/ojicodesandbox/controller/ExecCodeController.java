package site.notcoder.oji.ojicodesandbox.controller;

import cn.hutool.core.codec.Base64;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import site.notcoder.oji.ojicodesandbox.config.CodeSandboxConfig;
import site.notcoder.oji.ojicodesandbox.exception.CodeSandboxExceptions;
import site.notcoder.oji.ojicodesandbox.factory.CodeSandbox;
import site.notcoder.oji.ojicodesandbox.factory.CodeSandboxFactory;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorRequest;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorResponse;

import javax.annotation.Resource;

@RestController
public class ExecCodeController {
    @Resource
    private CodeSandboxConfig config;

    @PostMapping("/exec")
    public ExecutorResponse exec(@RequestBody ExecutorRequest executorRequest) throws CodeSandboxExceptions {
        executorRequest.setCode(Base64.decodeStr(executorRequest.getCode()));
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(config.getType());
        return codeSandbox.doExecute(executorRequest);
    }
}
