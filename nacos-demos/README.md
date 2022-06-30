# nacos特性测试说明
## 权重流量测试
1.测试步骤：

- 对service-a 打包
- 启动service-a ,分别设置不同的权重值, 如下：请求6次，通过端口区分对应实例
    - java  -Dspring.cloud.nacos.discovery.weight=100 -Dserver.port=0 -jar target/service-a-0.0.1-SNAPSHOT.jar 
    - java  -Dspring.cloud.nacos.discovery.weight=10 -Dserver.port=0 -jar target/service-a-0.0.1-SNAPSHOT.jar 
    - java  -Dspring.cloud.nacos.discovery.weight=5 -Dserver.port=0 -jar target/service-a-0.0.1-SNAPSHOT.jar
- 调用服务aggregate-service需要使用NacosRule负责均衡规则:
```
@Bean
    IRule iRule(){
        return new NacosRule();
    }
```
- 启动aggregate-service服务，访问：http://127.0.0.1:8888/test/weight

2.使用场景

- 按不同物理配置分配不同流量：若服务A有10个实例节点，其占用的物理资源（或部署的机器配置）是不一样的，那么可以通过权重值将更多的流量（访问调用）流向高配的实例节点

- 无损下线（优雅下线）：服务A有3实例（A1，A2，A3)，需要对服务A升级，将一个实例(如：A1)的权重值设置为0（后续所有请求就不会进来了，而不是直接kill，因为直接kill的话可能会将刚进来还没有处理完成的请求给终止掉），等待几秒观察A1实例已经没有流量后，
再将A1升级，A2，A3也同样进行。

- 优雅上线（滚动上线）：服务A有3实例（A1，A2，A3), 服务A有缺陷需要修复上线，先将A1无损下线，升级部署A1实例并重启，调整A1的权重值为最大整型值，将其他实例权重值调整为0，这样就会将请求全部落到更新后的A1上，观察升级后的A1功能是否正常，若正常就可以继续升级A2、A3实例，当然这里要考虑大流量负载问题。(可以另外准备几个节点来升级)

- 

3.其他说明
经测试发现，调用方采用nacos作为服务寻址，是可以动态感知到被调用服务实例的变化（增加或者删除；但不是实时的，大概隔几秒）。

## 集群优先测试
1. 测试步骤：

- 对service-a 打包
- 启动service-a ,分别设置不同clusterName
    - java -Dspring.cloud.nacos.discovery.clusterName=hangzhou -Dserver.port=0 -jar target/service-a-0.0.1-SNAPSHOT.jar
    - java -Dspring.cloud.nacos.discovery.clusterName=hangzhou -Dserver.port=0 -jar target/service-a-0.0.1-SNAPSHOT.jar
    - java -Dspring.cloud.nacos.discovery.clusterName=shanghai -Dserver.port=0 -jar target/service-a-0.0.1-SNAPSHOT.jar
- 调用服务aggregate-service的spring.cloud.nacos.discovery.clusterName设置为hangzhou
- 配置aggregate-service的 spring.factories文件：
```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.study.nacos.autoconfiguration.ClusterAwareWeightedRibbonAutoConfiguration
```
- 启动aggregate-service服务，访问：http://127.0.0.1:8888/test/clusterName , hangzhou的服务响应返回(与调用服务的clusterName一致))：
```
{"port":"64195","clusterName":"hangzhou","applicationName":"service-a"}
```
- 将clusterName为hangzhou的服务关闭，再次访问，返回shanghai的服务。
 ```
 {"port":"64205","clusterName":"shanghai","applicationName":"service-a"}
 ```
2.使用场景
- 区域就近访问：若系统面向全国（全球）用户，可以根据用户访问来源(如：IP解析获取)，访问区域网关服务，网关服务与下游服务均部署在相同的区域(即网关服务和下游服务的clusterName是同一个区域)，可以实现就近访问，加快响应速度。

- 容灾：

    - 同城容灾

      可在同个城市部署两个机房，两个机房部署差别只在不同位置，比如：杭州萧山机房(clusterName=xiaoshan)，杭州余杭机房(clusterName=yuhang)。

    - 异地多活

      可按城市部署机房，比如：杭州机房 ， 北京机房

- 

3.参考  
http://www.itmuch.com/spring-cloud-alibaba/ribbon-nacos-weight-cluster/



## 标签路由测试
描述：通过定义metadata，然后采用基于元数据的路由算法规则实现路由算法  

具体实现类似：com.study.nacos.demo.LevelInvokeNacosRule  


若service-a服务有三个不同版本的实例，其中tag=master为主线版本代码打包启动的服务，而tag=v1.2,tag=v2分别为v1.2、v2版本代码打包启动的服务
启动如下： 
```
java -Dspring.cloud.nacos.discovery.metadata.tag=master -jar target/service-a-0.0.1-SNAPSHOT.jar
java -Dspring.cloud.nacos.discovery.metadata.tag=v1.2 -jar target/service-a-0.0.1-SNAPSHOT.jar
java -Dspring.cloud.nacos.discovery.metadata.tag=v2 -jar target/service-a-0.0.1-SNAPSHOT.jar  

```
当调用方需要调用与调用方tag一致的服务，比如：tag=v2，则：  
```
java -Dspring.cloud.nacos.discovery.metadata.tag=v2 -jar target/aggregate-service-0.0.1-SNAPSHOT.jar  
```


1.测试步骤


2.使用场景

- 多套开发环境

  存在多个迭代分支同时开发的情况，需要分别为每个迭代开发提供一套开发环境。比如：feature-1，feature-2，feature-3 三个分支同时开发，我们需要提供dev-a,dev-b,dev-c 环境，三个环境间的服务是互相隔离的，每个环境只能访问内部的服务。


3. 参考：  
https://www.alibabacloud.com/help/en/enterprise-distributed-application-service/latest/configure-tag-based-routing-for-a-spring-cloud-application


## 服务分层调用测试

1.测试步骤

- 对service-a服务打包

- 启动service-a，设置标签level (根据实例的metadata中level值，level值越小越靠上层，上层能调用下层，同层之间可以互调，但下层不能调用上层服务)

  java -Dspring.cloud.nacos.discovery.metadata.level=-10000 -jar target/service-a-0.0.1-SNAPSHOT.jar

  java -Dspring.cloud.nacos.discovery.metadata.level=2 -jar target/service-a-0.0.1-SNAPSHOT.jar

- 设置aggregate-service的spring.cloud.nacos.discovery.metadata.level为0

- 配置aggregate-service的 spring.factories文件：
```
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
com.study.nacos.autoconfiguration.LevelInvokeNacosRuleRibbonAutoConfiguration
```
   

- 启动aggregate-service，访问：http://127.0.0.1:8888/test/levelInvoke ，虽然上面启动了service-a两个实例，但每次只能访问到level为2的实例。

  ```
  {"metadata":{"preserved.heart.beat.timeout":"15","preserved.ip.delete.timeout":"30","preserved.register.source":"SPRING_CLOUD","level":"2","preserved.heart.beat.interval":"5"},"port":"61675","applicationName":"service-a"}
  ```

  当level=2的实例停止后，再次访问链接就报错提示，错误日志：

  ```
  2020-11-29 21:09:27.415  WARN 15148 --- [nio-8888-exec-1] c.study.nacos.demo.LevelInvokeNacosRule  : lower level service can not invoke higher level service , current service level: 0,target Service Name: service-a
  2020-11-29 21:09:27.426 ERROR 15148 --- [nio-8888-exec-1] o.a.c.c.C.[.[.[/].[dispatcherServlet]    : Servlet.service() for servlet [dispatcherServlet] in context with path [] threw exception [Request processing failed; nested exception is java.lang.IllegalStateException: No instances available for service-a] with root cause
  
  java.lang.IllegalStateException: No instances available for service-a
  	at org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient.execute(RibbonLoadBalancerClient.java:119) ~[spring-cloud-netflix-ribbon-2.2.2.RELEASE.jar:2.2.2.RELEASE]
  	at org.springframework.cloud.netflix.ribbon.RibbonLoadBalancerClient.execute(RibbonLoadBalancerClient.java:99) ~[spring-cloud-netflix-ribbon-2.2.2.RELEASE.jar:2.2.2.RELEASE]
  ```

  页面显示：

  ```
  Whitelabel Error Page
  This application has no explicit mapping for /error, so you are seeing this as a fallback.
  
  Sun Nov 29 21:09:27 CST 2020
  There was an unexpected error (type=Internal Server Error, status=500).
  ```

  

2.使用场景

- 服务治理：从技术层面约束微服务间无规则调用问题，服务间应该按照层级关系来调用，上层服务可以调用下层服务，同层服务间可以互调，但是下层服务不能调用上层服务。


## 服务下线测试
1.测试步骤
- 对service-a 打包
- 启动service-a
    - java  -jar target/service-a-0.0.1-SNAPSHOT.jar 
    - java  -jar target/service-a-0.0.1-SNAPSHOT.jar 
- 启动aggregate-service服务
- 打开nacos管理后台页面，点击服务列表菜单，确保serice-a有2个服务实例和aggregate-service有1个服务实例
- 访问链接：http://127.0.0.1:8888/user ，观察service-a的两个控制台趋向均衡的打印输出日志。
- 选择service-a的其中一个服务实例，在Operation操作栏中点击Offline按钮下线。
- 观察aggregate-service服务控制台，几乎与点击下线操作同时接收到udp报文：
```
2021-02-04 10:36:28.222  INFO 22684 --- [g.push.receiver] com.alibaba.nacos.client.naming          : received push data: {"type":"dom","data":"{\"hosts\":[{\"ip\":\"192.168.99.1\",\"port\":64865,\"valid\":true,\"healthy\":true,\"marked\":false,\"instanceId\":\"192.168.99.1#64865#hangzhou#DEFAULT_GROUP@@service-a\",\"metadata\":{\"preserved.heart.beat.timeout\":\"15\",\"preserved.ip.delete.timeout\":\"30\",\"preserved.register.source\":\"SPRING_CLOUD\",\"level\":\"1\",\"preserved.heart.beat.interval\":\"5\"},\"enabled\":true,\"weight\":1.0,\"clusterName\":\"hangzhou\",\"serviceName\":\"DEFAULT_GROUP@@service-a\",\"ephemeral\":true}],\"dom\":\"DEFAULT_GROUP@@service-a\",\"name\":\"DEFAULT_GROUP@@service-a\",\"cacheMillis\":10000,\"lastRefTime\":1612406188206,\"checksum\":\"b216366d3c451ebd72e3218358d85b52\",\"useSpecifiedURL\":false,\"clusters\":\"\",\"env\":\"\",\"metadata\":{}}","lastRefTime":243318313484300} from /172.25.176.1
2021-02-04 10:36:28.239  INFO 22684 --- [g.push.receiver] com.alibaba.nacos.client.naming          : removed ips(1) service: DEFAULT_GROUP@@service-a -> [{"instanceId":"192.168.99.1#64887#hangzhou#DEFAULT_GROUP@@service-a","ip":"192.168.99.1","port":64887,"weight":1.0,"healthy":true,"enabled":true,"ephemeral":true,"clusterName":"hangzhou","serviceName":"DEFAULT_GROUP@@service-a","metadata":{"preserved.heart.beat.timeout":"15","preserved.ip.delete.timeout":"30","preserved.register.source":"SPRING_CLOUD","level":"1","preserved.heart.beat.interval":"5"},"instanceHeartBeatTimeOut":15,"instanceHeartBeatInterval":5,"ipDeleteTimeout":30}]
2021-02-04 10:36:28.246  INFO 22684 --- [g.push.receiver] com.alibaba.nacos.client.naming          : current ips:(1) service: DEFAULT_GROUP@@service-a -> [{"instanceId":"192.168.99.1#64865#hangzhou#DEFAULT_GROUP@@service-a","ip":"192.168.99.1","port":64865,"weight":1.0,"healthy":true,"enabled":true,"ephemeral":true,"clusterName":"hangzhou","serviceName":"DEFAULT_GROUP@@service-a","metadata":{"preserved.heart.beat.timeout":"15","preserved.ip.delete.timeout":"30","preserved.register.source":"SPRING_CLOUD","level":"1","preserved.heart.beat.interval":"5"},"instanceHeartBeatTimeOut":15,"instanceHeartBeatInterval":5,"ipDeleteTimeout":30}]

```
- 立马访问链接：http://127.0.0.1:8888/user ，发现负载均衡返回实例还包含了已经下线的实例，即没有立马被摘除,需要过一定时间。其原理详见：  
Nacos questioned why my service is still available when it is clearly offline?  
https://blog.fearcat.in/a?ID=00001-841dc12b-46c9-43d2-8acb-8f142b598ae2

2.如何实现通过nacos控制台上下线服务后立马让调用方感知到？
配置文件中添加：
```
#service-a客户端负载均衡器的服务端列表刷新间隔时间设置
service-a:
  ribbon:
    ServerListRefreshInterval: 2000
```
```
#所有服务的客户端负载均衡器的服务端列表刷新间隔时间设置
ribbon:
  ServerListRefreshInterval: 2000
```

相关源码：  
- com.netflix.loadbalancer.PollingServerListUpdater  


3.使用场景
当需要对注册到Nacos注册中心的web服务进行重启升级，那么需要及时通知该web服务的上游调用者其下线了，通过在nacos管理控制台页面的下线操作按钮，就可以将指定的web实例通过UDP报文发送给所有监听器。



