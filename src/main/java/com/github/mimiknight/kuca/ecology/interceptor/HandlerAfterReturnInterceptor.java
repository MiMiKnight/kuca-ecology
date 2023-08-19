package com.github.mimiknight.kuca.ecology.interceptor;

import com.github.mimiknight.kuca.ecology.model.request.EcologyRequest;
import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;

/**
 * 请求Handler后置返回拦截器接口
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-03-15 18:14:28
 */
public interface HandlerAfterReturnInterceptor<R extends EcologyRequest, T extends EcologyResponse> extends EcologyHandlerInterceptor {

    /**
     * 拦截方法
     *
     * @param request  请求参数
     * @param response 响应参数
     * @throws Exception 异常
     */
    void intercept(R request, T response) throws Exception;
}
