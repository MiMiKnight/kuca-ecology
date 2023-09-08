package com.github.mimiknight.kuca.ecology.interceptor;

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
import java.util.concurrent.ConcurrentMap;

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
        String DO_BEFORE_METHOD_NAME = "doBefore";

        /**
         * 方法参数个数
         */
        int DO_BEFORE_METHOD_PARAMETER_COUNT = 2;

    }

    private ApplicationContext appContext;

    @Autowired
    public void setAppContext(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    /**
     * Handler前置拦截器Map
     */
    private final ConcurrentMap<Class<EcologyRequest>, TreeSet<EcologyHandlerInterceptor<?, ?, ?>>> handlerInterceptorMap;

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
     * 构建拦截器容器Map
     *
     * @param method      方法
     * @param interceptor 拦截器
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void buildInterceptorMap(Method method, EcologyHandlerInterceptor interceptor) {
        if (!isDoBeforeMethod(method)) {
            return;
        }
        Class<?> parameterType = method.getParameterTypes()[0];
        Class<EcologyRequest> requestClass = (Class<EcologyRequest>) parameterType;
        // key如果不存在则新建TreeSet插入值，key如果已经存在则添加到TreeSet中
        this.handlerInterceptorMap.compute(requestClass, (k, v) -> {
            if (CollectionUtils.isEmpty(v)) {
                v = new TreeSet<>();
            }
            v.add(interceptor);
            return v;
        });
    }

    /**
     * 当前方法是否为doBefore方法
     * <p>
     * public修改的方法；
     * 方法为非人工合成的；
     * 匹配方法名；
     * 匹配方法参数个数；
     * 匹配方法参数类型；
     *
     * @param method 方法
     * @return boolean
     */
    private boolean isDoBeforeMethod(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return Modifier.isPublic(method.getModifiers())
                && !method.isSynthetic()
                && Constant.DO_BEFORE_METHOD_NAME.equals(method.getName())
                && Constant.DO_BEFORE_METHOD_PARAMETER_COUNT == method.getParameterCount()
                && EcologyRequest.class.isAssignableFrom(parameterTypes[0])
                && EcologyResponse.class.isAssignableFrom(parameterTypes[1]);

    }

    /**
     * 获取拦截器Map
     *
     * @return {@link ConcurrentHashMap}
     */
    public ConcurrentMap<Class<EcologyRequest>, TreeSet<EcologyHandlerInterceptor<?, ?, ?>>> getHandlerInterceptorMap() {
        return handlerInterceptorMap;
    }
}
