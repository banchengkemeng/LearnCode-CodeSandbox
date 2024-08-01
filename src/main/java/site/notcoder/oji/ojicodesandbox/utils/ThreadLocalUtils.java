package site.notcoder.oji.ojicodesandbox.utils;

import com.github.dockerjava.api.DockerClient;

public class ThreadLocalUtils {
    private static final ThreadLocal<DockerClient> dockerClientThreadLocal = new ThreadLocal<>();

    public static void setDockerClient(DockerClient client) {
        dockerClientThreadLocal.set(client);
    }
    public static DockerClient getDockerClient() {
        return dockerClientThreadLocal.get();
    }

    public static void remove() {
        dockerClientThreadLocal.remove();
    }
}
