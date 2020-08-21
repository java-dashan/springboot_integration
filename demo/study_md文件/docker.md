# windows 10

`下载地址`: http://mirrors.aliyun.com/docker-toolbox/windows/docker-toolbox/

1.下载docker  yum install docker -y

2.修改镜像源   /etc/docker/daemon.json Docker  (官方中国区  https://registry.docker-cn.com
                                                网易云 http://hub-mirror.c.163.com
                                                阿里云 https://pee6w651.mirror.aliyuncs.com)

            {
                 "registry-mirrors": ["https://docker.mirrors.ustc.edu.cn"] #中国科技大学
            }

            临时指定镜像源: docker pull hub.c.163.com/library/zookeeper:latest //网易云

3.docker下载zookeeper / kafka
docker pull  wurstmeister/zookeeper
docker pull  wurstmeister/kafka


4.使用 docker-compose -f docker-compose.yml up -d 命令启动集群服务  (docker-compose command not exist)

    配置yum 镜像源 vim /etc/yum.repos.d
    查看centOS版本 lsb_release -a 或者 cat /etc/redhat-release
    # 配置为阿里镜像源
        wget -O /etc/yum.repos.d/CentOS-Base.repo http://mirrors.aliyun.com/repo/Centos-7.repo
    # 设置缓存
        yum makecache
    # 更新镜像配置，可以看到阿里镜像源
        yum -y update

    docker-compose安装
    1.curl -L https://get.daocloud.io/docker/compose/releases/download/1.25.0/docker-compose-`uname -s`-`uname -m` > /usr/local/bin/docker-compose
      curl -L https://get.daocloud.io/docker/compose/releases/download/1.24.0/docker-compose-`uname -s`-`uname -m` > /usr/local/bin/docker-compose
      更改权限: chmod +x /usr/local/bin/docker-compose
    2.  python方式
        yum -y install epel-release
        yum -y install python-pip
        pip install -i https://pypi.douban.com/simple  --upgrade pip //使用豆瓣镜像升级pip
        pip install -i https://pypi.douban.com/simple docker-compose //使用豆瓣镜像安装

    docker-compose up -f /xxx.xml -d  (后台运行)  如不指定-f则默认使用当前目录下的docker-compose.xml

    出现xxx:Permission denied时
        临时关闭selinux: 1.getenforce 2.setenforce 0  3.getenforce
        永久关闭: vim /etc/sysconfig/selinux  -->SELINUX=enforcing 改为 SELINUX=disabled //然后重启服务器

5.  docker 容器管理

    docker ps -a // 查看所有容器
    docker ps -a -q // 查看所有容器ID
    docker stop $(docker ps -a -q) //  stop停止所有容器
    docker  rm $(docker ps -a -q) //   remove删除所有容器
    docker start containId        //启动容器


6.镜像管理
    删除镜像
        docker rmi [image]
        docker image rm [image]
        docker rmi imageId
    清理镜像残存文件0
        docker image prune -a, --all: 删除所有没有用的镜像，而不仅仅是临时文件；-f, --force：强制删除镜像文件，无需弹出提示确认;

    docker build -t springboot .   //以当前目录下Dockerfilex构建名为springboot的镜像


7.进入容器
    docker exec -it 容器ID /bin/bash
    ctrl+d 退出容器


8.docker 设置远程访问
    vim /usr/lib/systemd/system/docker.service //添加 -H tcp://0.0.0.0:2375 -H unix:///var/run/docker.sock \
    重启服务
    systemctl daemon‐reload
    systemctl restart docker

9.搭建kafka,zk集群完整实例------------> https://blog.csdn.net/wchyumo2009/article/details/88965053


10.网桥 network:

         docker network create --driver bridge --subnet 172.19.0.0/16 --gateway 172.19.0.1 --opt
                "com.docker.network.bridge.name"="docker1000" nt17219
         --driver 指定驱动
         --subnet 指定网络接口
         --opt    指定参数    bridge.name 是显示再ifconfig上的名称  nt17219 是显示再docker network ls 上的名称

         docker network ls   查看网桥
         docker network inspect [name]  查看网桥具体信息
         docker network rm      [name]  删除网桥
         docker network rm -f $(docker network ls)

11.数据卷 volume:

         docker volume ls
         docker volume create
         docker volume rm


12.docker swarm:

    docker swarm init       初始化主节点

    docker swarm join \
        --token SWMTKN-1-19t0veqdgc19v3ff0tl8f4uasa7cw2eghocqzd7rfdgzgvbn8n-advvrvohggj9gz97xsd75qlwj \
        172.16.1.41:2377    子节点加入swarm

    docker service ls

    docker service scale [name]=5    扩容/复制


-----------------------------------kafka集群yml----------------------------------------------
docker pull zookeeper
docker pull wurstmeister/kafka
docker pull hlebalbau/kafka-manager

>>> zk_kafka.yml


坑:  1. docker容器内提示Permission denied，这个权限问题怎么解决？
    2. docker启动shell断开连接
        删除br- 开头的网关接口  ------> ifconfig br-*** down
    3.外网访问:
        0.0.0.0:2186->2181/tcp
        通过宿主机IP:2186访问
    4. docker的网络配置https://blog.csdn.net/hetoto/article/details/99892743?utm_medium=distribute.pc_aggpage_search_result.none-task-blog-2~all~first_rank_v2~rank_v25-8-99892743.nonecase
    5. Docker的跨主机网络访问（不同宿主机上的容器之间的通信）https://blog.csdn.net/Rapig1/article/details/102470936
    6. docker swarm 扩容是如何处理端口冲突

注意:   如果image是通过临时指定镜像拉取的,需带上前缀如: docker pull hub.c.163.com/library/zookeeper:latest --> hub.c.163.com/library/zookeeper
       volumes: -./zookeeper/zoo1/data:/data 可以写相对路径