package com.github.mimiknight.kuca.ecology.core;

import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;
import com.github.mimiknight.kuca.ecology.model.response.FileResponse;
import com.github.mimiknight.kuca.ecology.model.response.SuccessResponse;
import org.springframework.http.ResponseEntity;

/**
 * 成功响应建造者
 *
 * @author MiMiKnight victor2015yhm@gmail.com
 * @since 2023-09-10 12:53:44
 */
public class SuccessResponseBuilder {

    private SuccessResponseBuilder() {
    }

    /**
     * 构建成功响应
     *
     * @param <P>      响应参数泛型
     * @param <B>      接口响应Body泛型
     * @param response 响应参数
     * @return {@link ResponseEntity}
     */
    @SuppressWarnings({"unchecked"})
    public static <B, P extends EcologyResponse> ResponseEntity<B> build(P response) {
        if (response instanceof FileResponse) {
            return FileResponse.buildResponse((FileResponse<B>) response);
        }
        return SuccessResponse.buildResponse(response);
    }
}
