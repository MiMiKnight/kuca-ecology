package cn.yhm.developer.kuca.ecology.model.response;

import cn.yhm.developer.kuca.common.constant.DateTimeFormatStandard;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

/**
 * 请求成功响应参数封装对象
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-03-09 19:58:20
 */
@Builder
@Data
public class SuccessResponse implements EcologyResponse {

    /**
     * HTTP响应状态码
     */
    @JsonProperty(value = "status_code", index = 1)
    private int statusCode;

    /**
     * 接口响应时间戳
     */
    @JsonFormat(pattern = DateTimeFormatStandard.STANDARD_4)
    @JsonProperty(value = "timestamp", index = 2)
    private ZonedDateTime timestamp;

    /**
     * 响应数据
     */
    @JsonProperty(value = "data", index = 3)
    Object data;
}
