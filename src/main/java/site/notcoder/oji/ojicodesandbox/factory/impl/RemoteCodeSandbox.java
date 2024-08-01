package site.notcoder.oji.ojicodesandbox.factory.impl;


import lombok.extern.slf4j.Slf4j;
import site.notcoder.oji.ojicodesandbox.exception.CodeSandboxExceptions;
import site.notcoder.oji.ojicodesandbox.factory.CodeSandbox;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorContext;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorRequest;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorResponse;
import site.notcoder.oji.ojicodesandbox.model.sandbox.LangStrategyEnum;
import site.notcoder.oji.ojicodesandbox.strategy.AbstractLangStrategy;
import site.notcoder.oji.ojicodesandbox.strategy.Executor;

/**
 * 远程代码沙箱
 */

@Slf4j
public class RemoteCodeSandbox implements CodeSandbox {
    @Override
    public ExecutorResponse doExecute(ExecutorRequest executorRequest) throws CodeSandboxExceptions {
        log.info("执行远程代码沙箱");

        // 检测编程语言是否存在
        String lang = executorRequest.getLang();
        LangStrategyEnum enumByValue = LangStrategyEnum.getEnumByValue(lang);
        if (enumByValue == null) {
            throw new CodeSandboxExceptions("编程语言 [" + lang + "] 不存在");
        }

        // 根据编程语言选择相应策略执行代码
        Executor executor = AbstractLangStrategy.getStrategy(enumByValue);

        // 设置策略上下文
        ExecutorContext executorContext = new ExecutorContext();
        executorContext.setExecutorRequest(executorRequest);

        return executor.execCode(executorContext);
    }
}
