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


### Reference 
1. Environment Changes  
https://cloud.spring.io/spring-cloud-static/Greenwich.SR2/multi/multi__spring_cloud_context_application_context_services.html#_environment_changes
2. Refresh Scope  
https://cloud.spring.io/spring-cloud-static/Greenwich.SR2/multi/multi__spring_cloud_context_application_context_services.html#refresh-scope


# ISSUES:
nacos-server-2.1.0 docker-compose 启动会报错，详见：https://github.com/alibaba/nacos/issues/8142  


