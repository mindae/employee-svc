package com.mindae.employeesvc.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class EmpConfig {
    @Bean
    @LoadBalanced
    RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
    /*
    * @Bean
@LoadBalanced
public RestTemplate restTemplate(RestTemplateBuilder builder) {
    return builder
            .connectTimeout(Duration.ofSeconds(2))
            .readTimeout(Duration.ofSeconds(3))
            .build();
}
    * */
}
