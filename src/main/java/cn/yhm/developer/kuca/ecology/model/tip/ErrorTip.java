package cn.yhm.developer.kuca.ecology.model.tip;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 错误提示类
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-03-11 00:08:41
 */
public class ErrorTip {

    @JsonProperty(value = "tip")
    private String tip;

    ErrorTip(String tip) {
        this.tip = tip;
    }

    /**
     * 构建ErrorTip
     *
     * @param tip 提示信息
     * @return {@link ErrorTip}
     */
    public static ErrorTip build(String tip) {
        return new ErrorTip(tip);
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public String getTip() {
        return tip;
    }
}
