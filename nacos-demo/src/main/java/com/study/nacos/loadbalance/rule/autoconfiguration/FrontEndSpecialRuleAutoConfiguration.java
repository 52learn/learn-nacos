package com.study.nacos.loadbalance.rule.autoconfiguration;

import com.netflix.loadbalancer.IRule;
import com.study.nacos.loadbalance.rule.fontspecial.DefaultFeignLoadBalancedConfiguration;
import com.study.nacos.loadbalance.rule.fontspecial.FrontEndSpecialRule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 注意： 不能同{@link BackendFeignRouteRuleAutoConfiguration} 同时在spring.factories配置使用
 */
@Configuration
@AutoConfigureAfter({ServletWebServerFactoryAutoConfiguration.class})
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@Import({DefaultFeignLoadBalancedConfiguration.class})
public class FrontEndSpecialRuleAutoConfiguration {
    @Value("${server.port}")
    private int serverPort;

    @Bean
    @ConditionalOnMissingBean
    public IRule frontEndSpecialRule(){
        return new FrontEndSpecialRule(serverPort);
    }
}
