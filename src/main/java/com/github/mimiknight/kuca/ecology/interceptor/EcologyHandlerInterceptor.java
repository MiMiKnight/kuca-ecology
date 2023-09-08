package com.github.mimiknight.kuca.ecology.interceptor;

import com.github.mimiknight.kuca.ecology.handler.EcologyRequestHandler;
import com.github.mimiknight.kuca.ecology.model.request.EcologyRequest;
import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;
import org.springframework.core.Ordered;

/**
 * 请求Handler拦截器接口
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-03-15 18:12:09
 */
public interface EcologyHandlerInterceptor<Q extends EcologyRequest, P extends EcologyResponse, H extends EcologyRequestHandler<Q, P>, I extends EcologyHandlerInterceptor<Q, P, H, I>> extends Ordered, Comparable<I> {

    /**
     * 执行前置拦截
     *
     * @param request  接口入参
     * @param response 接口出参
     * @return boolean
     * @throws Exception 抛出异常
     */
    default boolean doBefore(Q request, P response) throws Exception {
        return true;
    }

    /**
     * 执行环绕拦截
     *
     * @param request  接口入参
     * @param response 接口出参
     * @param handler  业务处理器
     * @return boolean
     * @throws Exception 抛出异常
     */
    default boolean doAround(Q request, P response, H handler) throws Exception {
        handler.handle(request, response);
        return true;
    }

    /**
     * 执行后置拦截
     *
     * @param request  接口入参
     * @param response 接口出参
     * @return boolean
     * @throws Exception 抛出异常
     */
    default boolean doAfterReturn(Q request, P response) throws Exception {
        return true;
    }

    /**
     * 排序比较
     *
     * @param interceptor 被比较地拦截器对象
     * @return int
     */
    @Override
    default int compareTo(I interceptor) {
        return Integer.compare(this.getOrder(), interceptor.getOrder());
    }
}
