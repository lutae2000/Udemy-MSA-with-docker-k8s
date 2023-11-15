package com.example.gatewayserver;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

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
				.filters(gatewayFilterSpec -> gatewayFilterSpec.rewritePath("/utleebank/cards/(?<segment>.*)", "/${segment}")
					.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
					.requestRateLimiter(config -> config.setRateLimiter(redisRateLimiter())
						.setKeyResolver(userKeySolver())))
				.uri("lb://CARDS")
			).build();
	}

	@Bean
	public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer(){
		return reactiveResilience4JCircuitBreakerFactory -> reactiveResilience4JCircuitBreakerFactory.configureDefault(id -> new Resilience4JConfigBuilder(id)
			.circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
			.timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(4)).build()).build());
	}

	@Bean
	public RedisRateLimiter redisRateLimiter(){
		return new RedisRateLimiter(1, 1, 1);
	}

	@Bean
	KeyResolver userKeySolver(){
		return exchange -> Mono.justOrEmpty(exchange.getRequest().getHeaders().getFirst("user")).defaultIfEmpty("anonymous");
	}
}
