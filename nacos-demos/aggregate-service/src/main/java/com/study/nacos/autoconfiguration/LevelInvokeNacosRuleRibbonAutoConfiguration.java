package com.study.nacos.autoconfiguration;

import com.netflix.loadbalancer.IRule;
import com.study.nacos.demo.ClusterAwareWeightedNacosRule;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RibbonClient(name = "service-a", configuration = LevelInvokeNacosRuleRibbonAutoConfiguration.RibbonConfig.class)
public class LevelInvokeNacosRuleRibbonAutoConfiguration {
    static class RibbonConfig {
        @Bean
        public IRule rule() {
            return new ClusterAwareWeightedNacosRule();
        }
    }
}