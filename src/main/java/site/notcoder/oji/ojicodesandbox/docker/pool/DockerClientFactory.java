package site.notcoder.oji.ojicodesandbox.docker.pool;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.netty.NettyDockerCmdExecFactory;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import site.notcoder.oji.ojicodesandbox.config.CodeSandboxConfig;
import site.notcoder.oji.ojicodesandbox.config.CodeSandboxDockerConfig;

import javax.annotation.Resource;

@Component
public class DockerClientFactory extends BasePooledObjectFactory<DockerClient> {

    @Resource
    private CodeSandboxConfig codeSandboxConfig;

    @Override
    public DockerClient create() throws Exception {
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(
                        codeSandboxConfig.getDocker().getHost()
                )
                .build();

        return DockerClientBuilder.getInstance(config)
                .withDockerCmdExecFactory(new NettyDockerCmdExecFactory())
                .build();
    }


    @Override
    public void destroyObject(PooledObject<DockerClient> p) throws Exception {
        DockerClient client = p.getObject();
        if (client != null) {
            client.close();
        }
    }

    @Override
    public PooledObject<DockerClient> wrap(DockerClient dockerClient) {
        return new DefaultPooledObject<>(dockerClient);
    }
}
