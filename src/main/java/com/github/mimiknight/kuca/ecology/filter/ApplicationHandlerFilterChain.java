package com.github.mimiknight.kuca.ecology.filter;

import com.github.mimiknight.kuca.ecology.handler.EcologyRequestHandler;
import com.github.mimiknight.kuca.ecology.model.request.EcologyRequest;
import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler过滤器实现类
 *
 * @author MiMiKnight victor2015yhm@gmail.com
 * @since 2023-09-09 00:09:41
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class ApplicationHandlerFilterChain implements HandlerFilterChain {

    private final List<EcologyHandlerFilter> filters;

    private int position;

    private EcologyRequestHandler handler;

    public ApplicationHandlerFilterChain() {
        this.filters = new ArrayList<>();
        this.position = 0;
    }

    @Override
    public <Q extends EcologyRequest,
            P extends EcologyResponse> void doFilter(Q request, P response) throws Exception {
        // 递归执行过滤器
        if (position < filters.size()) {
            EcologyHandlerFilter filter = filters.get(position++);
            filter.init();
            filter.doFilter(request, response, this);
            filter.destroy();
            return;
        }
        // 执行业务逻辑
        this.handler.handle(request, response);
    }

    public <Q extends EcologyRequest,
            P extends EcologyResponse,
            H extends EcologyRequestHandler<Q, P>,
            F extends EcologyHandlerFilter<Q, P, H>> void addFilter(F filter) {
        this.filters.add(filter);
    }

    public <Q extends EcologyRequest,
            P extends EcologyResponse,
            H extends EcologyRequestHandler<Q, P>,
            F extends EcologyHandlerFilter<Q, P, H>> void addFilter(List<F> filters) {
        this.filters.addAll(filters);
    }

    public <Q extends EcologyRequest,
            P extends EcologyResponse,
            H extends EcologyRequestHandler<Q, P>> void setTarget(H target) {
        this.handler = target;
    }

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
    public <Q extends EcologyRequest,
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
