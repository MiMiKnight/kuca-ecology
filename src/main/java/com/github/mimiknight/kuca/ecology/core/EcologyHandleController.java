package com.github.mimiknight.kuca.ecology.core;

import com.github.mimiknight.kuca.ecology.handler.EcologyRequestHandler;
import com.github.mimiknight.kuca.ecology.model.request.EcologyRequest;
import com.github.mimiknight.kuca.ecology.model.response.BaseResponse;
import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * 适配handler方法的抽象类
 *
 * @author victor2015yhm@gmail.com
 * @since 2022-09-05 01:15:27
 */
@Component
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

    /**
     * 处理方法
     * <p>
     * 仅限于继承自{@link BaseResponse}的响应
     *
     * @param <Q>           接口入参泛型
     * @param <P>           接口出参泛型
     * @param <B>           接口出参Body泛型
     * @param request       接口入参
     * @param responseClass 响应Class类型
     * @return {@link B}
     * @throws Exception 异常
     */
    public <B, Q extends EcologyRequest, P extends BaseResponse<B>> B handleAndGetBody(Q request, Class<P> responseClass) throws Exception {
        Assert.notNull(request, "The request argument is required; it must not be null");

        EcologyResponse eResponse = handlerExecutor.execute(request);

        if (!(eResponse instanceof BaseResponse)) {
            throw new IllegalStateException("Response is not inherited from BaseResponse.");
        }

        P response = responseClass.cast(eResponse);

        return response.getBody();
    }

}
