package site.notcoder.oji.ojicodesandbox.docker;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.io.FileUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import site.notcoder.oji.ojicodesandbox.config.CodeSandboxConfig;
import site.notcoder.oji.ojicodesandbox.constant.DockerConstant;
import site.notcoder.oji.ojicodesandbox.docker.pool.DockerClientPool;
import site.notcoder.oji.ojicodesandbox.docker.pool.DockerClientPoolManager;
import site.notcoder.oji.ojicodesandbox.exception.CodeSandboxExceptions;
import site.notcoder.oji.ojicodesandbox.model.docker.ExecCommandResponse;
import site.notcoder.oji.ojicodesandbox.model.exec.SandboxExecResponse;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorResponse;
import site.notcoder.oji.ojicodesandbox.model.sandbox.InputArg;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class DockerExecSandbox implements DockerExecutor {

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
            String entrypoint = FileUtil.readString(url, Charset.defaultCharset());
            uploadEntrypoint(client, container, entrypoint);

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

    /**
     * 实例化DockerClient
     */
    private DockerClient getClient() throws Exception {
        try {
            DockerClientPool pool = dockerClientPoolManager.getPool();
            return pool.borrowObject();
        } catch (Exception e) {
            throw new Exception("获取Client异常");
        }
    }

    /**
     * 拉取镜像
     */
    private Image loadImage(DockerClient client, String imageName) throws Exception {
        try {
            ListImagesCmd listImagesCmd = client.listImagesCmd();
            List<Image> imageAll = listImagesCmd.exec();
            List<Image> images = imageAll.stream().filter(item -> {
                for (String repoTag : item.getRepoTags()) {
                    return repoTag.equals(imageName);
                }
                return false;
            }).collect(Collectors.toList());

            if (images.size() > 1) {
                throw new Exception("多个 [" + imageName + "] 镜像");
            }

            if (images.isEmpty()) {
                // 如果不存在镜像，拉镜像
                PullImageCmd pullImageCmd = client.pullImageCmd(imageName);
                pullImageCmd.exec(new PullImageResultCallback()).awaitCompletion();
            }

            return images.get(0);
        } catch (Exception e) {
            throw new Exception(String.format("创建镜像 [%s] 失败: %s", imageName, e.getMessage()));
        }

    }

    /**
     * 创建容器
     */
    private CreateContainerResponse createContainer(DockerClient client, Image image) throws Exception{
        try {
            return client.createContainerCmd(image.getId())
                    .withNetworkDisabled(true)
                    .withWorkingDir(DockerConstant.WORKING_DIR)
                    .withStdinOpen(true)
                    .withAttachStdout(true)
                    .exec();
        } catch (Exception e) {
            throw new Exception("创建容器失败: " + e.getMessage());
        }

    }

    /**
     * 执行命令
     * @param container 容器
     * @param cmd 命令
     * @return 执行结果
     */
    protected ExecCommandResponse execCommand(DockerClient client, CreateContainerResponse container, String cmd) {
        log.info("执行命令, cmd: {}", cmd);
        try {
            ExecCreateCmdResponse exec = client.execCreateCmd(container.getId())
                    .withCmd("/bin/bash", "-c", cmd)
                    .withAttachStdout(true)
                    .withAttachStderr(true)
                    .withAttachStdin(true)
                    .exec();

            // 准备输出流和错误流
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();

            // 执行命令回调，传入输出流和错误流
            ExecStartResultCallback execStartResultCallback = new ExecStartResultCallback(outputStream, errorStream);

            // 执行命令
            client.execStartCmd(exec.getId())
                    .exec(execStartResultCallback)
                    .awaitCompletion();

            // 错误流
            String err = errorStream.toString();
            if (StringUtils.isNotBlank(err)) {
                // 出现错误，设置错误信息
                return ExecCommandResponse.error(err, 0L, 0L);
            }

            // 输出流
            String output = outputStream.toString();
            return ExecCommandResponse.success(output, 0L, 0L);
        } catch (Exception e) {
            return ExecCommandResponse.error(e.toString(), 0L, 0L);
        }
    }

    protected ExecCommandResponse uploadEntrypoint(
            DockerClient client,
            CreateContainerResponse container,
            String entrypoint
    ) {
        return execCommand(
                client,
                container,
                String.format("echo %s | base64 -d > /code/entrypoint.sh", Base64.encode(entrypoint))
        );
    }

    /**
     * 编译代码
     * @param container 容器
     * @param code 代码
     * @return 编译结果
     */
    protected ExecCommandResponse compile(
            DockerClient client,
            CreateContainerResponse container,
            String code
    ) {
        return execCommand(
                client,
                container,
                String.format("bash entrypoint.sh compile %s", code)
        );
    }

    /**
     * 执行代码
     * @param container 容器
     * @param input 输入
     * @return 执行结果
     */
    protected ExecCommandResponse exec(
            DockerClient client,
            CreateContainerResponse container,
            String input
    ) {
        return execCommand(
                client,
                container,
                String.format("bash entrypoint.sh run %s", input)
        );
    }
}
