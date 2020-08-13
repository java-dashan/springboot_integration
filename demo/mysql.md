# 					      mysql

### 1.mysql主从简介

- Mysql默认支持主从功能。(master slave)
- 配置完主从备份后效果:在主数据库中操作时,从数据库同步进行变化。
- 主从本质:主数据库的操作写入到日志中,从数据库从日志文件中读取,进行操作。
- 主从原理:
  - 默认mysql没用开启日志功能
  - 每个数据库需要有一个server_id,主server_id必须小于从server_id
  - 每个mysql都有一个uuid。(如果是虚拟机直接克隆,则需要修改这个uuid)
  - 必须要在主数据库中有一个用户具有被从数据库操作的权限。

![](C:\Users\da\AppData\Roaming\Typora\typora-user-images\mysql主从.png)

### 2.mycat简介

- 利用mysql主从备份功能实现读写分离
  - 增删改操作主数据库
  - 查询操作从数据库
  - 优点：提升程序执行性能和并发能力

- mycat解释:数据库中间件软件。

  ![](C:\Users\da\AppData\Roaming\Typora\typora-user-images\mycat作用.png)

- mycat具备分库/分表功能
  - 默认mycat分库(建议使用)--不同数据库所有表一样
  - 可以配置让mycat进行分表,业务比较复杂,配置也比较复杂

- mycat默认要求至少3个database
- 必须知道的mycat几个概念:
  - 逻辑库:一个包含了所有数据库的逻辑上的数据库
  - 逻辑表:一个包含了所有表的逻辑上的表(不是真实表)
  - 数据主机:数据库软件安装到哪个服务器上
  - 数据节点:数据库软件中的database
  - 分片规则:默认每个表中数据都一样的
  - 读主机:哪个数据库作为读操作
  - 写主机:哪个数据库作为写操作

### 3.mycat搭建

### 6.linux命令

```text
修改端口文件 vim /etc/sysconfig/iptables

重启端口服务 service iptables restart
```

