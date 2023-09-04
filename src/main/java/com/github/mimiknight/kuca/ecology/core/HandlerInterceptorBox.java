package com.github.mimiknight.kuca.ecology.core;

import com.github.mimiknight.kuca.ecology.interceptor.HandlerAfterReturnInterceptor;
import com.github.mimiknight.kuca.ecology.interceptor.HandlerBeforeInterceptor;
import com.github.mimiknight.kuca.ecology.model.request.EcologyRequest;
import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 装载Handler拦截器的容器
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-03-15 18:33:30
 */
public class HandlerInterceptorBox {

    private interface Constant {

        /**
         * 初始化容量
         */
        int INIT_CAPACITY = 128;

        /**
         * 拦截器方法名
         */
        String INTERCEPT_METHOD_NAME = "intercept";

        /**
         * 前置拦截器拦截方法参数个数
         */
        int BEFORE_INTERCEPTOR_METHOD_PARAMETER_COUNT = 1;

        /**
         * 后置拦截器拦截方法参数个数
         */
        int AFTER_INTERCEPTOR_METHOD_PARAMETER_COUNT = 2;

    }

    /**
     * Handler前置拦截器Map
     */
    private final ConcurrentHashMap<Class<EcologyRequest>, TreeSet<HandlerBeforeInterceptor<EcologyRequest>>> handlerBeforeInterceptorMap;

    /**
     * Handler后置拦截器Map
     */
    private final ConcurrentHashMap<Class<EcologyRequest>, TreeSet<HandlerAfterReturnInterceptor<EcologyRequest, EcologyResponse>>> handlerAfterReturnInterceptorMap;


    @Autowired
    private ApplicationContext appContext;

    public HandlerInterceptorBox() {
        this.handlerBeforeInterceptorMap = new ConcurrentHashMap<>(Constant.INIT_CAPACITY);
        this.handlerAfterReturnInterceptorMap = new ConcurrentHashMap<>(Constant.INIT_CAPACITY);
    }

    /**
     * 初始化
     *
     * @throws NoSuchMethodException 没有这样方法异常
     */
    @PostConstruct
    public void init() throws NoSuchMethodException {
        initInterceptMap();
    }

    /**
     * 初始化
     *
     * @throws NoSuchMethodException 没有这样方法异常
     */
    public void initInterceptMap() throws NoSuchMethodException {
        initBeforeHandlerMap();
        initAfterReturnHandlerMap();
    }

    /**
     * 是否为前置拦截器的intercept方法
     *
     * @param method 方法对象
     * @return boolean
     */
    private boolean isBeforeInterceptMethod(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return Modifier.isPublic(method.getModifiers())
                && !method.isSynthetic()
                && Constant.INTERCEPT_METHOD_NAME.equals(method.getName())
                && Constant.BEFORE_INTERCEPTOR_METHOD_PARAMETER_COUNT == method.getParameterCount()
                && EcologyRequest.class.isAssignableFrom(parameterTypes[0]);
    }

    /**
     * 判断是否存在前置拦截器指定的intercept方法
     *
     * @param methods 方法数组
     * @return {@link Method}
     */
    private Method hasBeforeInterceptMethod(Method[] methods) {
        for (Method method : methods) {
            if (isBeforeInterceptMethod(method)) {
                return method;
            }
        }
        return null;
    }

    /**
     * 是否为后置拦截器的intercept方法
     *
     * @param method 方法对象
     * @return boolean
     */
    private boolean isAfterInterceptMethod(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return Modifier.isPublic(method.getModifiers())
                && !method.isSynthetic()
                && Constant.INTERCEPT_METHOD_NAME.equals(method.getName())
                && Constant.AFTER_INTERCEPTOR_METHOD_PARAMETER_COUNT == method.getParameterCount()
                && EcologyRequest.class.isAssignableFrom(parameterTypes[0])
                && EcologyResponse.class.isAssignableFrom(parameterTypes[1]);
    }

    /**
     * 判断是否存在后置拦截器指定的intercept方法
     *
     * @param methods 方法数组
     * @return {@link Method}
     */
    private Method hasAfterInterceptMethod(Method[] methods) {
        for (Method method : methods) {
            if (isAfterInterceptMethod(method)) {
                return method;
            }
        }
        return null;
    }

    /**
     * 初始化前置拦截器Map
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void initBeforeHandlerMap() throws NoSuchMethodException {
        Map<String, HandlerBeforeInterceptor> beforeInterceptorMap = appContext.getBeansOfType(HandlerBeforeInterceptor.class);
        if (MapUtils.isEmpty(beforeInterceptorMap)) {
            return;
        }
        for (HandlerBeforeInterceptor interceptor : beforeInterceptorMap.values()) {
            Method method = interceptor.getClass().getMethod(Constant.INTERCEPT_METHOD_NAME, EcologyRequest.class);
            Class<?> requestClass = method.getParameterTypes()[0];
            this.handlerBeforeInterceptorMap.computeIfAbsent((Class<EcologyRequest>) requestClass, k -> new TreeSet<>()).add(interceptor);
        } // end for
    }

    /**
     * 初始化后置拦截器Map
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void initAfterReturnHandlerMap() throws NoSuchMethodException {
        Map<String, HandlerAfterReturnInterceptor> afterReturnInterceptorMap = appContext.getBeansOfType(HandlerAfterReturnInterceptor.class);
        if (MapUtils.isEmpty(afterReturnInterceptorMap)) {
            return;
        }
        for (HandlerAfterReturnInterceptor interceptor : afterReturnInterceptorMap.values()) {
            Method method = interceptor.getClass().getMethod(Constant.INTERCEPT_METHOD_NAME, EcologyRequest.class, EcologyResponse.class);
            Class<?> requestClass = method.getParameterTypes()[0];
            this.handlerAfterReturnInterceptorMap.computeIfAbsent((Class<EcologyRequest>) requestClass, k -> new TreeSet<>()).add(interceptor);
        } // end for
    }

    /**
     * 执行前置拦截器
     *
     * @param request 请求参数
     * @throws Exception 异常
     */
    public void doBeforeInterceptor(EcologyRequest request) throws Exception {
        ConcurrentHashMap<Class<EcologyRequest>, TreeSet<HandlerBeforeInterceptor<EcologyRequest>>> interceptorMap =
                this.handlerBeforeInterceptorMap;
        if (MapUtils.isEmpty(interceptorMap) || CollectionUtils.isEmpty(interceptorMap.get(request.getClass()))) {
            return;
        }
        TreeSet<HandlerBeforeInterceptor<EcologyRequest>> interceptors = interceptorMap.get(request.getClass());
        for (HandlerBeforeInterceptor<EcologyRequest> interceptor : interceptors) {
            interceptor.intercept(request);
        }
    }

    /**
     * 执行后置拦截器
     *
     * @param request  请求参数
     * @param response 响应参数
     * @throws Exception 异常
     */
    public void doAfterReturnInterceptor(EcologyRequest request, EcologyResponse response) throws Exception {
        ConcurrentHashMap<Class<EcologyRequest>, TreeSet<HandlerAfterReturnInterceptor<EcologyRequest, EcologyResponse>>> interceptorMap =
                this.handlerAfterReturnInterceptorMap;
        if (MapUtils.isEmpty(interceptorMap) || CollectionUtils.isEmpty(interceptorMap.get(request.getClass()))) {
            return;
        }
        TreeSet<HandlerAfterReturnInterceptor<EcologyRequest, EcologyResponse>> interceptors = interceptorMap.get(request.getClass());
        for (HandlerAfterReturnInterceptor<EcologyRequest, EcologyResponse> interceptor : interceptors) {
            interceptor.intercept(request, response);
        }
    }


}
