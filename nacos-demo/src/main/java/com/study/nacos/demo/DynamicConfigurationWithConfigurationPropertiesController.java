package com.study.nacos.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * Configuration use  will be re-bind in the contxt because of Environment Changes( EnvironmentChangeEvent)
 * Without the {@link org.springframework.cloud.context.config.annotation.RefreshScope} Annotaion
 */
@Slf4j
@RestController
@EnableConfigurationProperties(value = UserProperties.class)
public class DynamicConfigurationWithConfigurationPropertiesController {

    @Autowired
    private UserProperties userProperties;
    @GetMapping("/showUserProperties")
    public String showUserProperties(){
        log.info("userProperties : {}",userProperties);
        return userProperties.toString();
    }
}
