package site.notcoder.oji.ojicodesandbox.model.sandbox;

import lombok.Getter;

/**
 * 编程语言策略枚举
 */

@Getter
public enum LangStrategyEnum {
    JAVA("java", "java"),
    CPP("cpp", "cpp");

    private final String text;
    private final String  value;

    LangStrategyEnum(String text, String  value) {
        this.text = text;
        this.value = value;
    }

    public static LangStrategyEnum getEnumByValue(String value) {
        for (LangStrategyEnum langStrategyEnum : values()) {
            if (langStrategyEnum.value.equals(value)) {
                return langStrategyEnum;
            }
        }
        return null;
    }
}
