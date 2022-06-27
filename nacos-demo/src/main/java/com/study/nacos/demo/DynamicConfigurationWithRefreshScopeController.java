package com.study.nacos.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Value Configuration with  {@link RefreshScope}
 */
@RefreshScope
@Slf4j
@RestController
public class DynamicConfigurationWithRefreshScopeController {
    @Value("${test:}")
    private String test;
    @GetMapping("/showTest")
    public String showTest(){
        log.info("test : {}",test);
        return test;
    }
}
