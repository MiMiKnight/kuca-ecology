package cn.yhm.developer.kuca.ecology.interceptor;

import cn.yhm.developer.kuca.ecology.model.request.EcologyRequest;
import cn.yhm.developer.kuca.ecology.model.response.EcologyResponse;

/**
 * Handler前置拦截器接口
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-03-15 18:14:28
 */
public interface HandlerAfterReturnInterceptor<R extends EcologyRequest, T extends EcologyResponse> extends EcologyHandlerInterceptor {

    /**
     * 拦截器方法
     *
     * @param request  请求参数
     * @param response 响应参数
     */
    void intercept(R request, T response);
}
