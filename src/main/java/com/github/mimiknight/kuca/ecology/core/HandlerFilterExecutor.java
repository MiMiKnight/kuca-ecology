package com.github.mimiknight.kuca.ecology.core;

import com.github.mimiknight.kuca.ecology.filter.HandlerFilterChain;
import com.github.mimiknight.kuca.ecology.model.request.EcologyRequest;
import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Handler过滤器执行器
 *
 * @author MiMiKnight victor2015yhm@gmail.com
 * @since 2023-09-09 00:19:00
 */
public class HandlerFilterExecutor {

    private ConcurrentMap<Class<EcologyRequest>, HandlerFilterChain> handlerFilterChainMap;

    public HandlerFilterExecutor() {
        this.handlerFilterChainMap = new ConcurrentHashMap<>();
    }

    /**
     * @param <Q>      接口入参泛型
     * @param <P>      接口出参泛型
     * @param request  接口入参
     * @param response 接口出参
     * @throws Exception 被抛出的异常
     */
    public <Q extends EcologyRequest,
            P extends EcologyResponse> void execute(Q request, P response) throws Exception {
        HandlerFilterChain filterChain = this.handlerFilterChainMap.get(request.getClass());
        filterChain.doFilter(request, response);
        filterChain.resetPosition();
    }


}
