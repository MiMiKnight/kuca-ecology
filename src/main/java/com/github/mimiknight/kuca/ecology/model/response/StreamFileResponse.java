package com.github.mimiknight.kuca.ecology.model.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * StreamingResponseBody 文件响应对象
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-03-09 19:58:20
 */
@Getter
@Setter
public class StreamFileResponse extends FileResponse<StreamingResponseBody> {
}
