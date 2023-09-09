package com.github.mimiknight.kuca.ecology.filter;

import com.github.mimiknight.kuca.ecology.handler.EcologyRequestHandler;
import com.github.mimiknight.kuca.ecology.model.request.EcologyRequest;
import com.github.mimiknight.kuca.ecology.model.response.EcologyResponse;

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

    private EcologyRequestHandler handler;

    private int position;

    public ApplicationHandlerFilterChain() {
        this.filters = new ArrayList<>();
    }

    @Override
    public <Q extends EcologyRequest,
            P extends EcologyResponse> void doFilter(Q request, P response) throws Exception {
        if (position < filters.size()) {
            EcologyHandlerFilter filter = filters.get(position++);
            filter.doFilter(request, response, this);
            return;
        }
        // 执行业务逻辑
        this.handler.handle(request, response);
    }

    @Override
    public <Q extends EcologyRequest,
            P extends EcologyResponse,
            F extends EcologyHandlerFilter<Q, P>> void addFilter(F filter) {
        this.filters.add(filter);
    }


    @Override
    public <Q extends EcologyRequest,
            P extends EcologyResponse,
            F extends EcologyHandlerFilter<Q, P>> void addFilter(List<F> filters) {
        this.filters.addAll(filters);
    }

    @Override
    public <Q extends EcologyRequest,
            P extends EcologyResponse,
            H extends EcologyRequestHandler<Q, P>> void setTarget(H target) {
        this.handler = target;
    }

    @Override
    public void resetPosition() {
        this.position = 0;
    }
}
