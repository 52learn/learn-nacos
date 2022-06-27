package com.study.nacos.demo;

import com.study.nacos.demo.listener.ConfigChangedEventListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;

@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
public class NacosDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(NacosDemoApplication.class, args);
    }

    @Bean
    public ConfigChangedEventListener configChangedEventListener() {
        return new ConfigChangedEventListener();
    }
}
