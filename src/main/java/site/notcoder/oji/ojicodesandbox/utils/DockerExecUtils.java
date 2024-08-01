package site.notcoder.oji.ojicodesandbox.utils;

import cn.hutool.core.codec.Base64;
import org.springframework.stereotype.Component;
import site.notcoder.oji.ojicodesandbox.constant.DockerConstant;
import site.notcoder.oji.ojicodesandbox.docker.DockerExecSandbox;
import site.notcoder.oji.ojicodesandbox.exception.CodeSandboxExceptions;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorResponse;
import site.notcoder.oji.ojicodesandbox.model.sandbox.InputArg;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

@Component
public class DockerExecUtils {
    private static DockerExecSandbox dockerExecSandbox;

    @Resource
    private DockerExecSandbox preDockerSandbox;

    @PostConstruct
    void init() {
        dockerExecSandbox = preDockerSandbox;
    }

    public static ExecutorResponse execByOpenjdk80102(String code, List<InputArg> inputs) throws CodeSandboxExceptions {
        return dockerExecSandbox.execCode(
                "java",
                DockerConstant.OPENJDK_8U102_IMAGE_NAME,
                Base64.encode(code),
                inputs
        );
    }
}
