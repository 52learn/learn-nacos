# Running Environment
## start nacos server
use : https://github.com/52learn/local-vagrant/blob/master/boxes/centos7/nacos/nacos-docker-1.4.1/example/standalone-mysql-5.7.yaml  
```
docker-compose -f example/standalone-mysql-5.7.yaml up -d
```
## configuration
greet-app.yaml:  
```
test: 12345
user:
  name: kim
  address: hangzhou
```


# Feature List
## Dynamic Modifying Configuration Without restart application
### com.study.nacos.demo.DynamicConfigurationWithRefreshScopeController
Test Step:  
1. find the dataId greet-app.yaml , edit it
2. modify key : test
3. send the post request:
```
curl -X POST "http://127.0.0.1:8080/actuator/refresh"  
```
4. check the configuration key : test changed
```
http://127.0.0.1:8080/showConfiguration
```
### com.study.nacos.demo.DynamicConfigurationWithConfigurationPropertiesController
```
http://127.0.0.1:8080/showUserProperties
or 
http://127.0.0.1:8080/actuator/configprops
```


nacos dynamic configuration 实现源码：
com.alibaba.cloud.nacos.refresh.NacosContextRefresher.registerNacosListener

## Configuration Change Listener
com.study.nacos.demo.listener.ConfigChangedEventListener  

## feign route to localhost service
1. spring.factories configuration 
```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.study.nacos.loadbalance.rule.autoconfiguration.FeignLocalRouteRuleAutoConfiguration
```

2. start application with VM options:
```
-Dfeign.route=local
```
3. visit the link
http://127.0.0.1:8080/api/say

## feign route rule by front end special 
1. spring.factories configuration  
```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.study.nacos.loadbalance.rule.autoconfiguration.FrontEndSpecialRouteRuleAutoConfiguration
```
2. curl 
```
curl http://127.0.0.1:8080/api/say -H "route:local"
```

Use Case:  
1. 在服务应用层面，只需要让前端请求头上带上route=环境标识，可以为同一类提供多套环境，比如：为开发环境提供dev-a,dev-b,dev-c 等多套开发环境；减少部署多套的成本  


## Provide the default Server when ribbon can not choose one  
com.study.nacos.loadbalance.rule.MyZoneAvoidanceRule

## NacosRule
com.alibaba.cloud.nacos.ribbon.NacosRule

### Reference 
1. Environment Changes  
https://cloud.spring.io/spring-cloud-static/Greenwich.SR2/multi/multi__spring_cloud_context_application_context_services.html#_environment_changes
2. Refresh Scope  
https://cloud.spring.io/spring-cloud-static/Greenwich.SR2/multi/multi__spring_cloud_context_application_context_services.html#refresh-scope


# ISSUES:
nacos-server-2.1.0 docker-compose 启动会报错，详见：https://github.com/alibaba/nacos/issues/8142  


