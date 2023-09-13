package com.github.mimiknight.kuca.ecology.model.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * StreamingResponseBody 流式响应对象
 * <p>
 * 一般在文件下载时使用
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-03-09 19:58:20
 */
@Getter
@Setter
public final class StreamingResponse extends BaseResponse<StreamingResponseBody> {
}
