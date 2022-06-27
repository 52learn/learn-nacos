package com.study.nacos.demo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("user")
public class UserProperties {

    @Value("${name:}")
    private String name;
    @Value("${address:}")
    private String address;


}

