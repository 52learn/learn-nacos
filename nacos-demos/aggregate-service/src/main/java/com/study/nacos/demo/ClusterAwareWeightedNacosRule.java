package com.study.nacos.demo;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.ExtendBalancer;
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
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Loadbalance Rule：
 * 优先过滤相同clusterName下的服务名的实例列表，不存在就取其他clusterName下的服务名实例列表；
 * 实例列表中再按照权重值选择
 */

public class ClusterAwareWeightedNacosRule extends AbstractLoadBalancerRule {
    private static final Logger logger = LoggerFactory.getLogger(ClusterAwareWeightedNacosRule.class);
    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;
    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {

    }

    @Override
    public Server choose(Object key) {
        try {
            String clusterName = this.nacosDiscoveryProperties.getClusterName();
            DynamicServerListLoadBalancer loadBalancer = (DynamicServerListLoadBalancer) getLoadBalancer();
            String name = loadBalancer.getName();

            NamingService namingService = this.nacosDiscoveryProperties.namingServiceInstance();

            List<Instance> instances = namingService.selectInstances(name, true);
            if (CollectionUtils.isEmpty(instances)) {
                return null;
            }

            List<Instance> instancesToChoose = instances;
            if (!StringUtils.isEmpty(clusterName)) {
                List<Instance> sameClusterInstances = instances.stream()
                    .filter(instance -> Objects.equals(clusterName, instance.getClusterName()))
                    .collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(sameClusterInstances)) {
                    instancesToChoose = sameClusterInstances;
                } else {
                    logger.warn("cross different cluster remote invoke : name = {}, clusterName = {}, instance = {}", name, clusterName, instances);
                }
            }

            Instance instance = ExtendBalancer.getHostByRandomWeight2(instancesToChoose);

            return new NacosServer(instance);
        } catch (Exception e) {
            logger.warn("ClusterAwareWeightedNacosRule exception:", e);
            return null;
        }
    }
}
