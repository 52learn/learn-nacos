package com.study.nacos.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
@Component
@RefreshScope
public class MyPropertiesComponent {
    @Value("${aliyun.sms.url}")
    private String aliyunSmsUrl;

    public String getAliyunSmsUrl() {
        return aliyunSmsUrl;
    }

    public void setAliyunSmsUrl(String aliyunSmsUrl) {
        this.aliyunSmsUrl = aliyunSmsUrl;
    }
}
