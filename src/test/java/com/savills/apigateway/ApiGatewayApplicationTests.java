package com.savills.apigateway;

import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@MapperScan("com.savills.apigateway.generator.mapper")
class ApiGatewayApplicationTests {
    @Test
    void contextLoads() {
    }

}
