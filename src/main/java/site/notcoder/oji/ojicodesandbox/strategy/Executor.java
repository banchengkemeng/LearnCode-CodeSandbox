package site.notcoder.oji.ojicodesandbox.strategy;

import site.notcoder.oji.ojicodesandbox.exception.CodeSandboxExceptions;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorContext;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorResponse;

public interface Executor {
    ExecutorResponse execCode(ExecutorContext executorContext) throws CodeSandboxExceptions;
}
