package com.study.nacos.loadbalance.rule.autoconfiguration;

import com.netflix.loadbalancer.IRule;
import com.study.nacos.loadbalance.rule.FeignLocalRouteRule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 注意： 不能同{@link FrontEndSpecialRouteRuleAutoConfiguration} 同时在spring.factories配置使用
 * 使用说明：
 * IDEA启动命令行中
 */
@Configuration
@AutoConfigureAfter({ServletWebServerFactoryAutoConfiguration.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@ConditionalOnProperty(name = "feign.route",havingValue = "local")
public class FeignLocalRouteRuleAutoConfiguration {
    @Value("${server.port}")
    private int serverPort;

    @Bean
    @ConditionalOnMissingBean
    public IRule feignLocalRouteRule(){
        return new FeignLocalRouteRule(serverPort);
    }
}
