package cn.yhm.developer.kuca.ecology.core;

import cn.yhm.developer.kuca.ecology.model.request.EcologyRequest;
import cn.yhm.developer.kuca.ecology.model.response.EcologyResponse;
import cn.yhm.developer.kuca.ecology.model.response.SuccessResponse;
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
public class HandlerExecutor<R extends EcologyRequest, T extends EcologyResponse, H extends EcologyRequestHandler<R, T>> {

    /**
     * 异常信息常量
     */
    private interface ExceptionMessage {
        String MSG_001 = "The handler is not exist or not managed by spring.";
        String MSG_002 = "The class object of response is not exist.";
    }

    private HandlerContainer handlerContainer;

    private HandlerInterceptorContainer<R, T, ?, ?> handlerInterceptorContainer;

    @Autowired
    public void setHandlerInterceptorContainer(HandlerInterceptorContainer<R, T, ?, ?> handlerInterceptorContainer) {
        this.handlerInterceptorContainer = handlerInterceptorContainer;
    }

    @Autowired
    public void setHandlerContainer(HandlerContainer handlerContainer) {
        this.handlerContainer = handlerContainer;
    }


    /**
     * 执行方法
     *
     * @param request 请求参数对象
     * @return {@link T} 响应
     * @throws Exception 异常
     */
    @SuppressWarnings({"unchecked"})
    public SuccessResponse<T> execute(R request) throws Exception {
        // 通过请求参数Class获取handler
        H handler = (H) handlerContainer.getRequestHandlerMap().get(request.getClass());
        if (null == handler) {
            log.error(ExceptionMessage.MSG_001);
            throw new RuntimeException(ExceptionMessage.MSG_001);
        }
        return execute(request, handler);
    }

    /**
     * 执行方法
     *
     * @param request 请求参数对象
     * @param handler 处理器对象
     * @return {@link T} 响应
     * @throws Exception 异常
     */
    @SuppressWarnings({"unchecked"})
    public SuccessResponse<T> execute(R request, H handler) throws Exception {
        Class<?> responseClass = handlerContainer.getHandlerResponseMap().get(handler);
        if (null == responseClass) {
            log.error(ExceptionMessage.MSG_002);
            throw new RuntimeException(ExceptionMessage.MSG_002);
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
     * 构建成功响应参数封装对象
     *
     * @param response 响应参数
     * @return {@link SuccessResponse}<{@link T}>
     */
    private SuccessResponse<T> buildSuccessResponse(T response) {
        SuccessResponse<T> successResponse = new SuccessResponse<>();
        successResponse.setData(response);
        successResponse.setHttpStatus(HttpStatus.OK.value());
        successResponse.setTimestamp(ZonedDateTime.now());
        return successResponse;
    }
}
