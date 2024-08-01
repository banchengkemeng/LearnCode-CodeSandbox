package site.notcoder.oji.ojicodesandbox.utils;

import cn.hutool.core.io.FileUtil;
import site.notcoder.oji.ojicodesandbox.model.sandbox.CompileInfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 编译Java的工具类
 */

public class JavaCompileUtils {
    public static String writeFile(String code, String uuid) {
        File file = FileUtil.writeBytes(code.getBytes(), String.format("%s/Main.java", uuid));
        return file.getAbsolutePath();
    }

    public static Boolean deleteFile(String uuid) {
        return FileUtil.del(uuid);
    }

    public static CompileInfo compile(String uuid) throws Exception {
        CompileInfo compileInfo = new CompileInfo();

        // 获取代码文件路径
        String path = FileUtil.getAbsolutePath(uuid);

        // 生成process对象
        Process start = Runtime.getRuntime().exec(String.format("javac -encoding utf-8 %s/Main.java", path));

        // 等待命令执行完成
        start.waitFor();

        // 拿到返回值
        int i = start.exitValue();
        if (i == 0) {
            compileInfo.setSuccess(true);
            compileInfo.setMessage(getStringFromInputStream(start.getInputStream()));
        } else {
            compileInfo.setSuccess(false);
            compileInfo.setMessage(getStringFromInputStream(start.getErrorStream()));
        }

        // 销毁process对象
        start.destroy();

        return compileInfo;
    }

    private static String getStringFromInputStream(InputStream inputStream) throws Exception {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder message = new StringBuilder();
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            message.append(line).append("\n");
        }
        return message.toString();
    }
}
