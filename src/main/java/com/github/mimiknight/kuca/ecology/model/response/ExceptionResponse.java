package com.github.mimiknight.kuca.ecology.model.response;

import cn.yhm.developer.kuca.common.constant.DateTimeFormatStandard;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

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
     * 时间戳
     */
    @JsonFormat(pattern = DateTimeFormatStandard.STANDARD_4)
    @JsonProperty(value = "timestamp", index = 2)
    private ZonedDateTime timestamp;

    /**
     * 错误码
     */
    @JsonProperty(value = "error_code", index = 3)
    private String errorCode;


    /**
     * 错误类型
     */
    @JsonProperty(value = "error_type", index = 4)
    private String errorType;

    /**
     * 错误信息
     */
    @JsonProperty(value = "data", index = 5)
    Object data;
}
