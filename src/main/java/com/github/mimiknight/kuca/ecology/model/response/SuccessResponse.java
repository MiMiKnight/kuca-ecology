package com.github.mimiknight.kuca.ecology.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

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
    private Object data;

    /**
     * 构建成功响应参数封装对象
     *
     * @param <B>      接口响应Body泛型
     * @param <P>      响应参数泛型
     * @param response 响应参数
     * @return {@link SuccessResponse}
     */
    @SuppressWarnings({"unchecked"})
    public static <B, P extends EcologyResponse> ResponseEntity<B> buildResponse(P response) {
        B body = (B) SuccessResponse.builder().statusCode(HttpStatus.OK.value()).data(response).build();
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }
}
