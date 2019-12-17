一个基于棋牌、RPG游戏的分布式java游戏服务器，理论上可以无限水平扩展网关服，大厅服、游戏服达到人数承载。实现了集群注册中心，网关、登陆、后台服务器监控等通用服务器;封装了redis集群、mongodb等数据库处理；封装了消息队列、线程模型、及常用工具类。网关服务器使用mina封装了TCP、UDP、WebSocket、HTTP通信，使该框架能同时支持多种协议的客户端进行游戏。每个以scripts名字结尾的目录都为相应项目的脚本文件。

详细请查看[wiki](https://github.com/jzyong/game-server/wiki)

![项目架构图](https://raw.githubusercontent.com/jzyong/game-server/master/game-config/src/main/resources/image/server-architecture.jpg)

QQ交流群:
	144709243(已满)
	143469012
		


