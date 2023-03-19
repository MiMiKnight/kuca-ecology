package cn.yhm.developer.kuca.ecology.interceptor;

import org.springframework.core.Ordered;

/**
 * Handler拦截器接口
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-03-15 18:12:09
 */
public interface EcologyHandlerInterceptor extends Ordered, Comparable<EcologyHandlerInterceptor> {

    /**
     * 排序比较
     *
     * @param o 被比较对象
     * @return int
     */
    @Override
    default int compareTo(EcologyHandlerInterceptor o) {
        return Integer.compare(this.getOrder(), o.getOrder());
    }
}
