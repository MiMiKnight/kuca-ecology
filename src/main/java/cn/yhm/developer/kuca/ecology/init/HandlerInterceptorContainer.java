package cn.yhm.developer.kuca.ecology.init;

import cn.yhm.developer.kuca.ecology.interceptor.HandlerAfterReturnInterceptor;
import cn.yhm.developer.kuca.ecology.interceptor.HandlerBeforeInterceptor;
import cn.yhm.developer.kuca.ecology.model.request.EcologyRequest;
import cn.yhm.developer.kuca.ecology.model.response.EcologyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

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

    private interface Constant {

        /**
         * 初始化容量
         */
        int INIT_CAPACITY = 128;

        /**
         * interceptor方法名
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
            new ConcurrentHashMap<>(Constant.INIT_CAPACITY);

    /**
     * Handler后置拦截器Map
     */
    private final ConcurrentHashMap<Class<?>, TreeSet<A>> handlerAfterReturnInterceptor =
            new ConcurrentHashMap<>(Constant.INIT_CAPACITY);


    private ApplicationContext appContext;

    @Autowired
    public void setAppContext(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    /**
     * 初始化方法
     */
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
     * 是否为intercept方法
     *
     * @param method               方法对象
     * @param expectParameterCount 期望的方法参数个数
     * @param parameterCount       实际的方法参数格式
     * @return boolean
     */
    private boolean isInterceptMethod(Method method, int expectParameterCount, int parameterCount) {
        return Modifier.isPublic(method.getModifiers())
                && !method.isSynthetic()
                && Constant.INTERCEPT_METHOD_NAME.equalsIgnoreCase(method.getName())
                && expectParameterCount == method.getParameterCount();
    }

    /**
     * 初始化前置拦截器Map
     */
    private void initBeforeHandlerMap() {
        Map<String, HandlerBeforeInterceptor> beforeInterceptorMap =
                appContext.getBeansOfType(HandlerBeforeInterceptor.class);
        for (HandlerBeforeInterceptor<R> interceptor : beforeInterceptorMap.values()) {
            Method[] methods = interceptor.getClass().getMethods();
            for (Method method : methods) {
                if (isInterceptMethod(method, Constant.BEFORE_INTERCEPTOR_METHOD_PARAMETER_COUNT, method.getParameterCount())) {
                    Class<?> parameterType = method.getParameterTypes()[0];
                    this.handlerBeforeInterceptorMap.computeIfAbsent(parameterType, k -> {
                        return new TreeSet<>();
                    }).add((B) interceptor);
                }
            }
        } // end for
    }

    /**
     * 初始化后置拦截器Map
     */
    private void initAfterReturnHandlerMap() {
        Map<String, HandlerAfterReturnInterceptor> afterReturnInterceptorMap =
                appContext.getBeansOfType(HandlerAfterReturnInterceptor.class);
        for (HandlerAfterReturnInterceptor<R, T> interceptor : afterReturnInterceptorMap.values()) {
            Method[] methods = interceptor.getClass().getMethods();
            for (Method method : methods) {
                if (isInterceptMethod(method, Constant.AFTER_INTERCEPTOR_METHOD_PARAMETER_COUNT, method.getParameterCount())) {
                    Class<?> parameterType = method.getParameterTypes()[0];
                    this.handlerAfterReturnInterceptor.computeIfAbsent(parameterType, k -> {
                        return new TreeSet<>();
                    }).add((A) interceptor);
                }
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
        if (interceptorMap.size() < 1) {
            return;
        }
        for (B b : interceptorMap.get(request.getClass())) {
            b.intercept(request);
        }
    }

    /**
     * 执行后置拦截器
     *
     * @param request  请求参数
     * @param response 响应参数
     */
    public void doAfterReturnInterceptor(R request, T response) {
        ConcurrentHashMap<Class<?>, TreeSet<A>> interceptorMap = this.handlerAfterReturnInterceptor;
        if (interceptorMap.size() < 1) {
            return;
        }
        for (A a : interceptorMap.get(request.getClass())) {
            a.intercept(request, response);
        }
    }


}
