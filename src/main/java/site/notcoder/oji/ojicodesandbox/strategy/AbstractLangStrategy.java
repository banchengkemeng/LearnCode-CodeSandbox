package site.notcoder.oji.ojicodesandbox.strategy;

import lombok.extern.slf4j.Slf4j;
import site.notcoder.oji.ojicodesandbox.config.CodeSandboxConfig;
import site.notcoder.oji.ojicodesandbox.exception.CodeSandboxExceptions;
import site.notcoder.oji.ojicodesandbox.model.sandbox.CompileInfo;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorContext;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorResponse;
import site.notcoder.oji.ojicodesandbox.model.sandbox.LangStrategyEnum;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 不同编程语言编译执行代码策略
 * 模板模式 + 策略模式
 */

@Slf4j
public abstract class AbstractLangStrategy implements Executor {
    @Resource
    private CodeSandboxConfig config;

    protected static Map<String , AbstractLangStrategy> langStrategyRegistry = new HashMap<>();


    @PostConstruct
    private void init() {
        langStrategyRegistry.put(getStrategyName(), getStrategy());
    }

    @Override
    public ExecutorResponse execCode(ExecutorContext executorContext) throws CodeSandboxExceptions {
        try {
            CompileInfo compile = compile(executorContext);

            log.info("编译程序, compileInfo: {}", compile);

            ExecutorResponse execResponse = exec(executorContext);

            log.info("执行程序, executorResponse :{}", execResponse);

            return execResponse;
        } catch (Exception e) {
            throw new CodeSandboxExceptions(e.getMessage());
        } finally {
            // 资源回收
            if (!config.getDebug()) {
                destroy(executorContext);
            }
        }
    }

    public static Executor getStrategy(LangStrategyEnum langStrategyEnum) {
        return langStrategyRegistry.get(langStrategyEnum.getValue());
    }

    /**
     * 编译代码(如果需要本地编译，调用此方法)
     */
    abstract protected CompileInfo compile(ExecutorContext executorContext) throws CodeSandboxExceptions;

    /**
     * 执行代码
     */
    abstract protected ExecutorResponse exec(ExecutorContext executorContext) throws CodeSandboxExceptions;

    /**
     * 资源回收
     */
    protected abstract void destroy(ExecutorContext executorContext) throws CodeSandboxExceptions;

    abstract protected AbstractLangStrategy getStrategy();
    abstract protected String getStrategyName();
}
