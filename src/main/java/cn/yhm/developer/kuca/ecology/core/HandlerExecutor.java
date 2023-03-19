package cn.yhm.developer.kuca.ecology.core;

import cn.yhm.developer.kuca.ecology.init.HandlerInterceptorContainer;
import cn.yhm.developer.kuca.ecology.init.RequestResponseHandlerContainer;
import cn.yhm.developer.kuca.ecology.model.request.EcologyRequest;
import cn.yhm.developer.kuca.ecology.model.response.EcologyResponse;
import cn.yhm.developer.kuca.ecology.model.response.ResultResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;

/**
 * Handler执行器类
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-02-28 21:23:05
 */
@Slf4j
@Component
public class HandlerExecutor<R extends EcologyRequest, T extends EcologyResponse, H extends EcologyHandleable<R, T>> {

    private RequestResponseHandlerContainer handlerContainer;

    private HandlerInterceptorContainer<R, T, ?, ?> handlerInterceptorContainer;

    @Autowired
    public void setHandlerInterceptorContainer(HandlerInterceptorContainer<R, T, ?, ?> handlerInterceptorContainer) {
        this.handlerInterceptorContainer = handlerInterceptorContainer;
    }

    @Autowired
    public void setHandlerContainer(RequestResponseHandlerContainer handlerContainer) {
        this.handlerContainer = handlerContainer;
    }


    /**
     * 执行方法
     *
     * @param request 请求参数对象
     * @return {@link T} 响应
     * @throws Exception 异常
     */
    public ResultResponse<T> execute(R request) throws Exception {
        // 通过请求参数Class获取handler
        H handler = (H) handlerContainer.getRequestHandlerMap().get(request.getClass());
        if (null == handler) {
            log.error("The handler is not exist or not managed by spring.");
            throw new RuntimeException("The handler is not exist or not managed by spring.");
        }
        return execute(request, handler);
    }

    /**
     * 执行方法
     *
     * @param request 请求参数对象
     * @param handler 处理期对象
     * @return {@link T} 响应
     * @throws Exception 异常
     */
    public ResultResponse<T> execute(R request, H handler) throws Exception {
        Class<?> responseClass = handlerContainer.getHandlerResponseMap().get(handler);
        if (null == responseClass) {
            log.error("The class object of response is not exist.");
            throw new RuntimeException("The class object of response is not exist.");
        }
        T response = (T) responseClass.getDeclaredConstructor().newInstance();
        // 执行前置拦截器
        handlerInterceptorContainer.doBeforeInterceptor(request);
        // 执行handle方法
        handler.handle(request, response);
        // 执行后置拦截器
        handlerInterceptorContainer.doAfterReturnInterceptor(request, response);
        return buildResultResponse(response);
    }

    /**
     * 赋值结果响应参数对象
     *
     * @param response 响应参数
     * @return {@link ResultResponse}<{@link T}>
     */
    private ResultResponse<T> buildResultResponse(T response) {
        ResultResponse<T> resultResponse = new ResultResponse<>();
        resultResponse.setData(response);
        resultResponse.setHttpStatus(HttpStatus.OK.value());
        resultResponse.setTimestamp(ZonedDateTime.now());
        return resultResponse;
    }
}
