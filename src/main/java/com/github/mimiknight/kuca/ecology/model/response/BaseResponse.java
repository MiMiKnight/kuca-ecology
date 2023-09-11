package com.github.mimiknight.kuca.ecology.model.response;

import lombok.Getter;
import lombok.Setter;

/**
 * 响应抽象类
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-03-09 19:58:20
 */
@Getter
@Setter
public abstract class BaseResponse<B> implements EcologyResponse {

    /**
     * 响应体
     */
    private B body;

}
