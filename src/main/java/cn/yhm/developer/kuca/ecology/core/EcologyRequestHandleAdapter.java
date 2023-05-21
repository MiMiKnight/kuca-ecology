package cn.yhm.developer.kuca.ecology.core;

import cn.yhm.developer.kuca.ecology.model.request.EcologyRequest;
import cn.yhm.developer.kuca.ecology.model.response.EcologyResponse;
import cn.yhm.developer.kuca.ecology.model.response.SuccessResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 适配handler方法的抽象类
 *
 * @author victor2015yhm@gmail.com
 * @since 2022-09-05 01:15:27
 */
@Slf4j
@SuppressWarnings({"rawtypes", "unchecked"})
@Component
public abstract class EcologyRequestHandleAdapter {

    /**
     * 异常信息常量
     */
    private interface ExceptionMessage {
        String MSG_001 = "The handlerClass can not be null.";
        String MSG_002 = "'{}' has not managed by spring.";
    }

    private ApplicationContext appContext;

    private HandlerExecutor handlerExecutor;

    @Autowired
    public void setAppContext(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    @Autowired
    public void setHandlerExecutor(HandlerExecutor executor) {
        this.handlerExecutor = executor;
    }

    /**
     * 处理方法
     *
     * @param <R>          请求对象类
     * @param <T>          响应对象类
     * @param <H>          处理对象类
     * @param request      请求参数
     * @param handlerClass 处理类对象
     * @return response 响应参数
     * @throws Exception 异常
     */
    public <T extends EcologyResponse,
            R extends EcologyRequest,
            H extends EcologyRequestHandler<R, T>> SuccessResponse<T> handle(R request,
                                                                             Class<H> handlerClass) throws Exception {
        if (null == handlerClass) {
            throw new IllegalArgumentException(ExceptionMessage.MSG_001);
        }
        H handler;
        try {
            handler = appContext.getBean(handlerClass);
        } catch (NoSuchBeanDefinitionException e) {
            // handler未被Spring管理
            log.error(ExceptionMessage.MSG_002, handlerClass.getSimpleName());
            throw e;
        }
        return handlerExecutor.execute(request, handler);
    }

    /**
     * 处理方法
     *
     * @param request 请求参数
     * @return response 响应参数
     * @throws Exception 异常
     */
    public <T extends EcologyResponse,
            R extends EcologyRequest> SuccessResponse<T> handle(R request) throws Exception {
        return handlerExecutor.execute(request);
    }

}
