package com.study.nacos.demo;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.ExtendBalancer;
import com.alibaba.cloud.nacos.ribbon.NacosRule;
import com.alibaba.cloud.nacos.ribbon.NacosServer;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.DynamicServerListLoadBalancer;
import com.netflix.loadbalancer.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 服务分层调用负载均衡规则:
 * 根据实例的metadata中level值，level值越小越靠上层，上层能调用下层，同层之间可以互调，但下层不能调用上层服务
 */
public class LevelInvokeNacosRule extends AbstractLoadBalancerRule {
    private static final Logger LOGGER = LoggerFactory.getLogger(LevelInvokeNacosRule.class);

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;
    @Override
    public Server choose(Object key) {
        try {
            DynamicServerListLoadBalancer loadBalancer = (DynamicServerListLoadBalancer) getLoadBalancer();
            String name = loadBalancer.getName();

            NamingService namingService = nacosDiscoveryProperties
                .namingServiceInstance();
            List<Instance> instances = namingService.selectInstances(name, true);
            if (CollectionUtils.isEmpty(instances)) {
                LOGGER.warn("no instance in service {}", name);
                return null;
            }
            String levelStr = nacosDiscoveryProperties.getMetadata().get("level");
            Integer selfLevel = StringUtils.isEmpty(levelStr) ? 0:Integer.parseInt(levelStr);
            List<Instance> filterdInstances = instances.stream()
                .filter(instance -> {
                    String instanceLevelStr = instance.getMetadata().get("level");
                    Integer instanceLevel = StringUtils.isEmpty(instanceLevelStr) ? 0:Integer.parseInt(instanceLevelStr);
                    return selfLevel <= instanceLevel;
                })
                .collect(Collectors.toList());
            if (CollectionUtils.isEmpty(filterdInstances)){
                LOGGER.warn("lower level service can not invoke higher level service , current service level: {},target Service Name: {}", selfLevel,name);
                return null;
            }
            Instance instance = ExtendBalancer.getHostByRandomWeight2(filterdInstances);
            return new NacosServer(instance);
        }
        catch (Exception e) {
            LOGGER.warn("NacosRule error", e);
            return null;
        }
    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {

    }

}
