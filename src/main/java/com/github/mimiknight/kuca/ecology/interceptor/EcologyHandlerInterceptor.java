package com.github.mimiknight.kuca.ecology.interceptor;

import org.springframework.core.Ordered;

/**
 * 请求Handler拦截器接口
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-03-15 18:12:09
 */
public interface EcologyHandlerInterceptor extends Ordered, Comparable<EcologyHandlerInterceptor> {

    /**
     * 排序比较
     *
     * @param interceptor 被比较地拦截器对象
     * @return int
     */
    @Override
    default int compareTo(EcologyHandlerInterceptor interceptor) {
        return Integer.compare(this.getOrder(), interceptor.getOrder());
    }
}
