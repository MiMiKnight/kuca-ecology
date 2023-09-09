package com.github.mimiknight.kuca.ecology.core;

import com.github.mimiknight.kuca.ecology.filter.EcologyHandlerFilter;
import com.github.mimiknight.kuca.ecology.filter.HandlerFilterBox;
import com.github.mimiknight.kuca.ecology.filter.HandlerFilterChain;
import com.github.mimiknight.kuca.ecology.filter.HandlerFilterChainFactory;
import com.github.mimiknight.kuca.ecology.handler.EcologyRequestHandler;
import com.github.mimiknight.kuca.ecology.model.request.EcologyRequest;
import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Handler过滤器执行器
 *
 * @author MiMiKnight victor2015yhm@gmail.com
 * @since 2023-09-09 00:19:00
 */
public class HandlerFilterExecutor {

    @Autowired
    private HandlerFilterBox handlerFilterBox;

    /**
     * 过滤链执行
     *
     * @param <Q>      接口入参泛型
     * @param <P>      接口出参泛型
     * @param <H>      业务执行器泛型
     * @param request  接口入参
     * @param response 接口出参
     * @param handler  业务执行器
     * @throws Exception 被抛出的异常
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <Q extends EcologyRequest,
            P extends EcologyResponse,
            H extends EcologyRequestHandler<Q, P>> void execute(Q request, P response, H handler) throws Exception {
        Class<EcologyRequest> requestClass = (Class<EcologyRequest>) request.getClass();
        // 获取过滤器
        List<EcologyHandlerFilter> filters = handlerFilterBox.getHandlerFilterByKey(requestClass);
        // 获取过滤器链
        HandlerFilterChain filterChain = HandlerFilterChainFactory.getFilterChain();
        // 初始化过滤器链参数
        filterChain.init(handler, filters);
        // 执行过滤器
        filterChain.doFilter(request, response);
    }
}
