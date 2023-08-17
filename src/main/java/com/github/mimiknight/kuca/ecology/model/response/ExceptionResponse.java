package com.github.mimiknight.kuca.ecology.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * 异常响应
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-03-09 19:47:19
 */
@Builder
@Data
public class ExceptionResponse implements EcologyResponse {

    /**
     * HTTP响应状态码
     */
    @JsonProperty(value = "status_code", index = 1)
    private int statusCode;

    /**
     * 错误码
     */
    @JsonProperty(value = "error_code", index = 2)
    private String errorCode;

    /**
     * 错误类型
     */
    @JsonProperty(value = "error_type", index = 3)
    private String errorType;

    /**
     * 错误信息
     */
    @JsonProperty(value = "data", index = 4)
    Object data;
}
