		一个基于棋牌游戏的分布式java游戏服务器，理论上可以无限水平扩展网关服，大厅服、游戏服达到人数承载。每个以scripts名字结尾的目录都为相应项目的脚本文件。


![项目架构](https://github.com/jzyong/game-server/tree/master/game-config/src/main/resources/image/server-architecture.jpg)
		
		
# game-engine
Java游戏服务器开发核心组件包，包括：<br>
1、Mina和Netty对TCP、HTTP、Udp、WebSocket的通信底层封装<br>
2.ActiveMQ通信、Redis订阅发布等中间件封装<br>
3、mongodb、redis集群数据底层封装，redis使用jedis、fastjson的序列化和反序列化封装<br>
4、线程模型、消息处理器的设计封装<br>
5、常用工具类<br>
6、java源文件的脚本封装，实现服务器的热更新<br>

# game-model
游戏公共模块，基础实体类，公共逻辑等

# game-message
游戏protobuf通信消息

# game-maven-plugin
游戏代码生成等maven插件

# game-tool
游戏开发工具<br>
1.mongodb配置表导表工具<br>
2.服务器压测客户端<br>

# game-config
游戏配置文件项目：<br>
1.excel配置表

# game-cluster
服务器注册中心，管理所有分布式服务器：<br>
1.管理所有服务器的状态、内存等常用信息<br>
2.负载均衡网关服务器的连接数<br>
3.后台监控服务器获取服务器相关监控信息<br>

# game-gate
游戏网关服务器，转发所有客户端连接消息：<br>
1.转发角色消息到大厅服、游戏服务<br>
2.负载均衡大厅服和游戏服的连接数<br>

# game-hall
游戏大厅服务器，处理相应大厅界面逻辑<br>
1.登录，邮件、聊天、背包等<br>
2.角色基本信息管理<br>

# game-bydr
游戏服务器示例，简单的捕鱼游戏示例：<br>
1.房间管理，鱼群刷新，开炮结束等基础流程<br>

# game-bydr-world
捕鱼游戏服务器世界服：<br>
1.跨服竞技匹配等功能<br>

