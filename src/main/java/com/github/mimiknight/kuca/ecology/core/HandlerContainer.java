package com.github.mimiknight.kuca.ecology.core;

import com.github.mimiknight.kuca.ecology.model.request.EcologyRequest;
import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Handler容器
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-03-15 19:16:53
 */
@Component
public class HandlerContainer {

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

    /**
     * 请求对象与handler对应Map
     */
    private final ConcurrentMap<Class<?>, EcologyRequestHandler<?, ?>> requestHandlerMap = new ConcurrentHashMap<>(Constant.INIT_CAPACITY);

    /**
     * handler与响应对象对应Map
     */
    private final ConcurrentMap<EcologyRequestHandler<?, ?>, Class<?>> handlerResponseMap = new ConcurrentHashMap<>(Constant.INIT_CAPACITY);

    private ApplicationContext appContext;

    @Autowired
    public void setAppContext(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    /**
     * 初始化方法
     */
    @PostConstruct
    public void init() {
        initRequestResponseHandlerMap();
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
                && Constant.HANDLE_METHOD_NAME.equalsIgnoreCase(method.getName())
                && Constant.HANDLE_METHOD_PARAMETER_COUNT == method.getParameterCount()
                && EcologyRequest.class.isAssignableFrom(parameterTypes[0])
                && EcologyResponse.class.isAssignableFrom(parameterTypes[1]);

    }

    /**
     * 初始化Map
     */
    @SuppressWarnings("rawtypes")
    private void initRequestResponseHandlerMap() {
        Map<String, EcologyRequestHandler> handlerMap = appContext.getBeansOfType(EcologyRequestHandler.class);
        for (EcologyRequestHandler<?, ?> handler : handlerMap.values()) {
            for (Method method : AopUtils.getTargetClass(handler).getMethods()) {
                if (isHandleMethod(method)) {
                    buildRequestResponseHandlerMap(handler, method);
                }
            }
        }
    }

    /**
     * 初始化requestHandlerMap、handlerResponseMap
     *
     * @param handler Handler对象
     * @param method  handle方法
     */
    private void buildRequestResponseHandlerMap(EcologyRequestHandler<?, ?> handler, Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        requestHandlerMap.put(parameterTypes[0], handler);
        handlerResponseMap.put(handler, parameterTypes[1]);
    }

    /**
     * 获取RequestHandlerMap
     *
     * @return {@link ConcurrentMap}
     */
    public ConcurrentMap<Class<?>, EcologyRequestHandler<?, ?>> getRequestHandlerMap() {
        return requestHandlerMap;
    }

    /**
     * 获取HandlerResponseMap
     *
     * @return {@link ConcurrentMap}
     */
    public ConcurrentMap<EcologyRequestHandler<?, ?>, Class<?>> getHandlerResponseMap() {
        return handlerResponseMap;
    }
}
