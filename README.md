# SoyDNS

## 简介
SoyDNS是一款高性能DNS代理服务器，同时支持UDP和TCP数据传输，防止DNS污染及DNS劫持。由于DNS是基于无认证的UDP协议，客户端在向公网服务端请求DNS解析时，
中间路由节点很容易通过伪造应答报文（投毒），来达到欺骗的目的。而一些公网DNS服务器由于受到攻击，篡改数据，导致解析结果被恶意篡改。
SoyDNS通过如下措施保障客户端DNS解析正确。

1. 基于TCP协议与上游DNS服务器通信：如上文描述，DNS被污染的重要原因是基于UDP所致，SoyDNS可以配置成TCP形式与上游服务器通信，以避免投毒攻击；
2. 可信上游DNS服务：可以在SoyDNS中，将上游DNS配置成一些可信DNS（如8.8.8.8）等，避免DNS劫持；
3. 注意到投毒攻击的特点，中间路由节点在投毒时，仍然会上目标DNS服务器转发解析请求，实际上，我们只要在返回的所有应答请求中，甄别出错误的应答并剔除，
同样可以做到避免污染。

* 除了抗劫持，防污染外，SoyDNS还提供了丰富的本地解析能力，适合在企业内部使用。可以在SoyDNS服务器上配置/etc/soy/resolve，来解析私有域名。
* 同时，现在许多企业APP也是受DNS污染（劫持）的重灾区，SoyDNS提供HTTP接口，可用于APP的私有DNS解析。

## 部署

在本机部署：

```
git clone git@github.com:ahuazhu/SoyDNS.git
cd SoyDNS
mvn clean install -Dmaven.test.skip=true
sudo java -jar Soy-GateKeeper/target/Soy-DoorKeeper-1.0.0-SNAPSHOT.jar

```
验证：

```
dig @localhost www.facebook.com

```

