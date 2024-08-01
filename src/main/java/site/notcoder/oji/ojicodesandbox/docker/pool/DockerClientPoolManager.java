package site.notcoder.oji.ojicodesandbox.docker.pool;

import com.github.dockerjava.api.DockerClient;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import site.notcoder.oji.ojicodesandbox.config.CodeSandboxConfig;
import site.notcoder.oji.ojicodesandbox.config.CodeSandboxDockerConfig;

import javax.annotation.Resource;

@Component
public class DockerClientPoolManager {
    private DockerClientPool pool;
    @Resource
    private DockerClientFactory dockerClientFactory;
    @Resource
    private CodeSandboxConfig codeSandboxConfig;

    private GenericObjectPoolConfig<DockerClient> config;

    public synchronized DockerClientPool getPool() {
        if (config == null) {
            config = new GenericObjectPoolConfig<>();

            config.setMaxTotal(codeSandboxConfig.getDocker().getPool().getMaxTotal());
        }

        if (pool == null) {
            pool= new DockerClientPool(dockerClientFactory, config);
        }

        return pool;
    }

}
