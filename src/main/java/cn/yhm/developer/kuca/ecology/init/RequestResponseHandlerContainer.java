package cn.yhm.developer.kuca.ecology.init;

import cn.yhm.developer.kuca.ecology.core.EcologyHandleable;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author victor2015yhm@gmail.com
 * @since 2023-03-15 19:16:53
 */
@Component
public class RequestResponseHandlerContainer {

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
    private final ConcurrentHashMap<Class<?>, EcologyHandleable<?, ?>> requestHandlerMap = new ConcurrentHashMap<>(Constant.INIT_CAPACITY);

    /**
     * handler与响应对象对应Map
     */
    private final ConcurrentHashMap<EcologyHandleable<?, ?>, Class<?>> handlerResponseMap = new ConcurrentHashMap<>(Constant.INIT_CAPACITY);

    private ApplicationContext appContext;

    @Autowired
    public void setAppContext(ApplicationContext appContext) {
        this.appContext = appContext;
    }

    /**
     * 初始化方法
     */
    public void init() {
        initRequestResponseHandlerMap();
    }

    /**
     * 当前方法是否为Handler的默认handle方法
     *
     * @param method 方法
     * @return boolean
     */
    private boolean isHandleMethod(Method method) {
        // public修改的方法、方法名为handle、方法参数个数为2、方法为非人工合成的
        return Modifier.isPublic(method.getModifiers())
                && !method.isSynthetic()
                && Constant.HANDLE_METHOD_NAME.equalsIgnoreCase(method.getName())
                && Constant.HANDLE_METHOD_PARAMETER_COUNT == method.getParameterCount();
    }

    /**
     * 初始化Map
     */
    private void initRequestResponseHandlerMap() {
        Map<String, EcologyHandleable> handlerMap = appContext.getBeansOfType(EcologyHandleable.class);
        for (EcologyHandleable<?, ?> handler : handlerMap.values()) {
            for (Method method : AopUtils.getTargetClass(handler).getMethods()) {
                boolean isHandleMethod = isHandleMethod(method);
                buildRequestResponseHandlerMap(isHandleMethod, handler, method);
            }
        }
    }

    /**
     * 初始化requestHandlerMap、handlerResponseMap
     *
     * @param isHandleMethod 是否为handle方法
     * @param handler        Handler对象
     * @param method         handle方法
     */
    private void buildRequestResponseHandlerMap(boolean isHandleMethod, EcologyHandleable<?, ?> handler, Method method) {
        if (!isHandleMethod) {
            return;
        }
        Class<?>[] parameterTypes = method.getParameterTypes();
        requestHandlerMap.put(parameterTypes[0], handler);
        handlerResponseMap.put(handler, parameterTypes[1]);
    }

    public ConcurrentHashMap<Class<?>, EcologyHandleable<?, ?>> getRequestHandlerMap() {
        return requestHandlerMap;
    }

    public ConcurrentHashMap<EcologyHandleable<?, ?>, Class<?>> getHandlerResponseMap() {
        return handlerResponseMap;
    }
}
