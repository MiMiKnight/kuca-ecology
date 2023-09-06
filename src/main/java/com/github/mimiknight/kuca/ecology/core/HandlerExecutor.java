package com.github.mimiknight.kuca.ecology.core;

import com.github.mimiknight.kuca.ecology.exception.HandlerNotFoundException;
import com.github.mimiknight.kuca.ecology.handler.EcologyRequestHandler;
import com.github.mimiknight.kuca.ecology.handler.HandlerBox;
import com.github.mimiknight.kuca.ecology.interceptor.HandlerInterceptorBox;
import com.github.mimiknight.kuca.ecology.model.request.EcologyRequest;
import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;
import com.github.mimiknight.kuca.ecology.model.response.SuccessResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

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

    private HandlerInterceptorBox handlerInterceptorBox;

    @Autowired
    public void setHandlerInterceptorBox(HandlerInterceptorBox handlerInterceptorBox) {
        this.handlerInterceptorBox = handlerInterceptorBox;
    }

    PlatformTransactionManager transactionManager;

    @Autowired
    public void setPlatformTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

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
        Class<?> responseClass = handlerBox.getHandlerResponseMap().get(handler);
        if (null == responseClass) {
            String handlerName = handler.getClass().getSimpleName();
            log.error("The class of response is not exist,handler name = {}", handlerName);
            throw new ClassNotFoundException("The class object of response is not exist.");
        }
        // 实例化响应对象
        T response = (T) responseClass.getDeclaredConstructor().newInstance();
        // 事务中执行业务逻辑
        doTransaction(request, response, handler);
        // 构建成功响应
        return buildSuccessResponse(response);
    }

    /**
     * 在手动控制的事务中执行接口业务逻辑
     *
     * @param request  请求参数
     * @param response 响应参数
     * @param handler  执行器
     * @throws Exception 异常
     */
    private <R extends EcologyRequest,
            T extends EcologyResponse,
            H extends EcologyRequestHandler<R, T>> void doTransaction(R request, T response, H handler) throws Exception {
        // 定义事务属性
        DefaultTransactionDefinition transactionDefinition = new DefaultTransactionDefinition();
        // 开启事务
        TransactionStatus transactionStatus = transactionManager.getTransaction(transactionDefinition);
        try {
            // 执行前置拦截器
            handlerInterceptorBox.doBeforeInterceptor(request);
            // 执行handle方法
            handler.handle(request, response);
            // 执行后置拦截器
            handlerInterceptorBox.doAfterReturnInterceptor(request, response);
            // 业务正常执行完毕，事务提交
            transactionManager.commit(transactionStatus);
        } catch (Exception e) {
            log.error("Transaction rollback,error = {}", e.getMessage());
            // 业务发生异常，事务回滚
            transactionManager.rollback(transactionStatus);
            throw e;
        }
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
        H handler = (H) handlerBox.getRequestHandlerMap().get(request.getClass());
        if (null == handler) {
            String requestName = request.getClass().getSimpleName();
            log.error("The handler is undefined or not managed by spring,request class name = {}", requestName);
            throw new HandlerNotFoundException("The handler is undefined or not managed by spring.");
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
