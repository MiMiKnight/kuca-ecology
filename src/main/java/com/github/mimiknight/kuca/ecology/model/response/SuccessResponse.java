package com.github.mimiknight.kuca.ecology.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

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
     * 响应数据
     */
    @JsonProperty(value = "data", index = 2)
    Object data;

    /**
     * 构建成功响应参数封装对象
     *
     * @param <P>      响应参数泛型
     * @param response 响应参数
     * @return {@link SuccessResponse}
     */
    public static <P extends EcologyResponse> SuccessResponse buildSuccessResponse(P response) {
        return SuccessResponse.builder().statusCode(HttpStatus.OK.value()).data(response).build();
    }
}
