package org.bhuvanesh.orderservice.config;

import org.bhuvanesh.orderservice.client.ProductServiceClient;
import org.bhuvanesh.orderservice.client.UserServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class AppConfig {

    @Value("${services.user-service}")
    private String userServiceUrl;

    @Value("${services.product-service}")
    private String productServiceUrl;

    @Bean
    @LoadBalanced
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    @Bean
    public UserServiceClient userServiceClient(RestClient.Builder builder) {
        RestClient restClient = builder.baseUrl(userServiceUrl).build();
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(UserServiceClient.class);
    }

    @Bean
    public ProductServiceClient productServiceClient(RestClient.Builder builder) {
        RestClient restClient = builder.baseUrl(productServiceUrl).build();
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(ProductServiceClient.class);
    }
}
