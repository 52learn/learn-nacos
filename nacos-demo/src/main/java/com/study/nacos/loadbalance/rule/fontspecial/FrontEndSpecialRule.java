package com.study.nacos.loadbalance.rule.fontspecial;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.Server;
import com.netflix.loadbalancer.ZoneAvoidanceRule;

/**
 * 前端可通过添加header头，实现指定后端feignClient调用本地服务（而不通过注册中心服务列表通过负载均衡随机服务）。如：请求头request=local
 */
public class FrontEndSpecialRule extends ZoneAvoidanceRule {
    private int serverPort;

    public FrontEndSpecialRule() {
    }

    public FrontEndSpecialRule(int serverPort) {
        this.serverPort = serverPort;
    }

    @Override
    public Server choose(Object key) {
        if ("local".equals(key)) {
            return new Server("127.0.0.1", serverPort);
        }
        return super.choose(key);

    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
        super.initWithNiwsConfig(clientConfig);
    }
}