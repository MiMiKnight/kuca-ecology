package cn.yhm.developer.kuca.ecology.init;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 预置数据初始化
 *
 * @author victor2015yhm@gmail.com
 * @since 2023-03-12 19:54:31
 */
@Component
public class EcologyInitialize implements CommandLineRunner {

    private RequestResponseHandlerContainer handlerContainer;

    private HandlerInterceptorContainer<?, ?, ?, ?> handlerInterceptorContainer;

    @Autowired
    public void setHandlerContainer(RequestResponseHandlerContainer handlerContainer) {
        this.handlerContainer = handlerContainer;
    }

    @Autowired
    public void setHandlerInterceptorContainer(HandlerInterceptorContainer<?, ?, ?, ?> handlerInterceptorContainer) {
        this.handlerInterceptorContainer = handlerInterceptorContainer;
    }

    @Override
    public void run(String... args) throws Exception {
        handlerContainer.init();
        handlerInterceptorContainer.init();
    }
}
