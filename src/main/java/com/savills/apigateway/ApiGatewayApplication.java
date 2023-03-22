package com.savills.apigateway;

import com.savills.apigateway.helper.AdvLog;
import com.savills.apigateway.helper.BaseLog;
import com.savills.apigateway.helper.LoggerThread;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.ApplicationProtocolNegotiator;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import javax.net.ssl.*;
import java.net.http.HttpClient;
import java.util.List;

@SpringBootApplication
@MapperScan("com.savills.apigateway.generator.mapper")
public class ApiGatewayApplication {

    public static void main(String[] args) {

        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder){
        String str = "http://localhost:8096/csdn";
        return builder.routes()
                //id 表示被转发到uri地址的id名，
                .route("id",p -> p
                        //predicates，当访问的连接满足http://localhost:8096/csdn时即转发到https://blog.csdn.net
                        .path("/csdn")
                        .uri("https://blog.csdn.net"))

//                .route("id_1",p -> p
//                        //predicates，当访问的连接满足http://localhost:8096/csdn时即转发到https://blog.csdn.net
//                        .path("/echo2")
//                        .uri("https://webhook.site/a238e6c5-c3e5-4060-a667-61eef751b473"))

                //.route("test", r -> r.path("/test/**").filters(f -> f.rewritePath("/test(?<path>.*)", "/${path}")).uri("https://www.google.com"))

                .build();
    }
//    @Bean
//    public WebClient webClient() throws Exception {
//        reactor.netty.http.client.HttpClient httpClient = reactor.netty.http.client.HttpClient
//
//               .create();
//            //    .secure(t -> t.sslContext(context)); // Reference our context defined above
//        return WebClient.builder().clientConnector(new ReactorClientHttpConnector(httpClient)).build();
//    }

//class ConfigAuto
//{
//
//    public void init(){
//
//        SSLEngine sslEngine = getSSLEngine(); // Get your sslEngine reference
//
//// The below code will reconfigure Netty's SSLEngine to enable hostname verification
//        SSLParameters sslParameters = sslEngine.getSSLParameters();
//        sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
//        sslEngine.setSSLParameters(sslParameters);
//
//        SslContext context = new SslContext() {
//            // Alter the below context as needed, adding in any extra configurations needed for trust stores and such.
//            // I have provided a JDK default implementation that should work in most situations
//            SslContext sslContext = SslContextBuilder.forClient()
//                    .sslProvider(SslProvider.JDK)
//                    .keyManager(KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm()))
//                    .trustManager(TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm()))
//                    .build();
//
//            @Override
//            public boolean isClient() {
//                return sslContext.isClient();
//            }
//
//            @Override
//            public SSLSessionContext sessionContext() {
//                return sslContext.sessionContext();
//            }
//
//            @Override
//            public List<String> cipherSuites() {
//                return sslContext.cipherSuites();
//            }
//
//            @Override
//            public ApplicationProtocolNegotiator applicationProtocolNegotiator() {
//                return sslContext.applicationProtocolNegotiator();
//            }
//
//            @Override
//            public SSLEngine newEngine(ByteBufAllocator byteBufAllocator) {
//                SSLEngine sslEngine = sslContext.newEngine(byteBufAllocator);
//                // Hostname verification fix below:
//                SSLParameters sslParameters = sslEngine.getSSLParameters();
//                sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
//                sslEngine.setSSLParameters(sslParameters);
//                return engine;
//            }
//
//            @Override
//            public SSLEngine newEngine(ByteBufAllocator byteBufAllocator, String s, int i) {
//                SSLEngine sslEngine = sslContext.newEngine(byteBufAllocator, s, i);
//                // Hostname verification fix below:
//                SSLParameters sslParameters = sslEngine.getSSLParameters();
//                sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
//                sslEngine.setSSLParameters(sslParameters);
//                return engine;
//            }
//        };
//    }
//}

}
