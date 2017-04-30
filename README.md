# SoyDNS

## 简介
SoyDNS是一款高性能DNS服务器，同时支持UDP和TCP数据传输，防止DNS污染（区别于DNS劫持）。由于高效缓存，减少响应时间。支持服务端配置。

## Instruction

```
mvn clean install -Dmaven.test.skip=true
sudo java -jar Soy-GateKeeper/target/Soy-DoorKeeper-1.0.0-SNAPSHOT.jar

```
## 原理
* 高性能
利用缓存技术，减少向上游的请求,提升系统吞吐率。

* 抗污染
利用基于TCP协议


