package com.github.mimiknight.kuca.ecology.core;

import com.github.mimiknight.kuca.ecology.handler.EcologyRequestHandler;
import com.github.mimiknight.kuca.ecology.model.request.EcologyRequest;
import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

/**
 * 适配handler方法的抽象类
 *
 * @author victor2015yhm@gmail.com
 * @since 2022-09-05 01:15:27
 */
@Slf4j
public abstract class EcologyHandleController {
    @Autowired
    private ApplicationContext appContext;

    @Autowired
    private HandlerExecutor handlerExecutor;

    /**
     * 处理方法
     *
     * @param <Q>          接口入参泛型
     * @param <P>          接口出参泛型
     * @param <H>          业务处理器泛型
     * @param request      接口入参
     * @param handlerClass 业务处理器Class对象
     * @return response 响应参数
     * @throws Exception 异常
     */
    public <Q extends EcologyRequest,
            P extends EcologyResponse,
            H extends EcologyRequestHandler<Q, P>> P handle(Q request, Class<H> handlerClass) throws Exception {
        Assert.notNull(request, "The request argument is required; it must not be null");
        Assert.notNull(handlerClass, "The handlerClass argument is required; it must not be null");

        H handler = appContext.getBean(handlerClass);
        return handlerExecutor.execute(request, handler);
    }

    /**
     * 处理方法
     *
     * @param <Q>     接口入参泛型
     * @param <P>     接口出参泛型
     * @param request 接口入参
     * @return response 响应参数
     * @throws Exception 异常
     */
    public <Q extends EcologyRequest, P extends EcologyResponse> P handle(Q request) throws Exception {
        Assert.notNull(request, "The request argument is required; it must not be null");
        return handlerExecutor.execute(request);
    }

}
