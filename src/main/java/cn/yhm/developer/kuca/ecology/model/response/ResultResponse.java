package cn.yhm.developer.kuca.ecology.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

/**
 * 结果响应参数对象
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-03-09 19:58:20
 */
@Setter
@Getter
public class ResultResponse<T> implements EcologyResponse {

    /**
     * HTTP响应状态码
     */
    @JsonProperty(value = "status_code", index = 1)
    private int statusCode;

    /**
     * 接口响应时间戳
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS z")
    @JsonProperty(value = "timestamp", index = 2)
    private ZonedDateTime timestamp;

    /**
     * 响应数据
     */
    @JsonProperty(value = "data", index = 3)
    T data;
}
