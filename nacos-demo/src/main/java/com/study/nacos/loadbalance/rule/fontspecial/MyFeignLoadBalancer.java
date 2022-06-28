package com.study.nacos.loadbalance.rule.fontspecial;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.reactive.LoadBalancerCommand;
import org.springframework.cloud.netflix.ribbon.ServerIntrospector;
import org.springframework.cloud.openfeign.ribbon.FeignLoadBalancer;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class MyFeignLoadBalancer  extends FeignLoadBalancer {
    public MyFeignLoadBalancer(ILoadBalancer lb, IClientConfig clientConfig, ServerIntrospector serverIntrospector) {
        super(lb, clientConfig, serverIntrospector);
    }

    @Override
    protected void customizeLoadBalancerCommandBuilder(RibbonRequest request, IClientConfig config, LoadBalancerCommand.Builder<RibbonResponse> builder) {
        Map<String, Collection<String>> headers = request.getRequest().headers();
        Optional.ofNullable(headers.get("route")).ifPresent(new Consumer<Collection<String>>() {
            @Override
            public void accept(Collection<String> strings) {
                String value = strings.stream().findFirst().orElse(null);
                builder.withServerLocator(value);
            }
        });

    }

}
