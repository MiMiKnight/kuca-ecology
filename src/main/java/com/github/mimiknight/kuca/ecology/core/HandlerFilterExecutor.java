package com.github.mimiknight.kuca.ecology.core;

import com.github.mimiknight.kuca.ecology.filter.EcologyHandlerFilter;
import com.github.mimiknight.kuca.ecology.filter.HandlerFilterBox;
import com.github.mimiknight.kuca.ecology.filter.HandlerFilterChain;
import com.github.mimiknight.kuca.ecology.filter.HandlerFilterChainFactory;
import com.github.mimiknight.kuca.ecology.handler.EcologyRequestHandler;
import com.github.mimiknight.kuca.ecology.model.request.EcologyRequest;
import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * Handler过滤器执行器
 *
 * @author MiMiKnight victor2015yhm@gmail.com
 * @since 2023-09-09 00:19:00
 */
public class HandlerFilterExecutor {

    private HandlerFilterBox handlerFilterBox;

    @Autowired
    public void setHandlerFilterBox(HandlerFilterBox handlerFilterBox) {
        this.handlerFilterBox = handlerFilterBox;
    }

    private HandlerFilterChain filterChain;

    /**
     * @param <Q>      接口入参泛型
     * @param <P>      接口出参泛型
     * @param <H>      业务处理器泛型
     * @param request  接口入参
     * @param response 接口出参
     * @param handler  业务处理器
     * @throws Exception 被抛出的异常
     */
    public <Q extends EcologyRequest, P extends EcologyResponse, H extends EcologyRequestHandler<Q, P>> void execute(Q request,
                                                                                                                     P response,
                                                                                                                     H handler) throws Exception {
        init(request, handler);
        filterChain.doFilter(request, response);
    }

    /**
     * 初始化过滤器链
     *
     * @param <Q>     接口入参泛型
     * @param <P>     接口出参泛型
     * @param <H>     业务处理器泛型
     * @param request 接口入参
     * @param handler 业务处理器
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private <Q extends EcologyRequest, P extends EcologyResponse, H extends EcologyRequestHandler<Q, P>> void init(Q request, H handler) {
        filterChain = HandlerFilterChainFactory.getFilterChain();
        filterChain.setTarget(handler);

        ConcurrentMap<Class<EcologyRequest>, List<EcologyHandlerFilter>> handlerFilterMap = handlerFilterBox.getHandlerFilterMap();
        if (MapUtils.isEmpty(handlerFilterMap)) {
            return;
        }
        List<EcologyHandlerFilter> filters = handlerFilterMap.get(request.getClass());
        if (CollectionUtils.isEmpty(filters)) {
            return;
        }
        filterChain.addFilter(filters);

    }
}
