## 								linux 命令行

### 1.基本命令

- 1.1  关机和重启

```xml
关机
shutdown -h now    立刻关机
shutdown -h 5	   5分钟后关机
poweroff           立刻关机

重启
shutdown -r now    立刻重启
shutdown -r 5	   5分钟后重启
reboot			   立刻重启

```

- 1.2帮助命令

  ```
  --help    
  shutdown  -- help
  ifconfig  -- help   查看网卡信息
  
  
  man命令(命令说明书)
  man shutdown 
  注意： man shutdown 打开命令说明书之后,使用按键q退出
  ```

### 2.目录操作命令

- 2.1 目录切换cd

  ```java
  cd /	切换到根目录
  cd /usr	切换到根目录下的usr目录
  cd ../	切换到上级目录  或者cd..
  cd ~	切换到home目录
  cd -	切换到上次访问的目录
  ```

- 2.2目录查看

  ```
  ls		查看当前目录文件
  ls -a	查看当前目录下的所有目录和文件(包括隐藏文件)
  ls -l或ll  列表查看当前目录下的所有目录和文件 (列表查看,显示更多信息)
  ls /dir	查看指定目录下所有文件和目录 如：ls /usr
  ```
```
  
  

- 2.3目录操作

  ```java
  mkdir aaa	在当前目录下创建名为aaa的目录
  mkdir /usr/aaa   在指定目录下创建aaa目录
```

- 删除目录或文件

  ```java
  删除文件
  rm 文件名	删除当前目录下的文件
  rm -f 文件 删除当前目录下的文件 (不询问)  	
     
  删除目录
  rm -r 目录名	递归删除当前目录下的目录
  rm -rf 目录名	递归删除当前目录下的目录(不询问)    
      
  全部删除
  rm -rf *  删除当前目录下的所有mukluk和文件   
  rm -rf /* 删除根目录下所有文件 
  ```

- 2.4 目录修改 mv和cp

  ```java
  重命名
      mv 当前目录 新目录
      
  剪切目录
      mv 目录名称 新位置
      
  拷贝目录
      cp -r 目录名称 目录拷贝位置
  ```

- 2.5 搜素目录 find

  ```java
  find 目录 参数 文件名称
  示例： find /usr/tmp -name 'a*'  查找以a开头得目录或文件   
  ```
```
  
  

### 3.文件操作命令

- 3.1 新增文件

  ```java
  新建文件 touch
  示例：在当前目录下创建名为aaa.txt    touch aaa.txt    
```

- 3.2 删除文件

  ```java
  rm -rf 文件名
  ```

- 3.3 修改文件

  ```java
  vm 文件名
  vim 文件名
  
  vim 编辑器三种模式
      1.命令模式
      	(1)控制光标移动
      	(2)删除当前行：dd
      	(3)查找：/字符
      	(4)进入编辑模式： i o a
      	(5)进入底行模式： ：
      2.插入模式
      	只有在insert mode 模式下,菜可以做文字输入,按ESC键可回到命令行模式。
      3.底行模式
      将文件保存或者退出vi,也可以设置编辑环境,如寻找字符串,列出行号......等。
      	(1)退出编辑： ：q
      	(2)强制退出： ：!q
      	(3)保存并退出： ：wq
  ```

- 3.4 打开文件

  ```java
  vim 文件名称
  ```

- 3.5 文件得查看

  ```java
  cat aaa.txt   查看aaa文件,只能显示最后一屏内容
  
  more  aaa.txt 可以显示百分比，回车可以向下一行，空格可以向下一页，q可以退出查看
      
  less  aaa.txt  可以使用键盘上的PgUp和PgDn向上    和向下翻页，q结束查看
      
  tail -10 sudo.conf  查看/etc/sudo.conf文件的后10行，Ctrl+C结束      
  ```

  ```
  uname -a 查看内核版本
  
  cat /proc/version 查看内核版本
  
  cat /etc/redhat-release 查看系统版本
  
  top 相当于window任务管理器
  
  cat  filename 查看文本(小型)
  
  more filename 查看文本(大型) space下一页 enther下一行 
  
  less filename 不仅能分页,还可以搜索、回翻等操作
  
  head -n 2 filename 查看文本前两行
  
  tail -n 2 filename 查看文本后两行
  
  command > filename 将信息输入文本
  
  cat < filename 将信息输出
  
  systemctl name start == service name start
  
  ip addr == ifconfig
  
  tar -cvf a.tar.gz name 将文件打包成 a...
  
  tar -xvf a.tar.gz -C /.. 将a解压到...
  
  man command 查看某命令的使用方式
  
  grep 正则表达式 查找某种规则的...
  
  wc -(l w c) file 查看文件的行数,单词数,字节数
  
  history 查看历史命令
  
  yum -y install packagename 安装某包 全输入yes
  
  find path -name a* 查找某路径下以a开头的文件
  
  who 	查看在线用户
  whoami    显示当前在线用户
  
  mkdir  -(p ) 创建目录 -可以递归创建多层
  
  rmdir 删除文件夹
  
  rm -rf file/directory 删除文件 
  
  mv file /  移动文件 mv file file 改名称
  
  touch  创建文件
  
  sudo -i 切换到管理员
  
  cp file file 
  
  file/stat 查看文件类型或属性
  stat 	  显示文件详细信息  比ls 详细
  
  hostname 主机名
  
  kill  pid 杀死进程
  
  killall name 杀死与name相关的所有进程
  
  ssh root@192.168.40.1 远程登录xxx
  
  scp file root@192.168.40.1 path  传输文件到远程主机path下
  
  sftp 远程用户名@主机 //以安全模式进入ftp状态，此时可执行该状态下的命令： 
  
  　　get 远程路径 本地路径 //下载 
  
  　　put 本地路径 远程路径 //上传 
  
  
  ```
  
  
  
  ```
  正则表达式
  
  ^a  首字母以a开头
  a$  末字母为a
  .   匹配任意字符
  ?   可出现可不出现  xy?  x,xy
  *   可出现0次或者多次   xy*   xy,xyyyy  
  +   出现一次或一次以上
  [..] 出现任何一个  [xyz]  x,y,z
  ()  			(xy)+   xy,xyxy
  {n}  匹配n次		go{3}d  goood
  {n,} 至少匹配n次(包含n)  	go{2,}d good
  {n,m} 匹配n到m此(包含nm)  go(1,4)d  good
  |     逻辑或    good|job  good或者job
  \	  转义      \*    *
  
  ```
  
  













































