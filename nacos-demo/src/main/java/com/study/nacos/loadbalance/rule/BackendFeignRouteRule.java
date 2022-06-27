package com.study.nacos.loadbalance.rule;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAvoidanceRule;

public class BackendFeignRouteRule extends ZoneAvoidanceRule {
    private int serverPort;
    public BackendFeignRouteRule(){
    }
    public BackendFeignRouteRule(int serverPort){
        this.serverPort = serverPort;
    }
    @Override
    public Server choose(Object key) {
        return new Server("127.0.0.1",serverPort);
    }
    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        super.initWithNiwsConfig(clientConfig);
    }
}

