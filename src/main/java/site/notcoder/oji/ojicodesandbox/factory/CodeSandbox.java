package site.notcoder.oji.ojicodesandbox.factory;


import site.notcoder.oji.ojicodesandbox.exception.CodeSandboxExceptions;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorRequest;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorResponse;

public interface CodeSandbox {
    ExecutorResponse doExecute(ExecutorRequest executorRequest) throws CodeSandboxExceptions;
}
