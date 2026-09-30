package com.findmyroomie.api_gateway.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.filter.LoadBalancerFilterFunctions.lb;
import static org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions.route;
import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
public class GatewayRoutes {

    @Bean
    public RouterFunction<ServerResponse> authRoute() {

        return route("auth-service")
                .GET("api/auth/**",http())
                .POST("/api/auth/**", http())
                .filter(lb("AUTH-SERVICE"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> userRoute() {

        return route("user-service")
                .GET("/api/users/**", http())
                .POST("/api/users/**", http())
                .PUT("/api/users/**", http())
                .DELETE("/api/users/**", http())
                .filter(lb("USER-SERVICE"))
                .build();
    }
}