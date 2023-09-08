package com.github.mimiknight.kuca.ecology.core;

import com.github.mimiknight.kuca.ecology.handler.EcologyRequestHandler;
import com.github.mimiknight.kuca.ecology.interceptor.EcologyHandlerInterceptor;
import com.github.mimiknight.kuca.ecology.interceptor.HandlerInterceptorBox;
import com.github.mimiknight.kuca.ecology.model.request.EcologyRequest;
import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;

/**
 * Handler 拦截器执行器
 *
 * @author MiMiKnight victor2015yhm@gmail.com
 * @since 2023-09-08 08:31:07
 */
@Slf4j
public class HandlerInterceptorExecutor {

    private HandlerInterceptorBox interceptorBox;

    @Autowired
    public void setInterceptorBox(HandlerInterceptorBox interceptorBox) {
        this.interceptorBox = interceptorBox;
    }


    /**
     * 拦截器执行方法
     * <p>
     * 当系统中没有注册的拦截器或者当前handler下没有注册的拦截器，则方法返回false，否则返回true
     *
     * @param <Q>      接口入参泛型
     * @param <P>      接口出参泛型
     * @param <H>      接口处理器泛型
     * @param request  接口入参
     * @param response 接口出参
     * @param handler  业务处理器
     * @return boolean
     * @throws Exception 被抛出的异常
     */
    public <Q extends EcologyRequest, P extends EcologyResponse, H extends EcologyRequestHandler<Q, P>> boolean execute(Q request, P response, H handler) throws Exception {
        Assert.notNull(request, "The request argument is required; it must not be null");
        Assert.notNull(response, "The response argument is required; it must not be null");
        Assert.notNull(handler, "The handler argument is required; it must not be null");

        ConcurrentMap<Class<EcologyRequest>, TreeSet<EcologyHandlerInterceptor<?, ?, ?>>> interceptorMap =
                interceptorBox.getHandlerInterceptorMap();
        // 系统中没有注册的拦截器
        if (MapUtils.isEmpty(interceptorMap)) {
            return false;
        }
        Class<?> requestClass = AopUtils.getTargetClass(request);
        TreeSet<EcologyHandlerInterceptor<?, ?, ?>> interceptors = interceptorMap.get(requestClass);
        // 当前handler下没有注册的拦截器
        if (CollectionUtils.isEmpty(interceptors)) {
            return false;
        }
        // 执行拦截器
        doInterceptor(interceptors, request, response, handler);
        return true;
    }


    /**
     * 执行拦截器
     * <p>
     * 任意一个拦截方法返回false则不执行后续拦截，返回true则继续执行后续拦截逻辑
     *
     * @param <Q>          接口入参泛型
     * @param <P>          接口出参泛型
     * @param <H>          接口处理器泛型
     * @param interceptors 拦截器集合
     * @param request      接口入参
     * @param response     接口出参
     * @param handler      业务处理器
     * @throws Exception 被抛出异常
     */
    private <Q extends EcologyRequest, P extends EcologyResponse, H extends EcologyRequestHandler<Q, P>> void doInterceptor(TreeSet<EcologyHandlerInterceptor<?, ?, ?>> interceptors,
                                                                                                                            Q request,
                                                                                                                            P response,
                                                                                                                            H handler) throws Exception {
        // 批量执行前置拦截
        if (!applyDoBefore(interceptors, request, response)) {
            return;
        }
        // 执行handler
        handler.handle(request, response);
        // 批量执行后置拦截
        applyDoAfterReturn(interceptors, request, response);
    }

    /**
     * 批量执行拦截器的前置拦截方法
     * <p>
     * 任意一个拦截方法返回false则不执行后续拦截，返回true则继续执行后续拦截逻辑
     *
     * @param interceptors 拦截器集合
     * @param request      接口入参
     * @param response     接口出参
     * @return boolean
     * @throws Exception 被抛出的异常
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private <Q extends EcologyRequest, P extends EcologyResponse> boolean applyDoBefore(TreeSet<EcologyHandlerInterceptor<?, ?, ?>> interceptors,
                                                                                        Q request,
                                                                                        P response) throws Exception {
        boolean result;
        for (EcologyHandlerInterceptor interceptor : interceptors) {
            result = interceptor.doBefore(request, response);
            if (!result) {
                return false;
            }
        }
        return true;
    }

    /**
     * 批量执行拦截器的后置拦截方法
     * <p>
     * 任意一个拦截方法返回false则不执行后续拦截，返回true则继续执行后续拦截逻辑
     *
     * @param interceptors 拦截器集合
     * @param request      接口入参
     * @param response     接口出参
     * @throws Exception 被抛出的异常
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private <Q extends EcologyRequest, P extends EcologyResponse> void applyDoAfterReturn(TreeSet<EcologyHandlerInterceptor<?, ?, ?>> interceptors,
                                                                                          Q request,
                                                                                          P response) throws Exception {
        boolean result;
        for (EcologyHandlerInterceptor interceptor : interceptors) {
            result = interceptor.doAfterReturn(request, response);
            if (!result) {
                return;
            }
        }
    }

}
