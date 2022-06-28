package com.study.nacos.demo;

import com.alibaba.cloud.nacos.NacosConfigProperties;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.alibaba.nacos.client.naming.NacosNamingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

//@EnableFeignClients
@SpringBootApplication
//@EnableDiscoveryClient
public class AServiceApplication implements CommandLineRunner , EnvironmentAware {


    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${spring.cloud.nacos.discovery.weight}")
    private Integer weight;


    Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(AServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }
    @NacosConfigListener(dataId = "service-a")
    public void onMessage(String config) {
        System.out.println("NacosConfigListener....onMessage...change config: " + config);

    }


    @RestController
    public class ApplicationController{
        Logger logger = LoggerFactory.getLogger(getClass());
        @Autowired
        private NacosDiscoveryProperties nacosDiscoveryProperties;
        /**
         * 用于测试权重流量
         * @return
         */
        @RequestMapping("/test/weight")
        public Map<String,Object> testWeight(){
            Map<String,Object> map = new HashMap<>();
            map.put("port", environment.getProperty("local.server.port"));
            map.put("applicationName",applicationName);
            map.put("weight",weight);
            map.put("",nacosDiscoveryProperties.getClusterName());
            return map;
        }

        /**
         * 用于集群优先负责均衡的测试
         * @return
         */
        @RequestMapping("/test/clusterName")
        public Map<String,Object> testClusterName(){
            Map<String,Object> map = new HashMap<>();
            map.put("port", environment.getProperty("local.server.port"));
            map.put("applicationName",applicationName);
            map.put("clusterName",nacosDiscoveryProperties.getClusterName());
            return map;
        }

        @RequestMapping("/test/levelInvoke")
        public Map<String,Object> testIevelInvoke(){
            Map<String,Object> map = new HashMap<>();
            map.put("port", environment.getProperty("local.server.port"));
            map.put("applicationName",applicationName);
            map.put("metadata",nacosDiscoveryProperties.getMetadata());
            return map;
        }

        /**
         * 通过nacos client sdk的NamingMaintainService类的updateInstance方法更新实例信息。这里只修改实例信息表示是否启用该实例，可用于优雅上下线。
         * 测试URL：
         * http://127.0.0.1:56682/disable/192.168.99.1$56682$hangzhou$DEFAULT_GROUP@@service-a
         * @param instanceId  需将$替换为#
         * @return
         */
        @RequestMapping("/{instanceId}/{enabled}")
        public String disableOrEnableInstance(@PathVariable("enabled") Boolean enabled,@PathVariable("instanceId") String instanceId){
            final String _instanceId = instanceId.replace("$","#");
            Instance instance = new Instance();
            /*instance.setClusterName("hangzhou");
            instance.setEnabled(false);
            instance.setEphemeral(true);
            instance.setHealthy(true);
            instance.setInstanceId(instanceId);
            instance.setIp("192.168.99.1");
            instance.setMetadata();*/
            try {
                List<Instance> instances = nacosDiscoveryProperties.namingServiceInstance().getAllInstances("service-a","DEFAULT_GROUP");
                Optional<Instance> targetInstanceOptional = instances.stream().filter(x->x.getInstanceId().contains(_instanceId)).findFirst();
                if(targetInstanceOptional.isPresent()){
                    Instance targetInstance = targetInstanceOptional.get();
                    logger.info("instance : {}",targetInstance);
                    targetInstance.setEnabled(enabled);
                    //nacosDiscoveryProperties.namingServiceInstance().registerInstance(targetInstance.getServiceName(),targetInstance);
                    //nacosDiscoveryProperties.namingServiceInstance().deregisterInstance(targetInstance.getServiceName(),targetInstance);
                    nacosDiscoveryProperties.namingMaintainServiceInstance().updateInstance(targetInstance.getServiceName(),targetInstance);
                    return targetInstance.toString();
                }
                //nacosNamingService.deregisterInstance("service-a","DEFAULT_GROUP",instance);
            } catch (NacosException e) {
                e.printStackTrace();
            }
            return "InstanceId not found...";
        }
    }



}
