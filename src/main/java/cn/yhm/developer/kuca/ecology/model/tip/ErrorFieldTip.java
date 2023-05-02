package cn.yhm.developer.kuca.ecology.model.tip;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 字段校验错误提示类
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-03-11 00:02:03
 */
public class ErrorFieldTip extends ErrorTip {

    @JsonProperty(value = "field")
    private String field;


    public ErrorFieldTip(String field, String tip) {
        super(tip);
        this.field = field;
    }

    /**
     * 构建ErrorFieldTip
     *
     * @param field 字段名称
     * @param tip   提示信息
     * @return {@link ErrorFieldTip}
     */
    public static ErrorFieldTip build(String field, String tip) {
        return new ErrorFieldTip(field, tip);
    }

    @Override
    public void setTip(String tip) {
        super.setTip(tip);
    }

    @Override
    public String getTip() {
        return super.getTip();
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
