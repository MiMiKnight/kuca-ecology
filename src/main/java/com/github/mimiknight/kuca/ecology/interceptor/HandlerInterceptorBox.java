package com.github.mimiknight.kuca.ecology.interceptor;

import com.github.mimiknight.kuca.ecology.model.request.EcologyRequest;
import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
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

    @Autowired
    private ApplicationContext appContext;

    /**
     * Handler拦截器Map
     */
    private final ConcurrentMap<Class<EcologyRequest>, List<EcologyHandlerInterceptor<?, ?>>> handlerInterceptorMap;

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
        buildInterceptorMap(map.values());
    }

    /**
     * 构建拦截器容器Map
     *
     * @param interceptors 拦截器集合
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void buildInterceptorMap(Collection<EcologyHandlerInterceptor> interceptors) {
        Assert.notEmpty(interceptors, "The interceptors argument is required; it must not be empty");

        HashMap<Class<EcologyRequest>, TreeSet<EcologyHandlerInterceptor>> map = new HashMap<>();

        for (EcologyHandlerInterceptor interceptor : interceptors) { // outer
            for (Method method : interceptor.getClass().getMethods()) { // inner
                if (isDoBeforeMethod(method)) {
                    Class<EcologyRequest> requestClass = (Class<EcologyRequest>) method.getParameterTypes()[0];
                    sortIt(map, requestClass, interceptor);
                    break;
                }
            } // end inner
        } // end outer

        for (Map.Entry<Class<EcologyRequest>, TreeSet<EcologyHandlerInterceptor>> entry : map.entrySet()) { // outer
            Class<EcologyRequest> key = entry.getKey();
            for (EcologyHandlerInterceptor interceptor : entry.getValue()) { // inner
                putIt(key, interceptor);
            } // end inner
        } // end outer

        map.clear();
    }

    /**
     * @param key   Map键
     * @param value Map值
     */
    @SuppressWarnings({"rawtypes"})
    private void putIt(Class<EcologyRequest> key, EcologyHandlerInterceptor value) {
        this.handlerInterceptorMap.compute(key, (k, v) -> {
            if (CollectionUtils.isEmpty(v)) {
                v = new ArrayList<>();
            }
            v.add(value);
            return v;
        });
    }

    /**
     * @param map   Map容器
     * @param key   Map键
     * @param value Map值
     */
    @SuppressWarnings({"rawtypes"})
    private static void sortIt(HashMap<Class<EcologyRequest>, TreeSet<EcologyHandlerInterceptor>> map,
                               Class<EcologyRequest> key,
                               EcologyHandlerInterceptor value) {
        // key如果不存在则新建TreeSet插入值，key如果已经存在则添加到TreeSet中
        map.compute(key, (k, v) -> {
            if (CollectionUtils.isEmpty(v)) {
                v = new TreeSet<>(new HandlerInteceptorComparator<>());
            }
            v.add(value);
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
    public ConcurrentMap<Class<EcologyRequest>, List<EcologyHandlerInterceptor<?, ?>>> getHandlerInterceptorMap() {
        return handlerInterceptorMap;
    }

    /**
     * 自定义拦截器比较器
     */
    private static class HandlerInteceptorComparator<Q extends EcologyRequest,
            P extends EcologyResponse,
            I extends EcologyHandlerInterceptor<Q, P>> implements Comparator<I> {

        @Override
        public int compare(I o1, I o2) {
            return Integer.compare(o1.getOrder(), o2.getOrder());
        }
    }
}
