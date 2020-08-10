linux 安装es报错

1.max virtual memory areas vm.max_map_count [65530] is too low, increase to at least [262144]
    临时: sysctl -w vm.max_map_count=262144
    或者在/etc/sysctl.conf文件最后添加一行 vm.max_map_count=262144

2.
    /etc/security/limits.conf 添加
    * soft nofile 100000
    * hard nofile 100000

3.  上述配置不生效
    vi /etc/ssh/sshd_config
      将# UseLogin no 改成  UseLogin yes,并重启
      再使用hadoop 登录，ulimit -n 已经生效

4.  root不能登录
    添加用户 adduser dashan ,passwd dashan   aaaaaa
    切换用户 dashan

5.  将es安装目录属主权限改为esroot用户,chown -R esroot <es安装目录>

cluster.name: escluster  #集群名称，同一个集群的标识.
node.name: es1 #节点名称,其他机器es2 es3..
path.data: /home/data/es #数据存储目录
path.logs: /home/log/es #日志存储目录
node.master: true #允许该节点可以成为一个master
node.data: true #允许该节点存储数据(默认开启)
network.host: 192.168.19.102  #绑定监听IP,其他节点填各自的ip
http.port: 9200 #设置对外服务的http端口
transport.tcp.port: 9300 # 设置节点间交互的tcp端口
transport.tcp.compress: true #设置是否压缩tcp传输时的数据，默认为false
http.max_content_length: 100mb #设置内容的最大容量，默认100mb
#discovery.zen.ping.unicast.hosts: ["192.168.19.102","192.168.19.103","192.168.19.104"] # 7版本弃用 这是一个集群中的主节点的初始列表,当节点(主节点或者数据节点)启动时使用这个列表进行探测
discovery.seed_hosts: ["192.168.19.102","192.168.19.103","192.168.19.104"]
cluster.initial_master_nodes: ["192.168.19.102","192.168.19.103","192.168.19.104"]
gateway.recover_after_nodes: 2
gateway.recover_after_time: 5m
gateway.expected_nodes: 3
node.max_local_storage_nodes: 2 # 多个节点可以在同一个安装路径启动
#discovery.zen.minimum_master_nodes: 2 #7版本弃用 设置这个参数来保证集群中的节点可以知道其它N个有master资格的节点.默认为1,对于大的集群来说,可以设置大一点的值(2-4)
http.cors.enabled: true       #允许跨域访问，head使用
http.cors.allow-origin: "*"   #允许跨域访问，head使用

-------------------------------------------------------------->
vim /etc/security/limits.conf
* soft nofile 65537
* hard nofile 65537
* soft nproc 65537
* hard nproc 65537

vim /etc/sysctl.conf
vm.max_map_count = 262144
sysctl -p

cd /usr/local/src
tar -zxv -f elasticsearch-7.3.0-linux-x86_64.tar.gz -C /usr/local/
cd /usr/local/
cp elasticsearch-7.3.0 elasticsearch-7.3.0_node1
cp elasticsearch-7.3.0 elasticsearch-7.3.0_node2
cp elasticsearch-7.3.0 elasticsearch-7.3.0_node3
useradd elastic
chown -R elastic:elastic elasticsearch-7.3.0_node1
chown -R elastic:elastic elasticsearch-7.3.0_node2
chown -R elastic:elastic elasticsearch-7.3.0_node3
su elastic

查看集群状态
curl http://192.168.0.160:9200/_cat/nodes?v

curl http://192.168.0.160:9200/_cluster/health?pretty



# 如下是每个节点的配置文件内容
# 1
cluster.name: my-application
node.name: node-1
# 主节点
node.master: true
# 数据节点
node.data: true
network.host: 192.168.0.160
http.port: 9200
transport.port: 9300
discovery.seed_hosts: ["192.168.0.160:9300", "192.168.0.160:9301","192.168.0.160:9302"]
cluster.initial_master_nodes: ["node-1"] # 确保当前节点是主节点
http.cors.enabled: true
http.cors.allow-origin: "*"


# 2
cluster.name: my-application
node.name: node-2
node.master: false
node.data: true
network.host: 192.168.0.160
http.port: 9201
transport.port: 9301
discovery.seed_hosts: ["192.168.0.160:9300", "192.168.0.160:9301","192.168.0.160:9302"]
cluster.initial_master_nodes: ["node-1", "node-2","node3"]
http.cors.enabled: true
http.cors.allow-origin: "*"

# 3
cluster.name: my-application
node.name: node-3
node.master: false
node.data: true
network.host: 192.168.0.160
http.port: 9202
transport.port: 9302
discovery.seed_hosts: ["192.168.0.160:9300", "192.168.0.160:9301","192.168.0.160:9302"]
cluster.initial_master_nodes: ["node-1","node-2","node-3"]
http.cors.enabled: true
http.cors.allow-origin: "*"