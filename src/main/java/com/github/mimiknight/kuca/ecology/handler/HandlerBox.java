package com.github.mimiknight.kuca.ecology.handler;

import com.github.mimiknight.kuca.ecology.exception.RequestRepeatBindException;
import com.github.mimiknight.kuca.ecology.model.request.EcologyRequest;
import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 装载Handler的容器
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-03-15 19:16:53
 */
@Slf4j
@SuppressWarnings({"rawtypes", "unchecked"})
public class HandlerBox {

    private interface Constant {

        /**
         * 初始化容量
         */
        int INIT_CAPACITY = 128;

        /**
         * handle方法名
         */
        String HANDLE_METHOD_NAME = "handle";

        /**
         * handle方法参数个数
         */
        int HANDLE_METHOD_PARAMETER_COUNT = 2;

    }

    @Autowired
    private ApplicationContext appContext;

    /**
     * Request与Handler映射Map
     */
    private final ConcurrentMap<Class<EcologyRequest>, EcologyRequestHandler> requestHandlerMap;

    /**
     * Request与Response映射Map
     */
    private final ConcurrentMap<Class<EcologyRequest>, Class<EcologyResponse>> requestResponseMap;

    public HandlerBox() {
        requestHandlerMap = new ConcurrentHashMap<>(Constant.INIT_CAPACITY);
        requestResponseMap = new ConcurrentHashMap<>(Constant.INIT_CAPACITY);
    }

    /**
     * 初始化方法
     */
    @PostConstruct
    public void init() {
        initRequestResponseHandlerMap();
    }

    /**
     * 初始化Map
     */
    private void initRequestResponseHandlerMap() {
        Map<String, EcologyRequestHandler> handlerMap = appContext.getBeansOfType(EcologyRequestHandler.class);
        if (MapUtils.isEmpty(handlerMap)) {
            return;
        }
        for (EcologyRequestHandler handler : handlerMap.values()) {
            for (Method method : AopUtils.getTargetClass(handler).getMethods()) {
                buildRequestResponseHandlerMap(handler, method);
            }
        }
    }

    /**
     * 当前方法是否为Handler的默认handle方法
     * <p>
     * public修改的方法；
     * 方法为非人工合成的；
     * 方法名为handle；
     * 方法参数个数为2；
     * 方法的第1个参数实现了EcologyRequest接口；
     * 方法的第2个参数实现了EcologyResponse接口；
     *
     * @param method 方法
     * @return boolean
     */
    private boolean isHandleMethod(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return Modifier.isPublic(method.getModifiers())
                && !method.isSynthetic()
                && Constant.HANDLE_METHOD_NAME.equals(method.getName())
                && Constant.HANDLE_METHOD_PARAMETER_COUNT == method.getParameterCount()
                && EcologyRequest.class.isAssignableFrom(parameterTypes[0])
                && EcologyResponse.class.isAssignableFrom(parameterTypes[1]);

    }

    /**
     * 初始化requestHandlerMap、handlerResponseMap
     * <p>
     * 绑定关系：
     * <p>
     * Handler:Request=1:1 and Request:Handler=1:1
     * <p>
     * Handler:(Request,Response)=1:1 and (Request,Response):Handler=1:1
     * <p>
     * Handler:Response=1:1 and Response:Handler=1:N
     * <p>
     * Request:Response:Handler=1:1:1
     * <p>
     * Request:Response=1:1 and Response:Request=1:N
     *
     * @param handler Handler对象
     * @param method  handle方法
     */
    private <H extends EcologyRequestHandler<?, ?>> void buildRequestResponseHandlerMap(H handler, Method method) {
        if (!isHandleMethod(method)) {
            return;
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        Class<EcologyRequest> requestClass = (Class<EcologyRequest>) parameterTypes[0];
        Class<EcologyResponse> responseClass = (Class<EcologyResponse>) parameterTypes[1];
        // Handler:Request=1:1 and Request:Handler=1:1
        requestHandlerMap.compute(requestClass, (k, v) -> {
            if (null != v) {
                String format = "The request can not repeat bind handler,request = %s,handler = %s";
                String tip = String.format(format, k.getSimpleName(), v.getClass().getSimpleName());
                log.error(tip);
                throw new RequestRepeatBindException(tip);
            }
            return handler;
        });
        // Request:Response=1:1 and Response:Request=1:1
        requestResponseMap.compute(requestClass, (k, v) -> {
            if (null != v) {
                String format = "The request can not repeat bind response,request = %s,response = %s";
                String tip = String.format(format, k.getSimpleName(), v.getSimpleName());
                log.error(tip);
                throw new RequestRepeatBindException(tip);
            }
            return responseClass;
        });
    }

    /**
     * 获取 RequestHandlerMap
     *
     * @return {@link ConcurrentMap}
     */
    public ConcurrentMap<Class<EcologyRequest>, EcologyRequestHandler> getRequestHandlerMap() {
        return requestHandlerMap;
    }

    /**
     * 获取 RequestResponseMap
     *
     * @return {@link ConcurrentMap}
     */
    public ConcurrentMap<Class<EcologyRequest>, Class<EcologyResponse>> getRequestResponseMap() {
        return requestResponseMap;
    }
}
