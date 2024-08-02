package site.notcoder.oji.ojicodesandbox.docker;

import cn.hutool.core.io.FileUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.Image;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import site.notcoder.oji.ojicodesandbox.config.CodeSandboxConfig;
import site.notcoder.oji.ojicodesandbox.docker.pool.DockerClientPool;
import site.notcoder.oji.ojicodesandbox.docker.pool.DockerClientPoolManager;
import site.notcoder.oji.ojicodesandbox.exception.CodeSandboxExceptions;
import site.notcoder.oji.ojicodesandbox.model.docker.ExecCommandResponse;
import site.notcoder.oji.ojicodesandbox.model.exec.SandboxExecResponse;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorResponse;
import site.notcoder.oji.ojicodesandbox.model.sandbox.InputArg;

import javax.annotation.Resource;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class DockerExecSandbox extends AbstractDockerExecSandbox {

    @Resource
    private DockerClientPoolManager dockerClientPoolManager;

    @Resource
    private CodeSandboxConfig config;


    @Override
    public ExecutorResponse execCode(
            String lang,
            String imageName,
            String code,
            List<InputArg> inputs
    ) throws CodeSandboxExceptions {

        DockerClientPool pool = dockerClientPoolManager.getPool();

        DockerClient client = null;

        CreateContainerResponse container = null;

        try {
            // 实例化DockerClient
            client = getClient();

            // 拉取镜像
            Image image = loadImage(client, imageName);

            // 创建容器
            container = createContainer(client, image);

            // 启动容器
            client.startContainerCmd(container.getId()).exec();

            // 上传入口shell脚本
            URL url = this.getClass().getClassLoader().getResource(
                    String.format("entrypoints/%s/entrypoint.sh", lang)
            );

            Assert.notNull(url, "entrypoint脚本路径为空");

            String entrypoint = FileUtil.readString(url, Charset.defaultCharset());
            ExecCommandResponse execCommandResponse = uploadEntrypoint(client, container, entrypoint);

            Assert.isTrue(execCommandResponse.getSuccess(), "上传entrypoint脚本失败");

            // 编译代码
            ExecCommandResponse compile = compile(client, container, code);

            if (!compile.getSuccess()) {
                // 编译失败，结束
                return ExecutorResponse.errorForCompile(compile.getMessage());
            }

            // 执行代码
            ArrayList<SandboxExecResponse> responses = new ArrayList<>();
            for (InputArg input : inputs) {
                // TODO 如果超过最大时间或超过最大内存就停止/失败
                // TODO 计算消耗的时间和内存
                ExecCommandResponse exec = exec(client, container, input.getData());

                if (!exec.getSuccess()) {
                    responses.add(SandboxExecResponse.error(
                            exec.getMessage(),
                            input,
                            0L,
                            0L
                    ));
                    return ExecutorResponse.errorForExec(exec.getMessage(), responses);
                }

                responses.add(SandboxExecResponse.success(
                   input,
                   exec.getMessage(),
                   0L,
                   0L
                ));
            }
            return ExecutorResponse.success(responses);

        } catch (Exception e) {
            throw new CodeSandboxExceptions(String.format("Docker执行代码出现错误: %s", e.getMessage()));
        } finally {
            if (client != null) {
                // 删除container
                if (container != null) {
                    try {
                        // 如果不是debug模式, 删除容器
                        if (!config.getDebug()) {
                            client.removeContainerCmd(container.getId())
                                    .withForce(true)
                                    .exec();
                        }
                    } catch (Exception e){
                        log.error("容器删除失败： message: {}", e.getMessage());
                    }
                    finally {
                        // 回收DockerClient
                        pool.returnObject(client);
                    }
                }
            }
        }
    }


}
