package com.github.mimiknight.kuca.ecology.filter;

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
 * 装载Handler过滤器的容器
 *
 * @author MiMiKnight victor2015yhm@gmail.com
 * @since 2023-09-09 00:27:00
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class HandlerFilterBox {

    private interface Constant {

        int INIT_CAPACITY = 16;

        String DO_FILTER_METHOD_NAME = "doFilter";

        int DO_FILTER_METHOD_PARAMETER_COUNT = 3;

    }

    @Autowired
    private ApplicationContext appContext;

    /**
     * Handler 过滤器Map
     */
    private final ConcurrentMap<Class<EcologyRequest>, List<EcologyHandlerFilter>> handlerFilterMap;

    /**
     * 空参构造
     */
    public HandlerFilterBox() {
        this.handlerFilterMap = new ConcurrentHashMap<>(Constant.INIT_CAPACITY);
    }

    @PostConstruct
    public void init() {
        initFilterMap();
//        initFilterChainMap();
    }


    /**
     * 初始化过滤器Map
     */
    public void initFilterMap() {
        Map<String, EcologyHandlerFilter> map = appContext.getBeansOfType(EcologyHandlerFilter.class);
        if (MapUtils.isEmpty(map)) {
            return;
        }
        buildFilterMap(map.values());
    }


    /**
     * 构建过滤器Map
     *
     * @param filters 过滤器集合
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private void buildFilterMap(Collection<EcologyHandlerFilter> filters) {
        Assert.notEmpty(filters, "The interceptors argument is required; it must not be empty");

        HashMap<Class<EcologyRequest>, TreeSet<EcologyHandlerFilter>> map = new HashMap<>();

        for (EcologyHandlerFilter filter : filters) { // outer
            for (Method method : filter.getClass().getMethods()) { // inner
                if (isDoFilterMethod(method)) {
                    Class<EcologyRequest> requestClass = (Class<EcologyRequest>) method.getParameterTypes()[0];
                    sortIt(map, requestClass, filter);
                    break;
                }
            } // end inner
        } // end outer

        for (Map.Entry<Class<EcologyRequest>, TreeSet<EcologyHandlerFilter>> entry : map.entrySet()) { // outer
            Class<EcologyRequest> key = entry.getKey();
            for (EcologyHandlerFilter interceptor : entry.getValue()) { // inner
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
    private void putIt(Class<EcologyRequest> key, EcologyHandlerFilter value) {
        this.handlerFilterMap.compute(key, (k, v) -> {
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
    private static void sortIt(HashMap<Class<EcologyRequest>, TreeSet<EcologyHandlerFilter>> map,
                               Class<EcologyRequest> key,
                               EcologyHandlerFilter value) {
        // key如果不存在则新建TreeSet插入值，key如果已经存在则添加到TreeSet中
        map.compute(key, (k, v) -> {
            if (CollectionUtils.isEmpty(v)) {
                v = new TreeSet<>(new HandlerFilterComparator<>());
            }
            v.add(value);
            return v;
        });
    }

    /**
     * 当前方法是否为doFilter方法
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
    private boolean isDoFilterMethod(Method method) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        return Modifier.isPublic(method.getModifiers())
                && !method.isSynthetic()
                && Constant.DO_FILTER_METHOD_NAME.equals(method.getName())
                && Constant.DO_FILTER_METHOD_PARAMETER_COUNT == method.getParameterCount()
                && EcologyRequest.class.isAssignableFrom(parameterTypes[0])
                && EcologyResponse.class.isAssignableFrom(parameterTypes[1])
                && HandlerFilterChain.class.isAssignableFrom(parameterTypes[2]);

    }


    /**
     * 获取过滤器Map
     *
     * @return {@link ConcurrentMap}
     */
    public ConcurrentMap<Class<EcologyRequest>, List<EcologyHandlerFilter>> getHandlerFilterMap() {
        return this.handlerFilterMap;
    }

    /**
     * 自定义过滤器比较器
     */
    private static class HandlerFilterComparator<Q extends EcologyRequest,
            P extends EcologyResponse,
            F extends EcologyHandlerFilter<Q, P>> implements Comparator<F> {

        @Override
        public int compare(F o1, F o2) {
            return Integer.compare(o1.getOrder(), o2.getOrder());
        }
    }
}
