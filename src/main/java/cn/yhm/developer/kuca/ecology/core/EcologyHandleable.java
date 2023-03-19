package cn.yhm.developer.kuca.ecology.core;

import cn.yhm.developer.kuca.ecology.model.request.EcologyRequest;
import cn.yhm.developer.kuca.ecology.model.response.EcologyResponse;

/**
 * 处理器接口
 *
 * @author victor2015yhm@gmail.com
 * @since 2022-10-06 18:41:11
 */
public interface EcologyHandleable<R extends EcologyRequest, T extends EcologyResponse> {

    /**
     * 处理方法
     *
     * @param request  请求参数
     * @param response 响应参数
     * @throws Exception 异常
     */
    void handle(R request, T response) throws Exception;


}
