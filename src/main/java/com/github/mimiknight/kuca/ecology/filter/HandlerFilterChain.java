package com.github.mimiknight.kuca.ecology.filter;

import com.github.mimiknight.kuca.ecology.handler.EcologyRequestHandler;
import com.github.mimiknight.kuca.ecology.model.request.EcologyRequest;
import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;

/**
 * 过滤器链接口
 *
 * @author MiMiKnight victor2015yhm@gmail.com
 * @since 2023-09-09 00:13:22
 */
public interface HandlerFilterChain {

    /**
     * 过滤器方法体
     *
     * @param <Q>      接口入参泛型
     * @param <P>      接口出参泛型
     * @param request  接口入参
     * @param response 接口出参
     * @throws Exception 被抛出的异常
     */
    <Q extends EcologyRequest,
            P extends EcologyResponse> void doFilter(Q request, P response) throws Exception;

    /**
     * 添加过滤器
     *
     * @param <Q>    接口入参泛型
     * @param <P>    接口出参泛型
     * @param <H>    处理器泛型
     * @param <F>    过滤器泛型
     * @param filter 过滤器对象
     */
    <Q extends EcologyRequest,
            P extends EcologyResponse,
            H extends EcologyRequestHandler<Q, P>,
            F extends EcologyHandlerFilter<Q, P, H>> void addFilter(F filter);

    /**
     * 添加过滤器
     *
     * @param <Q>     接口入参泛型
     * @param <P>     接口出参泛型
     * @param <H>     处理器泛型
     * @param <F>     过滤器泛型
     * @param filters 过滤器对象集合
     */
    <Q extends EcologyRequest,
            P extends EcologyResponse,
            H extends EcologyRequestHandler<Q, P>,
            F extends EcologyHandlerFilter<Q, P, H>> void addFilter(List<F> filters);

    /**
     * 设置过滤目标对象
     *
     * @param <Q>    接口入参泛型
     * @param <P>    接口出参泛型
     * @param <H>    业务处理器泛型
     * @param target 被过滤拦截的目标对象
     */
    <Q extends EcologyRequest,
            P extends EcologyResponse,
            H extends EcologyRequestHandler<Q, P>> void setTarget(H target);

    /**
     * 初始化过滤器链
     *
     * @param <Q>     接口入参泛型
     * @param <P>     接口出参泛型
     * @param <H>     业务处理器泛型
     * @param <F>     过滤器泛型
     * @param handler 接口业务处理器
     * @param filters 接口过滤器接口
     */
    default <Q extends EcologyRequest,
            P extends EcologyResponse,
            H extends EcologyRequestHandler<Q, P>,
            F extends EcologyHandlerFilter<Q, P, H>> void init(H handler, List<F> filters) {
        // 设置目标对象
        this.setTarget(handler);
        // 设置过滤器
        if (CollectionUtils.isNotEmpty(filters)) {
            this.addFilter(filters);
        }
    }

}
