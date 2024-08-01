package site.notcoder.oji.ojicodesandbox.docker;

import site.notcoder.oji.ojicodesandbox.exception.CodeSandboxExceptions;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorResponse;
import site.notcoder.oji.ojicodesandbox.model.sandbox.InputArg;

import java.util.List;

/**
 * Docker执行器
 */

public interface DockerExecutor {
    ExecutorResponse execCode(
            String lang,
            String imageName,
            String code,
            List<InputArg> inputs
    ) throws CodeSandboxExceptions;
}
