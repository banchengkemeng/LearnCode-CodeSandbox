package site.notcoder.oji.ojicodesandbox.factory.impl;

import site.notcoder.oji.ojicodesandbox.factory.CodeSandbox;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorRequest;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorResponse;

public class ThirdPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecutorResponse doExecute(ExecutorRequest executorRequest) {
        System.out.println("执行第三方代码沙箱");
        return null;
    }
}
