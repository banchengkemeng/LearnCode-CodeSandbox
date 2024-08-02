package site.notcoder.oji.ojicodesandbox.docker.pool;

import com.github.dockerjava.api.DockerClient;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.AbandonedConfig;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class DockerClientPool extends GenericObjectPool<DockerClient> {
    public DockerClientPool(PooledObjectFactory<DockerClient> factory) {
        super(factory);
    }

    public DockerClientPool(PooledObjectFactory<DockerClient> factory, GenericObjectPoolConfig<DockerClient> config) {
        super(factory, config);
    }

    public DockerClientPool(PooledObjectFactory<DockerClient> factory, GenericObjectPoolConfig<DockerClient> config, AbandonedConfig abandonedConfig) {
        super(factory, config, abandonedConfig);
    }
}
