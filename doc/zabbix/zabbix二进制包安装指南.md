## Zabbix4.0安装指南

#### 安装前的准备

1. 关闭selinux

   > vi  /etc/selinux/config

   ![](https://github.com/18715838915/monitor-server/blob/main/doc/zabbix/img/selinuxConfig.png)

以上配置要重启后生效，不重启可临时关闭

> setenforce 0    临时关闭命令
>
> getenforce        查看selinux是否关闭，Permissive为关闭

2. 关闭防火墙

> systemctl stop firewalld   关闭防火墙
> systemctl disable firewalld   禁止防火墙开机启动

#### 搭建LAMP环境

1. 获取Mysql安装包

   > wget http://repo.mysql.com/mysql-community-release-el7-5.noarch.rpm

2. 安装Mysql rpm包

   > rpm -ivh mysql-community-release-el7-5.noarch.rpm
   >
   > yum install mysql-community-server -y

3. 重启mysql服务，首次登录mysql设置root密码

   > systemctl restart mysqld   重启Mysql服务
   >
   > mysql -u root 			首次登录mysql无须密码
   >
   >  set password for 'root'@'localhost' =password('password');	修改root密码

4. 创建zabbix数据库，并创建zabbix用户并授权

   > CREATE DATABASE zabbix character set utf8 collate utf8_bin;   创建数据库
   >
   > GRANT all ON zabbix.* TO 'zabbix'@'%' IDENTIFIED BY 'password';  创建用户并授权
   >
   > flush privileges;    刷新权限退出即可

5. 安装httpd

   > yum install -y httpd

6. 安装php

   > yum install -y  php php-mysql php-gd libjpeg* php-ldap php-odbc php-pear php-xml php-xmlrpc php-mhash
   >
   > rpm -qa httpd php   查看http php是否安装成功

7. 配置httpd

   > vim /etc/httpd/conf/httpd.conf

   ![](https://github.com/18715838915/monitor-server/blob/main/doc/zabbix/img/httpdConfig.png)

8. 配置php  设置中国时区

   > vim /etc/php.ini

   ![](https://github.com/18715838915/monitor-server/blob/main/doc/zabbix/img/phpTimeConfig.png)

9. 启动Httpd 

   > systemctl start httpd   
   >
   > systemctl enable httpd     开启自启动
   >
   > ss -anplt | grep httpd    查看Http是否已启动

10. 创建测试页，测试LAMP是否搭建成功

    > vi /var/www/html/index.php   填充以下内容
    >
    > `````php
    > <?php
    > phpinfo()
    > ?>
    > `````
    >
    > 输入  http://ip/index.php   出现php版本信息则说明搭建成功
    >
    > 修改index.php测试zabbix是否能登录成功
    >
    > `````php
    > <?php
    > $link=mysql_connect('localhost','zabbix','123456');
    > if($link) echo "<h2>Success</h2>";
    > 	else echo "Fail!!";
    > mysql_close();
    > ?>
    > `````
    >
    > 访问index.php若出现Fail则删除mysql中主机为空字符串的用户即可

    #### 安装zabbix-server和zabbix-web

    1. 安装依赖包

       > yum  install net-snmp net-snmp-devel curl curl-devel libxml2 libxml2-devel libevent-devel.x86_64 javacc.noarch  javacc-javadoc.noarch javacc-maven-plugin.noarch javacc*n

    2. 安装php支持zabbix

       > yum install php-bcmath php-mbstring –y

    3. 安装zabbix软件仓库配置包

       > wget http://repo.zabbix.com/zabbix/4.0/rhel/7/x86_64/zabbix-release-4.0-1.el7.noarch.rpm   获取
       >
       > rpm -ivh zbbix-release-4.0-1.el7.noarch.rpm 

       4. 安装zabbix-server 以及zabbix-web

          > yum install zabbix-server-mysql -y
          >
          > yum install zabbix-web-mysql -y

       5. 导入zabbix-server表数据到Mysql

       > zcat /usr/share/doc/zabbix-server-mysql-4.0.0/create.sql.gz | mysql -uzabbix -p -h localhost zabbix
       >
       > 如果当前指令无法执行，请进入create.sql.gz所在文件夹执行导入命令
       >
       > 在这里可能会卡一下，但属于正常情况

       6. zabbix-server配置数据库、用户、密码

          > vim /etc/zabbix/zabbix_server.conf
          >
          > ``````
          > DBName=zabbix
          > DBUser=zabbix
          > DBPassword=密码
          > ``````

       7. 修改时区

          > vim /etc/httpd/conf.d/zabbix.conf
          >
          > ![](https://github.com/18715838915/monitor-server/blob/main/doc/zabbix/img/httpZabbixTimeConfig.png)
          >
          > 若对于Httpd2.4版本及其以上请修改执行权限
          >
          > ![](https://github.com/18715838915/monitor-server/blob/main/doc/zabbix/img/httpZabbixGrantConfig.png)

       8. 登录zabbix首次配置页面配置数据库信息主机信息

          > http://ip/zabbix/setup.php

       9. 登录zabbix 主页即可

          > 用户名:Admin
          >
          > 密码:zabbix