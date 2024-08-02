package site.notcoder.oji.ojicodesandbox;

import cn.hutool.core.compiler.CompilerException;
import cn.hutool.core.compiler.CompilerUtil;
import cn.hutool.core.io.FileUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.netty.NettyDockerCmdExecFactory;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import site.notcoder.oji.ojicodesandbox.config.CodeSandboxConfig;
import site.notcoder.oji.ojicodesandbox.docker.pool.DockerClientFactory;
import site.notcoder.oji.ojicodesandbox.docker.pool.DockerClientPool;
import site.notcoder.oji.ojicodesandbox.factory.CodeSandbox;
import site.notcoder.oji.ojicodesandbox.factory.CodeSandboxFactory;
import site.notcoder.oji.ojicodesandbox.model.exec.SandboxExecResponse;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorRequest;
import site.notcoder.oji.ojicodesandbox.model.sandbox.ExecutorResponse;
import site.notcoder.oji.ojicodesandbox.model.sandbox.InputArg;
import site.notcoder.oji.ojicodesandbox.starter.CodeSandboxExecutor;
import site.notcoder.oji.ojicodesandbox.utils.DockerExecUtils;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

@SpringBootTest
public class OjiCodeSandboxApplicationTests {
    @Resource
    private CodeSandboxConfig codeSandboxConfig;

    @Resource
    private DockerClientFactory dockerClientFactory;

    @Resource
    private CodeSandboxExecutor codeSandboxExecutor;

    @Test
    void contextLoads() {
        System.out.println(dockerClientFactory);
    }

    @Test
    void testCodeSandbox() throws Exception {
        BufferedReader reader = FileUtil.getReader("code/Main.java", "utf-8");
        StringBuilder code = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            code.append(line).append("\n");
        }

        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(codeSandboxConfig.getType());


        ExecutorRequest executorRequest = ExecutorRequest
                .builder()
                .lang("java")
                .code(code.toString())
                .inputs(Arrays.asList(
                        new InputArg("in1.txt", "3 4 5"),
                        new InputArg("in2.txt", "4 5 6")
                ))
                .build();
        codeSandbox.doExecute(executorRequest);
    }

    @Test
    void readFile() throws Exception {


        BufferedReader reader = FileUtil.getReader("code/Main.java", "utf-8");
        String s = reader.readLine();
        System.out.println(s);
    }

    @Test
    void writeFile() throws Exception {
        File file = FileUtil.writeBytes("a".getBytes(), "code/Test.java");
        String absolutePath = file.getAbsolutePath();
        System.out.println(absolutePath);
    }

    @Test
    void testJavaCompile() throws Exception {
//        String absolutePath = FileUtil.getAbsolutePath("code/Main.java");
//        System.out.println(absolutePath);
//        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
//        javaCompiler.run(null, System.out, System.out, absolutePath);

        BufferedReader reader = FileUtil.getReader("code/Main.java", "utf-8");
        StringBuilder code = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            code.append(line).append("\n");
        }

        Class<?> clazz = null;
        try {
            ClassLoader classLoader = CompilerUtil.getCompiler(null)
                    .addSource("Main", code.toString())
                    .compile();
            clazz = classLoader.loadClass("Main");
        } catch (CompilerException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (clazz == null) {
            return;
        }

        Object o = clazz.newInstance();
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            String name = declaredMethod.getName();
            if ("main".equals(name)) {
                Object invoke = declaredMethod.invoke(o, (Object[]) new String[1]);
                System.out.println(invoke);
            }
        }
    }

    @Test
    void testDocker() throws Exception {
        DefaultDockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost("tcp://192.168.67.129:2375")
                .build();

        DockerClient client = DockerClientBuilder.getInstance(config)
                .withDockerCmdExecFactory(new NettyDockerCmdExecFactory())
                .build();


        client.listImagesCmd().exec().forEach(System.out::println);

        client.close();
    }

    @Test
    void testDockerPool() throws Exception {
        GenericObjectPoolConfig<DockerClient> config = new GenericObjectPoolConfig<>();
        config.setMaxTotal(5);
        DockerClientPool dockerClientPool = new DockerClientPool(dockerClientFactory, config);
        DockerClient client = dockerClientPool.borrowObject();
        dockerClientPool.close();

        List<Image> exec = client.listImagesCmd().exec();
        System.out.println(exec);

    }

    @Test
    void testDockerExec() throws Exception {
        ArrayList<InputArg> inputs = new ArrayList<>();
        inputs.add(new InputArg("in1.txt", "3"));
        inputs.add(new InputArg("in2.txt", "8"));
        DockerExecUtils.execByOpenjdk80102("1", inputs);
    }

    @Test
    void testStarter() throws Exception {
        String code = "import java.util.Scanner;\n" +
                "\n" +
                "public class Main{\n" +
                "    public static void main(String[] args) {\n" +
                "        Scanner scanner = new Scanner(System.in);\n" +
                "        int i = scanner.nextInt();\n" +
                "        int i1 = scanner.nextInt();\n" +
                "        System.out.println(i*i1);\n" +
                "    }\n" +
                "}";
        ExecutorRequest executorRequest = new ExecutorRequest(
                code,
                "java",
                Arrays.asList(
                        new InputArg(
                                "1.txt",
                                "6 6"
                        ),
                        new InputArg(
                                "2.txt",
                                "3 5"
                        )
                )
        );

        ExecutorResponse response = codeSandboxExecutor.exec(executorRequest);
        System.out.println(response);
    }

    @Test
    void temp() throws Exception {

//        URL resource = this.getClass().getClassLoader().getResource("entrypoints/java/entrypoint.sh");
//        System.out.println(resource);
//        String s = FileUtil.readString(resource, Charset.defaultCharset());
//        System.out.println(s);

        String path = "file:\\D:\\MavenRepository\\site\\notecoder\\oji\\oji-code-sandbox\\0.0.1-SNAPSHOT\\oji-code-sandbox-0.0.1-SNAPSHOT.jar!\\entrypoints\\java\\entrypoint.sh";
        //        BufferedReader in = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(path)));
//        StringBuffer buffer = new StringBuffer();
//        String line = "";
//        while ((line = in.readLine()) != null){
//            buffer.append(line);
//        }
//        String input = buffer.toString();
//
//        System.out.println(input);

//        FileUtil.readString(, Charset.defaultCharset());
    }

    @Test
    void concurrent() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setMaxPoolSize(50);
        threadPoolTaskExecutor.setCorePoolSize(20);
        threadPoolTaskExecutor.setQueueCapacity(200);
        threadPoolTaskExecutor.setKeepAliveSeconds(60);
        threadPoolTaskExecutor.setThreadNamePrefix("judge-thread-pool");
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();


        ArrayList<Future<ExecutorResponse>> futures = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            futures.add(
                    threadPoolTaskExecutor.submit(() -> {
                        String code = "import java.util.Scanner;\n" +
                                "\n" +
                                "public class Main{\n" +
                                "    public static void main(String[] args) {\n" +
                                "        Scanner scanner = new Scanner(System.in);\n" +
                                "        int i = scanner.nextInt();\n" +
                                "        int i1 = scanner.nextInt();\n" +
                                "        System.out.println(i*i1);\n" +
                                "    }\n" +
                                "}";
                        ExecutorRequest executorRequest = new ExecutorRequest(
                                code,
                                "java",
                                Arrays.asList(
                                        new InputArg(
                                                "1.txt",
                                                "6 6"
                                        ),
                                        new InputArg(
                                                "2.txt",
                                                "3 5"
                                        )
                                )
                        );

                        return codeSandboxExecutor.exec(executorRequest);
                    })
            );
        }

        futures.forEach(f -> {
            try {
                ExecutorResponse response = f.get();
                Assertions.assertEquals(response.getSuccess(), true);
                Assertions.assertEquals(response.getMessage(), "执行成功");
                List<SandboxExecResponse> responses = response.getResponses();
                for (SandboxExecResponse resp : responses) {
                    Assertions.assertEquals(resp.getMessage(), "执行成功");
                }

            } catch (Exception e) {
                e.printStackTrace();
                e.printStackTrace();
            }
        });

    }

}
