package com.github.mimiknight.kuca.ecology.model.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

/**
 * 文件响应对象
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-03-09 19:58:20
 */
@Getter
@Setter
public class FileResponse<B> implements EcologyResponse {

    /**
     * 响应头
     */
    private HttpHeaders header;

    /**
     * 媒体类型
     */
    private MediaType mediaType;

    /**
     * 文件输入流 或者 StreamingResponseBody
     */
    private B body;

    /**
     * 构建成功响应参数封装对象
     *
     * @param <B>      文件输入流 或者 StreamingResponseBody
     * @param response 响应参数
     * @return {@link SuccessResponse}
     */
    public static <B> ResponseEntity<B> buildResponse(FileResponse<B> response) {
        return ResponseEntity.status(HttpStatus.OK)
                .headers(response.getHeader())
                .contentType(response.getMediaType())
                .body(response.getBody());
    }

}
