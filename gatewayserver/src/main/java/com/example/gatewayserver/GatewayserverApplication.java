package com.example.gatewayserver;

import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;

@SpringBootApplication
public class GatewayserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}

	@Bean
	public RouteLocator utleeBankRouteConfig(RouteLocatorBuilder routeLocatorBuilder){
		return routeLocatorBuilder.routes()
			.route(predicateSpec -> predicateSpec.path("/utleebank/accounts/**")
				.filters(gatewayFilterSpec -> gatewayFilterSpec.rewritePath("/utleebank/accounts/(?<segment>.*)", "/${segment}")
					.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
					.circuitBreaker(config -> config.setName("accountsCircuitBreaker")
						.setFallbackUri("forward:/contactSupport")))
				.uri("lb://ACCOUNTS")
			)
			.route(predicateSpec -> predicateSpec.path("/utleebank/loans/**")
				.filters(gatewayFilterSpec -> gatewayFilterSpec.rewritePath("/utleebank/loans/(?<segment>.*)", "/${segment}")
					.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
					.retry(retryConfig -> retryConfig.setRetries(3)
						.setMethods(HttpMethod.GET)
						.setBackoff(Duration.ofMillis(100), Duration.ofMillis(1000),2, true)))
				.uri("lb://LOANS")
			)
			.route(predicateSpec -> predicateSpec.path("/utleebank/cards/**")
				.filters(gatewayFilterSpec -> gatewayFilterSpec.rewritePath("/utleebank/cards/(?<segment>.*)", "/${segment}"))
				.uri("lb://CARDS")
			).build();
	}

}
