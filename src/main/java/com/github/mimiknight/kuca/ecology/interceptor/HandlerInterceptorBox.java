package com.github.mimiknight.kuca.ecology.interceptor;

import com.github.mimiknight.kuca.ecology.handler.EcologyRequestHandler;
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
        String DO_AROUND_METHOD_NAME = "doAround";

        /**
         * 方法参数个数
         */
        int DO_AROUND_METHOD_PARAMETER_COUNT = 3;

    }

    private ApplicationContext appContext;

    @Autowired
    public void setAppContext(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    /**
     * Handler前置拦截器Map
     */
    private final ConcurrentHashMap<Class<EcologyRequestHandler<?, ?>>, TreeSet<EcologyHandlerInterceptor<?, ?, ?, ?>>> handlerInterceptorMap;

    /**
     * 空参构造
     */
    public HandlerInterceptorBox() {
        this.handlerInterceptorMap = new ConcurrentHashMap<>(Constant.INIT_CAPACITY);
    }

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        initInterceptMap();
    }

    /**
     * 初始化
     */
    @SuppressWarnings({"rawtypes"})
    public void initInterceptMap() {
        Map<String, EcologyHandlerInterceptor> map = appContext.getBeansOfType(EcologyHandlerInterceptor.class);
        if (MapUtils.isEmpty(map)) {
            return;
        }
        for (EcologyHandlerInterceptor interceptor : map.values()) {
            Method[] methods = interceptor.getClass().getMethods();
            for (Method method : methods) {
                buildInterceptorMap(method, interceptor);
            }
        } // end for
    }

    /**
     * @param method      方法
     * @param interceptor 拦截器
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void buildInterceptorMap(Method method, EcologyHandlerInterceptor interceptor) {
        if (!isDoAroundMethod(method)) {
            return;
        }
        Class<?> parameterType = method.getParameterTypes()[2];
        Class<EcologyRequestHandler<?, ?>> handlerClass = (Class<EcologyRequestHandler<?, ?>>) parameterType;
        // key如果不存在则新建TreeSet插入值，key如果已经存在则添加到TreeSet中
        this.handlerInterceptorMap.compute(handlerClass, (k, v) -> {
            if (CollectionUtils.isEmpty(v)) {
                v = new TreeSet<>();
            }
            v.add(interceptor);
            return v;
        });
    }

    /**
     * 当前方法是否为doAround()方法
     * <p>
     * public修改的方法；
     * 方法为非人工合成的；
     * 方法名为doAround；
     * 方法参数个数为3；
     * 方法的第1个参数实现了EcologyRequest接口；
     * 方法的第2个参数实现了EcologyResponse接口；
     * 方法的第3个参数实现了EcologyRequestHandler接口；
     *
     * @param method 方法
     * @return boolean
     */
    private boolean isDoAroundMethod(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return Modifier.isPublic(method.getModifiers())
                && !method.isSynthetic()
                && Constant.DO_AROUND_METHOD_NAME.equals(method.getName())
                && Constant.DO_AROUND_METHOD_PARAMETER_COUNT == method.getParameterCount()
                && EcologyRequest.class.isAssignableFrom(parameterTypes[0])
                && EcologyResponse.class.isAssignableFrom(parameterTypes[1])
                && EcologyRequestHandler.class.isAssignableFrom(parameterTypes[2]);

    }

    /**
     * 获取拦截器Map
     */
    public ConcurrentHashMap<Class<EcologyRequestHandler<?, ?>>, TreeSet<EcologyHandlerInterceptor<?, ?, ?, ?>>> getHandlerInterceptorMap() {
        return handlerInterceptorMap;
    }
}
