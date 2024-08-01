package site.notcoder.oji.ojicodesandbox.factory.impl;

import site.notcoder.oji.ojicodesandbox.factory.CodeSandbox;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorRequest;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorResponse;

public class ExampleCodeSandbox implements CodeSandbox {
    @Override
    public ExecutorResponse doExecute(ExecutorRequest executorRequest) {
        System.out.println("执行示例代码沙箱");

        ExecutorResponse executorResponse = new ExecutorResponse();
        executorResponse.setSuccess(true);
        executorResponse.setMessage("执行成功");
        return executorResponse;
    }
}
