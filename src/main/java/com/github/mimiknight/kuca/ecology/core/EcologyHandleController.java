package com.github.mimiknight.kuca.ecology.core;

import com.github.mimiknight.kuca.ecology.handler.EcologyRequestHandler;
import com.github.mimiknight.kuca.ecology.model.request.EcologyRequest;
import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;
import com.github.mimiknight.kuca.ecology.model.response.SuccessResponse;
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
     * @param request      请求参数
     * @param handlerClass 处理类对象
     * @param <T>          接口响应对象类型泛型
     * @param <R>          接口请求对象类型泛型
     * @param <H>          请求处理器对象类型泛型
     * @return response 响应参数
     * @throws Exception 异常
     */
    public <T extends EcologyResponse,
            R extends EcologyRequest,
            H extends EcologyRequestHandler<R, T>> SuccessResponse handle(R request,
                                                                          Class<H> handlerClass) throws Exception {
        Assert.notNull(request, "The request argument is required; it must not be null");
        Assert.notNull(handlerClass, "The handlerClass argument is required; it must not be null");

        H handler = appContext.getBean(handlerClass);
        return handlerExecutor.execute(request, handler);
    }

    /**
     * 处理方法
     *
     * @param <R>     请求参数对象类型
     * @param request 请求参数
     * @return response 响应参数
     * @throws Exception 异常
     */
    public <R extends EcologyRequest> SuccessResponse handle(R request) throws Exception {
        Assert.notNull(request, "The request argument is required; it must not be null");
        return handlerExecutor.execute(request);
    }

}
