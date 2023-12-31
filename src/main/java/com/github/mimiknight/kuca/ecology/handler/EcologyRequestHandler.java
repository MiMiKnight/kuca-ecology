package com.github.mimiknight.kuca.ecology.handler;

import com.github.mimiknight.kuca.ecology.model.request.EcologyRequest;
import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;

/**
 * 请求处理器接口
 *
 * @author victor2015yhm@gmail.com
 * @since 2022-10-06 18:41:11
 */
public interface EcologyRequestHandler<Q extends EcologyRequest, P extends EcologyResponse> {

    /**
     * 处理方法
     *
     * @param request  请求参数
     * @param response 响应参数
     * @throws Exception 异常
     */
    void handle(Q request, P response) throws Exception;

}
