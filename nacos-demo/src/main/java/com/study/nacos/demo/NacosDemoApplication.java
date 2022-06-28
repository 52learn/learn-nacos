package com.study.nacos.demo;

import com.alibaba.cloud.nacos.ribbon.NacosRule;
import com.netflix.loadbalancer.IRule;
import com.study.feign.FeignRequestInterceptor;
import com.study.nacos.demo.feign.GreetingClient;
import com.study.nacos.demo.listener.ConfigChangedEventListener;
import com.study.nacos.loadbalance.rule.MyZoneAvoidanceRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Bean
    public FeignRequestInterceptor feignRequestInterceptor(){
        FeignRequestInterceptor feignRequestInterceptor = new FeignRequestInterceptor();
        return feignRequestInterceptor;
    }
    //@Bean
    //@ConditionalOnMissingBean
    public IRule ribbonRule() {
        NacosRule nacosRule = new NacosRule();
        return nacosRule;
    }

    //@Bean
    //@ConditionalOnMissingBean
    public IRule myZoneAvoidanceRule(){
        return new MyZoneAvoidanceRule();
    }

    @RestController
    class SayController{
        @Autowired
        private GreetingClient greetingClient;
        @GetMapping("/api/say")
        public String sayHello(){
            return greetingClient.sayHello()+" (by feign)";
        }
    }
}
