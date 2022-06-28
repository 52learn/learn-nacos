package com.study.nacos.loadbalance.rule;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAvoidanceRule;

/**
 * Feign Route to localhost (127.0.0.1)
 */
public class FeignLocalRouteRule extends ZoneAvoidanceRule {
    private int serverPort;
    public FeignLocalRouteRule(){
    }
    public FeignLocalRouteRule(int serverPort){
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

