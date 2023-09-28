package com.github.mimiknight.kuca.ecology.filter;

import com.github.mimiknight.kuca.ecology.model.request.EcologyRequest;
import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;
import org.springframework.core.Ordered;

/**
 * Handler过滤器接口
 *
 * @author MiMiKnight victor2015yhm@gmail.com
 * @since 2023-09-09 00:10:12
 */
public interface EcologyHandlerFilter<Q extends EcologyRequest, P extends EcologyResponse> extends Ordered {

    /**
     * 初始化方法
     */
    default void init() {
    }

    /**
     * 过滤器方法体
     *
     * @param request  接口入参
     * @param response 接口出参
     * @param chain    过滤器链
     * @throws Exception 被抛出的异常
     */
    void doFilter(Q request, P response, HandlerFilterChain chain) throws Exception;

    /**
     * 销毁方法
     */
    default void destroy() {
    }
}
