package com.back.gatewayservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
public class GatewayLoggingConfig {

    @Bean
    public GlobalFilter customGlobalFilter() {
        return (exchange, chain) -> {
            log.info("Request URI: {}", exchange.getRequest()
                    .getURI());
            return chain.filter(exchange)
                    .then(Mono.fromRunnable(() -> {
                        log.info("최종 응답 Status Code: {}", exchange.getResponse()
                                .getStatusCode());
                        log.info("응답 Header: {}", exchange.getResponse()
                                .getHeaders());
                    }));
        };
    }
}
