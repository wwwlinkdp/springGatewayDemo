package com.savills.apigateway.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.savills.apigateway.helper.AesUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.factory.rewrite.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerStrategies;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

@Slf4j
@Component
public class CustomGatewayFilterFactory extends AbstractGatewayFilterFactory<CustomGatewayFilterFactory.Config> {

//    @Override
//    public String name() {
//        //返回用于配置的名称
//        return "AddPrefix";
//    }

    @Autowired
    private ObjectMapper objectMapper;

    private final List<HttpMessageReader<?>> messageReaders = HandlerStrategies.withDefaults().messageReaders();

    //构造方法
    public CustomGatewayFilterFactory() {
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

                log.info("进入Demo网关");
                log.info("打印网关参数:" + config.arg1);
                log.info("打印网关参数:" + config.arg2);
                log.info("打印网关参数:" + config.arg3);
                log.info("打印网关参数:" + config.arg4);
                log.info("打印网关参数:" + config.arg5);
                log.info("退出Demo网关");

                if(config.arg1.equals("C1"))
                {
                    ServerRequest serverRequest = ServerRequest.create(exchange,
                            messageReaders);

                    Mono<String> modifiedBody = serverRequest.bodyToMono(String.class)
                            .flatMap(originalBody -> modifyBody().apply(exchange,Mono.just(originalBody)));

                    BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody,String.class);
                    HttpHeaders headers = new HttpHeaders();
                    headers.putAll(exchange.getRequest().getHeaders());
                    headers.remove(HttpHeaders.CONTENT_LENGTH);
                    CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange,headers);

                    return bodyInserter.insert(outputMessage, new BodyInserterContext())
                            .then(Mono.defer(() -> {
                                ServerHttpRequest decorator = decorate(exchange, headers,outputMessage);
                                return chain.filter(exchange.mutate().request(decorator).build());
                            }));
                }
                if(config.arg1.equals("C2")) {
                    ServerHttpRequest req = exchange.getRequest();
                    String path = req.getURI().getRawPath();
                    String newPath = path;//path.replaceAll(config.regexp, replacement);
                    ServerHttpRequest request = req.mutate()
                            .path(newPath)
                            .build();
                    return chain.filter(exchange.mutate().request(request).build());
                }


               return chain.filter(exchange);
              //  return chain.filter(exchange);

               // return processRequest(exchange, chain);
            }
        };
    }
    private void setPayloadTextNode(String text, JsonNode root) {
        try {
            ObjectNode objectNode = (ObjectNode) root;
            objectNode.set("payload", new TextNode(text));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
    private BiFunction<ServerWebExchange, Mono<String>, Mono<String>> modifyBody() {
        return (exchange, body) -> {
            try {
                AtomicReference<String> result = new AtomicReference<>();
                body.subscribe(value -> {
                    String v = value;
                        try {
                            JsonNode jsonObject = objectMapper.readTree(value);

                            JsonNode payload = jsonObject.get("payload");
                            String payloadText = payload.asText();

                            byte[] content = AesUtils.X.decrypt(payloadText);
                            String requestBody = new String(content, StandardCharsets.UTF_8);
                            setPayloadTextNode(requestBody,jsonObject);
                            v = jsonObject.toString();
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }

                            log.info(v);
                     result.set(v);
                        },
                        e -> log.error(e.getMessage(), e)
                );
                return Mono.just(result.get());
            } catch (Exception e) {
                log.error("gateway parameter decryption exception", e);
                throw new RuntimeException("Parameter decryption exception");
            }
        };
    }
    private ServerHttpRequestDecorator decorate(ServerWebExchange exchange, HttpHeaders headers,
                                                CachedBodyOutputMessage outputMessage) {
        return new ServerHttpRequestDecorator(exchange.getRequest()) {
            @Override
            public HttpHeaders getHeaders() {
                long contentLength = headers.getContentLength();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.putAll(super.getHeaders());
                if (contentLength > 0) {
                    httpHeaders.setContentLength(contentLength);
                }
                else {
                    httpHeaders.set(HttpHeaders.TRANSFER_ENCODING, "chunked");
                }
                return httpHeaders;
            }

            @Override
            public Flux<DataBuffer> getBody() {
                return outputMessage.getBody();
            }
        };

    }
/////////////////////////////


    //Config静态内部类 ->  负责指定网关的参数
    @Data
    static class Config {
        private String arg1;
        private String arg2;
        private String arg3;
        private String arg4;
        private String arg5;
    }


}