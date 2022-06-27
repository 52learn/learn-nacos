package com.study.nacos.loadbalance.rule;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAvoidanceRule;

/**
 * 使用场景： 当Ribbon无法通过正常的方式获取server时，使用本地的server返回
 */
public class MyZoneAvoidanceRule extends ZoneAvoidanceRule {

    @Override
    public Server choose(Object key) {
        Server server = super.choose(key);
        if(server == null){
            server = new Server("127.0.0.1",8080);
        }
        return server;
    }
    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        super.initWithNiwsConfig(clientConfig);
    }
}
