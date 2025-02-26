# 项目介绍
LearnCode 代码沙箱

本项目是为编程学习人员提供的在线题目评测系统(Online Judge)。系统可以依据预设测试样例，执行用户提交的代码并判断是否符合题目要求。

# 服务提供方式
- SpringBoot Starter, 导入Jar包, 直接注入代码沙箱Bean，调用代码执行方法即可使用
    ```xml
    <dependency>
        <groupId>site.notcoder.oji</groupId>
        <artifactId>learncode-codesandbox-spring-boot-starter</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </dependency>
    ```
    ```java
    @Resource
    private CodeSandboxExecutor codeSandboxExecutor;
  
    @Test
    void testCodeSandbox() {
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
                                "1.in",
                                "6 6"
                        ),
                        new InputArg(
                                "2.in",
                                "3 5"
                        )
                )
        );
        ExecutorResponse executorResponse = codeSandboxExecutor.exec(executorRequest);
        Assertions.assertEquals(executorResponse.getMessage(), "执行成功");
    }
    ```
- Restful API, 通过发送Http请求，使用代码执行接口
    ```http request
    POST /api/exec HTTP/1.1
    Content-Type: application/json
    User-Agent: insomnia/2023.5.8
    Host: 127.0.0.1:8180
    Content-Length: 490
    
    {
        "code": "BASE64编码后的源代码",
        "lang": "java",
        "inputs": [
            {
                "id": "1.in",
                "data": "BASE64编码后的输入文件的内容"
            },
            {
                "id": "2.in",
                "data": "BASE64编码后的输入文件的内容"
            }
        ]
    }
    ```