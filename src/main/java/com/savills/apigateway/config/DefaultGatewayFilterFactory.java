package com.savills.apigateway.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Slf4j
@Component
public class DefaultGatewayFilterFactory extends AbstractGatewayFilterFactory<DefaultGatewayFilterFactory.Config> {


    //构造方法
    public DefaultGatewayFilterFactory() {
        super(Config.class);
    }

    //返回一个List   ->  负责指定接收网关参数的顺序
    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList("arg1", "arg2", "arg3", "arg4", "arg5");
    }

    //主要方  => 负责 执行网关流程
    @Override
    public GatewayFilter apply(Config config) {
        return new GatewayFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

                log.info("进入Default网关");
                log.info("打印网关参数:" + config.arg1);
                log.info("打印网关参数:" + config.arg2);
                log.info("打印网关参数:" + config.arg3);
                log.info("打印网关参数:" + config.arg4);
                log.info("打印网关参数:" + config.arg5);
                log.info("退出Default网关");
                return chain.filter(exchange);
            }
        };
    }

    //Config静态内部类 ->  负责指定网关的参数
    @Data
    static class Config {
        private String arg1;
        private String arg2;
        private String arg3;
        private String arg4;
        private String arg5;
    }


//    //   值越小，优先级越高
//    @Override
//    public int getOrder()
//    {
//        return 100;
//    }


}