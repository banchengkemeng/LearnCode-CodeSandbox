package site.notcoder.oji.ojicodesandbox.docker;

import cn.hutool.core.codec.Base64;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import site.notcoder.oji.ojicodesandbox.constant.DockerConstant;
import site.notcoder.oji.ojicodesandbox.docker.pool.DockerClientPool;
import site.notcoder.oji.ojicodesandbox.docker.pool.DockerClientPoolManager;
import site.notcoder.oji.ojicodesandbox.exception.CodeSandboxExceptions;
import site.notcoder.oji.ojicodesandbox.model.docker.ExecCommandResponse;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorResponse;
import site.notcoder.oji.ojicodesandbox.model.sandbox.InputArg;

import javax.annotation.Resource;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public abstract class AbstractDockerExecSandbox implements DockerExecutor {

    @Resource
    private DockerClientPoolManager dockerClientPoolManager;

    @Override
    public abstract ExecutorResponse execCode(
            String lang,
            String imageName,
            String code,
            List<InputArg> inputs
    ) throws CodeSandboxExceptions;

    /**
     * 实例化DockerClient
     */
    protected DockerClient getClient() throws Exception {
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
    protected Image loadImage(DockerClient client, String imageName) throws Exception {
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
    protected CreateContainerResponse createContainer(DockerClient client, Image image) throws Exception{
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
    private ExecCommandResponse execCommand(DockerClient client, CreateContainerResponse container, String cmd) {
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
