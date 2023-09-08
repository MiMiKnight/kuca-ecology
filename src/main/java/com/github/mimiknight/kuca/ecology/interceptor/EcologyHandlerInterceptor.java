package com.github.mimiknight.kuca.ecology.interceptor;

import com.github.mimiknight.kuca.ecology.model.request.EcologyRequest;
import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;
import org.springframework.core.Ordered;

/**
 * 请求Handler拦截器接口
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-03-15 18:12:09
 */
public interface EcologyHandlerInterceptor<Q extends EcologyRequest, P extends EcologyResponse> extends Ordered {

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
     * 执行后置返回拦截
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
     * 执行后置异常拦截
     *
     * @param request  接口入参
     * @param response 接口出参
     * @param ex       传入的异常参数
     * @return boolean
     * @throws Exception 抛出异常
     */
    default boolean doAfterThrowing(Q request, P response, Exception ex) throws Exception {
        throw ex;
    }

}
