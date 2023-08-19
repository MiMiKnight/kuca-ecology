package com.github.mimiknight.kuca.ecology.core;

import com.github.mimiknight.kuca.ecology.exception.HandlerNotFoundException;
import com.github.mimiknight.kuca.ecology.model.request.EcologyRequest;
import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;
import com.github.mimiknight.kuca.ecology.model.response.SuccessResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

/**
 * Handler执行器类
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-02-28 21:23:05
 */
@Slf4j
public class HandlerExecutor {

    @Autowired
    private HandlerContainer handlerContainer;

    @Autowired
    private HandlerInterceptorContainer handlerInterceptorContainer;


    /**
     * 执行方法
     *
     * @param <R>     请求参数泛型
     * @param <T>     响应参数泛型
     * @param <H>     处理器泛型
     * @param request 请求参数对象
     * @param handler 处理器对象
     * @return {@link T} 响应
     * @throws Exception 异常
     */
    @SuppressWarnings({"unchecked"})
    public <R extends EcologyRequest,
            T extends EcologyResponse,
            H extends EcologyRequestHandler<R, T>> SuccessResponse execute(R request, H handler) throws Exception {
        Class<?> responseClass = handlerContainer.getHandlerResponseMap().get(handler);
        if (null == responseClass) {
            String handlerName = handler.getClass().getSimpleName();
            log.error("The class of response is not exist,handler name = {}", handlerName);
            throw new ClassNotFoundException("The class object of response is not exist.");
        }
        T response = (T) responseClass.getDeclaredConstructor().newInstance();
        // 执行前置拦截器
        handlerInterceptorContainer.doBeforeInterceptor(request);
        // 执行handle方法
        handler.handle(request, response);
        // 执行后置拦截器
        handlerInterceptorContainer.doAfterReturnInterceptor(request, response);
        return buildSuccessResponse(response);
    }

    /**
     * 执行方法
     *
     * @param <R>     请求参数泛型
     * @param <T>     响应参数泛型
     * @param <H>     处理器泛型
     * @param request 请求参数对象
     * @return {@link T} 响应
     * @throws Exception 异常
     */
    @SuppressWarnings({"unchecked"})
    public <R extends EcologyRequest,
            T extends EcologyResponse,
            H extends EcologyRequestHandler<R, T>> SuccessResponse execute(R request) throws Exception {
        // 通过请求参数Class获取handler
        H handler = (H) handlerContainer.getRequestHandlerMap().get(request.getClass());
        if (null == handler) {
            String requestName = request.getClass().getSimpleName();
            log.error("The handler is not exist or not managed by spring,request class name = {}", requestName);
            throw new HandlerNotFoundException("The handler is not exist or not managed by spring.");
        }
        return execute(request, handler);
    }

    /**
     * 构建成功响应参数封装对象
     *
     * @param <T>      响应参数泛型
     * @param response 响应参数
     * @return {@link SuccessResponse}<{@link T}>
     */
    private <T extends EcologyResponse> SuccessResponse buildSuccessResponse(T response) {
        return SuccessResponse.builder().statusCode(HttpStatus.OK.value()).data(response).build();
    }
}
