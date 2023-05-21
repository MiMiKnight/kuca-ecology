package cn.yhm.developer.kuca.ecology.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

/**
 * 异常响应
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-03-09 19:47:19
 */
@Builder
@Getter
@Setter
public class ExceptionResponse<T> implements EcologyResponse {

    /**
     * HTTP响应状态码
     */
    @JsonProperty(value = "http_status", index = 1)
    private int httpStatus;

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
     * 时间戳
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS z")
    @JsonProperty(value = "timestamp", index = 4)
    private ZonedDateTime timestamp;

    /**
     * 错误信息
     */
    @JsonProperty(value = "data", index = 5)
    T data;
}
