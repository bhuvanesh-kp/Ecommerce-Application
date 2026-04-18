package org.bhuvanesh.userservice.config;

import org.bhuvanesh.userservice.client.ProductServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class AppConfig {

    @Value("${services.product-service}")
    private String productServiceUrl;

    @Bean
    public ProductServiceClient productServiceClient() {
        RestClient restClient = RestClient.builder().baseUrl(productServiceUrl).build();
        return HttpServiceProxyFactory
                .builderFor(RestClientAdapter.create(restClient))
                .build()
                .createClient(ProductServiceClient.class);
    }
}
