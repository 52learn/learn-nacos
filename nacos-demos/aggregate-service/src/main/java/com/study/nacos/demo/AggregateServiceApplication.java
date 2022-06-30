package com.study.nacos.demo;

import com.alibaba.cloud.nacos.ribbon.NacosRule;
import com.netflix.loadbalancer.IRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

//@EnableFeignClients
@SpringBootApplication
//@EnableDiscoveryClient
public class AggregateServiceApplication implements CommandLineRunner  {

    @Value("${spring.application.name}")
    private String applicationName;

    public static void main(String[] args) {
        SpringApplication.run(AggregateServiceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

    }


    @RestController
    public class ApplicationController{
        @Autowired
        private RestTemplate restTemplate;

        @Autowired
        private LoadBalancerClient loadBalancerClient;
        /**
         * 用于测试权重流量
         * @return
         */
        @RequestMapping("/test/weight")
        public Map<String,Object> testWeight(){
            return restTemplate.getForObject("http://service-a/test/weight",Map.class);
        }

        /**
         * 用于集群优先负责均衡的测试
         * @return
         */
        @RequestMapping("/test/clusterName")
        public Map<String,Object> testClusterName(){
            return restTemplate.getForObject("http://service-a/test/clusterName",Map.class);
        }

        /**
         * 用于服务分层调用测试，即：根据实例的metadata中level值，level值越小越靠上层，上层能调用下层，但下层不能调用上层服务
         * @return
         */
        @RequestMapping("/test/levelInvoke")
        public Map<String,Object> testIevelInvoke(){
            return restTemplate.getForObject("http://service-a/test/levelInvoke",Map.class);
        }

        @RequestMapping("/user")
        public String user(){
            return restTemplate.getForObject("http://service-a/user",String.class);
        }
    }


    @Bean
    @LoadBalanced
    RestTemplate restTemplate(){
        return new RestTemplate();
    }

    /**
     集群优先和权重负载均衡规则（全局配置）
     **/
    //@Bean
    IRule iRule(){
        return new NacosRule();
    }

    /**
     * 服务分层调用测试（全局配置）
     * @return
     */
    //@Bean
    IRule levelInvokeNacosRule(){
        return new LevelInvokeNacosRule();
    }

}
