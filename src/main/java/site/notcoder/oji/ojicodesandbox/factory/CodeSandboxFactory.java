package site.notcoder.oji.ojicodesandbox.factory;


import site.notcoder.oji.ojicodesandbox.exception.CodeSandboxExceptions;
import site.notcoder.oji.ojicodesandbox.factory.impl.ExampleCodeSandbox;
import site.notcoder.oji.ojicodesandbox.factory.impl.RemoteCodeSandbox;
import site.notcoder.oji.ojicodesandbox.factory.impl.ThirdPartyCodeSandbox;
import site.notcoder.oji.ojicodesandbox.proxy.CodeSandboxProxy;

/**
 * 工厂模式生产不同沙箱
 */

public class CodeSandboxFactory {
    public static CodeSandbox newInstance(String type) throws CodeSandboxExceptions {
        return new CodeSandboxProxy(newInstanceBeforeProxy(type));
    }

    private static CodeSandbox newInstanceBeforeProxy(String type) throws CodeSandboxExceptions{
        switch (type) {
            case "example":
                return new ExampleCodeSandbox();
            case "remote":
                return new RemoteCodeSandbox();
            case "thirdParty":
                return new ThirdPartyCodeSandbox();
            default:
                throw new CodeSandboxExceptions(String.format("不存在[%s]类型的代码沙箱", type));
        }
    }
}
