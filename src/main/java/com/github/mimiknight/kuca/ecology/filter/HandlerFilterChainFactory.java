package com.github.mimiknight.kuca.ecology.filter;

/**
 * Handler过滤器链工厂类
 *
 * @author MiMiKnight victor2015yhm@gmail.com
 * @since 2023-09-09 00:08:51
 */
public class HandlerFilterChainFactory {

    private HandlerFilterChainFactory() {
    }

    /**
     * 获取过滤器链
     *
     * @return {@link ApplicationHandlerFilterChain}
     */
    public static ApplicationHandlerFilterChain getFilterChain() {
        return new ApplicationHandlerFilterChain();
    }
}
