package com.github.mimiknight.kuca.ecology.model.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;

/**
 * 请求参数接口
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-03-08 23:36:33
 */
@Setter
@Getter
public abstract class BaseRequest<Q, B> implements EcologyRequest {

    /**
     * 请求行查询参数
     */
    private Q query;

    /**
     * 请求头
     */
    private HttpHeaders headers;

    /**
     * 请求体
     */
    private B body;

    protected BaseRequest() {
        this.headers = new HttpHeaders();
    }
}
