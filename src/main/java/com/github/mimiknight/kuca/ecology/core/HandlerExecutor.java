package com.github.mimiknight.kuca.ecology.core;

import com.github.mimiknight.kuca.ecology.exception.HandlerNotFoundException;
import com.github.mimiknight.kuca.ecology.handler.EcologyRequestHandler;
import com.github.mimiknight.kuca.ecology.handler.HandlerBox;
import com.github.mimiknight.kuca.ecology.model.request.EcologyRequest;
import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;
import com.github.mimiknight.kuca.ecology.model.response.SuccessResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Handler执行器类
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-02-28 21:23:05
 */
@Slf4j
@Component
public class HandlerExecutor {


    private HandlerBox handlerBox;

    @Autowired
    public void setHandlerBox(HandlerBox handlerBox) {
        this.handlerBox = handlerBox;
    }

    private HandlerInterceptorExecutor interceptorExecutor;

    @Autowired
    public void setHandlerInterceptorExecutor(HandlerInterceptorExecutor interceptorExecutor) {
        this.interceptorExecutor = interceptorExecutor;
    }

    /**
     * 执行方法
     *
     * @param <Q>     请求参数泛型
     * @param <P>     响应参数泛型
     * @param <H>     处理器泛型
     * @param request 请求参数对象
     * @return {@link P} 响应
     * @throws Exception 异常
     */
    @SuppressWarnings({"unchecked"})
    public <Q extends EcologyRequest,
            P extends EcologyResponse,
            H extends EcologyRequestHandler<Q, P>> SuccessResponse execute(Q request) throws Exception {
        // 通过请求参数Class获取handler
        H handler = (H) handlerBox.getRequestHandlerMap().get(request.getClass());
        if (null == handler) {
            String requestName = request.getClass().getSimpleName();
            log.error("The handler is undefined or not managed by spring,request class name = {}", requestName);
            throw new HandlerNotFoundException("The handler is undefined or not managed by spring.");
        }
        return execute(request, handler);
    }

    /**
     * 执行方法
     *
     * @param <Q>     请求参数泛型
     * @param <P>     响应参数泛型
     * @param <H>     处理器泛型
     * @param request 请求参数对象
     * @param handler 处理器对象
     * @return {@link P} 响应
     * @throws Exception 异常
     */
    @SuppressWarnings({"unchecked"})
    public <Q extends EcologyRequest,
            P extends EcologyResponse,
            H extends EcologyRequestHandler<Q, P>> SuccessResponse execute(Q request, H handler) throws Exception {
        Class<?> responseClass = handlerBox.getHandlerResponseMap().get(handler);
        if (null == responseClass) {
            String handlerName = handler.getClass().getSimpleName();
            log.error("The class of response is not exist,handler name = {}", handlerName);
            throw new ClassNotFoundException("The class object of response is not exist.");
        }
        // 实例化响应对象
        P response = (P) responseClass.getDeclaredConstructor().newInstance();
        // 执行业务逻辑
        doService(request, response, handler);
        // 构建成功响应
        return buildSuccessResponse(response);
    }

    /**
     * 执行业务逻辑
     *
     * @param request  请求参数
     * @param response 响应参数
     * @param handler  执行器
     * @throws Exception 异常
     */
    private <Q extends EcologyRequest,
            P extends EcologyResponse,
            H extends EcologyRequestHandler<Q, P>> void doService(Q request, P response, H handler) throws Exception {

        boolean result = interceptorExecutor.execute(request, response, handler);
        if (!result) {
            handler.handle(request, response);
        }
    }

    /**
     * 构建成功响应参数封装对象
     *
     * @param <P>      响应参数泛型
     * @param response 响应参数
     * @return {@link SuccessResponse}<{@link P}>
     */
    private <P extends EcologyResponse> SuccessResponse buildSuccessResponse(P response) {
        return SuccessResponse.builder().statusCode(HttpStatus.OK.value()).data(response).build();
    }
}
