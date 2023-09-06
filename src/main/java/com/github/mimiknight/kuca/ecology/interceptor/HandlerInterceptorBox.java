package com.github.mimiknight.kuca.ecology.interceptor;

import com.github.mimiknight.kuca.ecology.model.request.EcologyRequest;
import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
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

    }

    private ApplicationContext appContext;

    @Autowired
    public void setAppContext(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    /**
     * Handler前置拦截器Map
     */
    private final ConcurrentHashMap<Class<EcologyRequest>, TreeSet<HandlerBeforeInterceptor<EcologyRequest>>> handlerBeforeInterceptorMap;

    /**
     * Handler后置拦截器Map
     */
    private final ConcurrentHashMap<Class<EcologyRequest>, TreeSet<HandlerAfterReturnInterceptor<EcologyRequest, EcologyResponse>>> handlerAfterReturnInterceptorMap;


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
     * 初始化前置拦截器Map
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void initBeforeHandlerMap() throws NoSuchMethodException {
        Map<String, HandlerBeforeInterceptor> map = appContext.getBeansOfType(HandlerBeforeInterceptor.class);
        if (MapUtils.isEmpty(map)) {
            return;
        }
        for (HandlerBeforeInterceptor interceptor : map.values()) {
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
        Map<String, HandlerAfterReturnInterceptor> map = appContext.getBeansOfType(HandlerAfterReturnInterceptor.class);
        if (MapUtils.isEmpty(map)) {
            return;
        }
        for (HandlerAfterReturnInterceptor interceptor : map.values()) {
            Method method = interceptor.getClass().getMethod(Constant.INTERCEPT_METHOD_NAME, EcologyRequest.class, EcologyResponse.class);
            Class<?> requestClass = method.getParameterTypes()[0];
            this.handlerAfterReturnInterceptorMap.computeIfAbsent((Class<EcologyRequest>) requestClass, k -> new TreeSet<>()).add(interceptor);
        } // end for
    }

    /**
     * 执行前置拦截器
     *
     * @param request 请求参数
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
