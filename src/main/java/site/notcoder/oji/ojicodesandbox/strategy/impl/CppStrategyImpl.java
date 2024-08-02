package site.notcoder.oji.ojicodesandbox.strategy.impl;

import org.springframework.stereotype.Component;
import site.notcoder.oji.ojicodesandbox.model.sandbox.CompileInfo;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorContext;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorResponse;
import site.notcoder.oji.ojicodesandbox.model.sandbox.LangStrategyEnum;
import site.notcoder.oji.ojicodesandbox.strategy.AbstractLangStrategy;

@Component
public class CppStrategyImpl extends AbstractLangStrategy {
    @Override
    protected CompileInfo compile(ExecutorContext executorContext) {
        System.out.println("CPP编译成功");
        CompileInfo compileInfo = new CompileInfo();
        compileInfo.setSuccess(true);
        compileInfo.setMessage("CPP编译成功");
        return compileInfo;
    }

    @Override
    protected ExecutorResponse exec(ExecutorContext executorContext) {
        System.out.println("CPP执行");
        return null;
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
        return LangStrategyEnum.CPP.getValue();
    }
}
