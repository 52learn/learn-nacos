package com.study.nacos.demo.listener;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 主逻辑同{@link org.springframework.cloud.endpoint.event.RefreshEventListener}, 为了自定义监听配置变更。
 *  使用{@link com.alibaba.nacos.api.config.annotation.NacosConfigListener} 监听配置变更无效，很奇怪?????????????????
 */
@Slf4j
public class ConfigChangedEventListener implements SmartApplicationListener {

    private AtomicBoolean ready = new AtomicBoolean(false);
    public ConfigChangedEventListener() {
    }
    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return ApplicationReadyEvent.class.isAssignableFrom(eventType)
                || EnvironmentChangeEvent.class.isAssignableFrom(eventType);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof ApplicationReadyEvent) {
            handle((ApplicationReadyEvent) event);
        }
        else if (event instanceof EnvironmentChangeEvent) {
            handle((EnvironmentChangeEvent) event);
        }
    }

    public void handle(ApplicationReadyEvent event) {
        this.ready.compareAndSet(false, true);
    }

    public void handle(EnvironmentChangeEvent event) {
        if (this.ready.get()) { // don't handle events before app is ready
            Set<String> keys = event.getKeys();
            log.info("...............ConfigChangedEventListener  Refresh keys changed: " + keys);
        }
    }

}
