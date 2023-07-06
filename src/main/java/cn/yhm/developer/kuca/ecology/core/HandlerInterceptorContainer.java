package cn.yhm.developer.kuca.ecology.core;

import cn.yhm.developer.kuca.ecology.interceptor.HandlerAfterReturnInterceptor;
import cn.yhm.developer.kuca.ecology.interceptor.HandlerBeforeInterceptor;
import cn.yhm.developer.kuca.ecology.model.request.EcologyRequest;
import cn.yhm.developer.kuca.ecology.model.response.EcologyResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 初始化
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-03-15 18:33:30
 */
@Component
public class HandlerInterceptorContainer<R extends EcologyRequest, T extends EcologyResponse,
        B extends HandlerBeforeInterceptor<R>, A extends HandlerAfterReturnInterceptor<R, T>> {

    private interface LocalConstant {

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
    private final ConcurrentHashMap<Class<?>, TreeSet<B>> handlerBeforeInterceptorMap =
            new ConcurrentHashMap<>(LocalConstant.INIT_CAPACITY);

    /**
     * Handler后置拦截器Map
     */
    private final ConcurrentHashMap<Class<?>, TreeSet<A>> handlerAfterReturnInterceptorMap =
            new ConcurrentHashMap<>(LocalConstant.INIT_CAPACITY);


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
        initInterceptMap();
    }

    /**
     * 初始化方法
     */
    public void initInterceptMap() {
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
                && LocalConstant.INTERCEPT_METHOD_NAME.equalsIgnoreCase(method.getName())
                && LocalConstant.BEFORE_INTERCEPTOR_METHOD_PARAMETER_COUNT == method.getParameterCount()
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
                && LocalConstant.INTERCEPT_METHOD_NAME.equalsIgnoreCase(method.getName())
                && LocalConstant.AFTER_INTERCEPTOR_METHOD_PARAMETER_COUNT == method.getParameterCount()
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
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void initBeforeHandlerMap() {
        Map<String, HandlerBeforeInterceptor> beforeInterceptorMap =
                appContext.getBeansOfType(HandlerBeforeInterceptor.class);
        for (HandlerBeforeInterceptor interceptor : beforeInterceptorMap.values()) {
            Method[] methods = interceptor.getClass().getMethods();
            Method method = hasBeforeInterceptMethod(methods);
            if (null != method) {
                Class<?> parameterType = method.getParameterTypes()[0];
                this.handlerBeforeInterceptorMap.computeIfAbsent(parameterType, k -> new TreeSet<>()).add((B) interceptor);
            }
        } // end for
    }

    /**
     * 初始化后置拦截器Map
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private void initAfterReturnHandlerMap() {
        Map<String, HandlerAfterReturnInterceptor> afterReturnInterceptorMap =
                appContext.getBeansOfType(HandlerAfterReturnInterceptor.class);
        for (HandlerAfterReturnInterceptor interceptor : afterReturnInterceptorMap.values()) {
            Method[] methods = interceptor.getClass().getMethods();
            Method method = hasAfterInterceptMethod(methods);
            if (null != method) {
                Class<?> parameterType = method.getParameterTypes()[0];
                this.handlerAfterReturnInterceptorMap.computeIfAbsent(parameterType, k -> new TreeSet<>()).add((A) interceptor);
            }
        } // end for
    }

    /**
     * 执行前置拦截器
     *
     * @param request 请求参数
     */
    public void doBeforeInterceptor(R request) {
        ConcurrentHashMap<Class<?>, TreeSet<B>> interceptorMap = this.handlerBeforeInterceptorMap;
        if (MapUtils.isEmpty(interceptorMap) || CollectionUtils.isEmpty(interceptorMap.get(request.getClass()))) {
            return;
        }
        TreeSet<B> interceptors = interceptorMap.get(request.getClass());
        for (B interceptor : interceptors) {
            interceptor.intercept(request);
        }
    }

    /**
     * 执行后置拦截器
     *
     * @param request  请求参数
     * @param response 响应参数
     */
    public void doAfterReturnInterceptor(R request, T response) {
        ConcurrentHashMap<Class<?>, TreeSet<A>> interceptorMap = this.handlerAfterReturnInterceptorMap;
        if (MapUtils.isEmpty(interceptorMap) || CollectionUtils.isEmpty(interceptorMap.get(request.getClass()))) {
            return;
        }
        TreeSet<A> interceptors = interceptorMap.get(request.getClass());
        for (A interceptor : interceptors) {
            interceptor.intercept(request, response);
        }
    }


}
