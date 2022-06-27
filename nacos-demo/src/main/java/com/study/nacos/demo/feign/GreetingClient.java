package com.study.nacos.demo.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "greeting-service")
public interface GreetingClient {
    @GetMapping("/say")
    String sayHello();
}
