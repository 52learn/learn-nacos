package com.study.nacos.loadbalance.rule.fontspecial;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import org.springframework.cloud.client.loadbalancer.LoadBalancedRetryFactory;
import org.springframework.cloud.netflix.ribbon.ServerIntrospector;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.cloud.openfeign.ribbon.CachingSpringLoadBalancerFactory;
import org.springframework.cloud.openfeign.ribbon.FeignLoadBalancer;
import org.springframework.cloud.openfeign.ribbon.RetryableFeignLoadBalancer;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.util.Map;

public class MyCachingSpringLoadBalancerFactory extends CachingSpringLoadBalancerFactory {
    public MyCachingSpringLoadBalancerFactory(SpringClientFactory factory) {
        super(factory);
    }

    public MyCachingSpringLoadBalancerFactory(SpringClientFactory factory, LoadBalancedRetryFactory loadBalancedRetryPolicyFactory) {
        super(factory, loadBalancedRetryPolicyFactory);
    }
    private volatile Map<String, FeignLoadBalancer> cache = new ConcurrentReferenceHashMap<>();
    public FeignLoadBalancer create(String clientName) {
        FeignLoadBalancer client = this.cache.get(clientName);
        if (client != null) {
            return client;
        }
        IClientConfig config = this.factory.getClientConfig(clientName);
        ILoadBalancer lb = this.factory.getLoadBalancer(clientName);
        ServerIntrospector serverIntrospector = this.factory.getInstance(clientName,
                ServerIntrospector.class);
        client = this.loadBalancedRetryFactory != null
                ? new RetryableFeignLoadBalancer(lb, config, serverIntrospector,
                this.loadBalancedRetryFactory)
                : new MyFeignLoadBalancer(lb, config, serverIntrospector);
        this.cache.put(clientName, client);
        return client;
    }
}
