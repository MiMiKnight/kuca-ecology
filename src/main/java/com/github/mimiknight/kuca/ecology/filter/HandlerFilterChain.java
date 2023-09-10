package com.github.mimiknight.kuca.ecology.filter;

import com.github.mimiknight.kuca.ecology.model.request.EcologyRequest;
import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;

/**
 * 过滤器链接口
 *
 * @author MiMiKnight victor2015yhm@gmail.com
 * @since 2023-09-09 00:13:22
 */
public interface HandlerFilterChain {

    /**
     * 过滤器方法体
     *
     * @param <Q>      接口入参泛型
     * @param <P>      接口出参泛型
     * @param request  接口入参
     * @param response 接口出参
     * @throws Exception 被抛出的异常
     */
    <Q extends EcologyRequest,
            P extends EcologyResponse> void doFilter(Q request, P response) throws Exception;
}
