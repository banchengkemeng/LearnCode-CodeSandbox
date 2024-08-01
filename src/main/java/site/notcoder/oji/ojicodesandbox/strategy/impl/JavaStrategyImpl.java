package site.notcoder.oji.ojicodesandbox.strategy.impl;

import org.springframework.stereotype.Component;
import site.notcoder.oji.ojicodesandbox.exception.CodeSandboxExceptions;
import site.notcoder.oji.ojicodesandbox.model.sandbox.CompileInfo;
import site.notcoder.oji.ojicodesandbox.model.sandbox.LangStrategyEnum;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorContext;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorRequest;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorResponse;
import site.notcoder.oji.ojicodesandbox.model.sandbox.InputArg;
import site.notcoder.oji.ojicodesandbox.strategy.AbstractLangStrategy;
import site.notcoder.oji.ojicodesandbox.utils.DockerExecUtils;

import java.util.List;

@Component
public class JavaStrategyImpl extends AbstractLangStrategy {

    @Override
    protected CompileInfo compile(ExecutorContext executorContext) {
        return new CompileInfo(
                true,
                "编译操作在容器中进行，无需本地编译"
        );
    }

    @Override
    protected ExecutorResponse exec(ExecutorContext executorContext) throws CodeSandboxExceptions {
        ExecutorRequest executorRequest = executorContext.getExecutorRequest();
        List<InputArg> inputs = executorRequest.getInputs();
        return DockerExecUtils.execByOpenjdk80102(executorRequest.getCode(), inputs);
    }

    @Override
    protected void destroy(ExecutorContext executorContext) {
    }

    @Override
    protected AbstractLangStrategy getStrategy() {
        return this;
    }

    @Override
    protected String getStrategyName() {
        return LangStrategyEnum.JAVA.getValue();
    }
}
