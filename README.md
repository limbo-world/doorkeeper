<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [介绍](#%E4%BB%8B%E7%BB%8D)
  - [项目架构](#%E9%A1%B9%E7%9B%AE%E6%9E%B6%E6%9E%84)
  - [技术选型](#%E6%8A%80%E6%9C%AF%E9%80%89%E5%9E%8B)
  - [概念](#%E6%A6%82%E5%BF%B5)
- [使用说明](#%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E)
- [安装教程](#%E5%AE%89%E8%A3%85%E6%95%99%E7%A8%8B)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->


# 介绍

这是个脚手架项目，可以在此基础上快速接入权限管理，**不是认证授权框架**(Shiro\Spring Security)，但可以集成认证授权框架一起使用。

这是一个独立的权限配置管理服务的脚手架，用于将登录认证、权限验证、权限管理从业务中抽离，便于业务系统快速接入权限功能。大致用法是：
1. 首先在权限管理后台中配置业务系统的权限、菜单、角色；
2. 在业务系统中web接口执行前，调用权限管理服务的接口判断用户是否有权限访问web接口；
3. 业务系统获知用户是否有权访问之后，可自行决定是否允许访问。
  
## 项目架构  

![项目架构图](https://github.com/LimboHome/limbo-authc/raw/master/doc/system.png)
  
- **Doorkeeper Server** : 项目、账户、角色、权限等服务操作；
- **Mysql** : 授权服务使用的DB；
- **Redis** : 权限服务的会话管理和数据缓存；
- **App** : 其他的业务系统；

## 技术选型
- Vue、Vuex、Vue-router 前端框架；
- ElementUI 前端组件库；
- SpringBoot Spring全家桶作为应用主框架；
- Redis、Redisson 缓存与分布式会话实现；
- Spring Cloud Eureka；
- MyBatis、MyBatisPlus DAO层；
- MySQL 持久化关系数据库；

## 概念
权限管理系统中常见的概念。

- **权限** :   
  项目下按权限名称唯一，一个带请求方式的API，如 get /account
    
- **角色** :   
  项目下按角色名称唯一，角色是"权限"的集合，同时角色可以额外配置一些"权限策略"，在除了菜单拥有的权限之外，可以设置角色是否拥有或排除(根据策略)单独的某个(某些)权限；
    
   > 拦截的优先级大于放行的，所以只要配置了一个拦截，当账户拥有多个角色的时候，对于此权限也是拦截的。

- **账户** :   
  全局唯一，通过登录认证获取会话信息；

- **项目** :   
  项目代表了全部在授权服务中管理权限配置的业务系统，必须在创建了项目之后，业务系统才能正确调用授权服务的接口；
    
- **项目账户** :   
  可以将某个账户绑定到特定项目下面，这样就可以对他绑定这个项目的角色；

- **管理员** :   
  管理员拥有当前项目下权限系统的所有接口权限（非第三方自己项目）；

# 使用说明

初始账户 admin admin

 1. 创建项目

![创建项目](https://github.com/LimboHome/limbo-authc/raw/master/doc/project.jpg)

 2. 新建账户并加入项目并设置为管理员

![项目账户](https://github.com/LimboHome/limbo-authc/raw/master/doc/project-account.jpg)

 3. 使用新建的项目管理员账户登陆权限管理后台，配置项目的权限、角色、账户角色
 
![项目账户](https://github.com/LimboHome/limbo-authc/raw/master/doc/permission.jpg)
![项目账户](https://github.com/LimboHome/limbo-authc/raw/master/doc/role.jpg)
![项目账户](https://github.com/LimboHome/limbo-authc/raw/master/doc/account.jpg) 

 5. 业务系统自动为账户授权，或者登陆权限管理后台进行账户的授权

 6. 第三方系统，只需要如下自定义拦截器，调用接口判断是否有权限访问接口(此处可接入授权认证框架Shiro等)

```
请求接口时，额外带上两个Header
Doorkeeper-Project: projectId
Doorkeeper-Token:   登录后获取到的token
```

 7. 接口文档
```
http://ip:host/api-docs.html
```

# 安装教程
  
1. 将 `init/doorkeeper.sql` 导入对应数据库

2. 修改配置文件

3. 根目录下执行命令打包编译

```
mvn clean package -Dmaven.test.skip=true -P dev
```

4. vue 目录下执行 npm install & npm run build

5. nginx配置
```
server {
  listen 80;
  server_name doorkeeper.limbo.org;
  location / {
      root /path/of/dist;
      autoindex on;
      autoindex_exact_size on;
      autoindex_localtime on;
  }
  
  location /api {
      # 后端接口
      proxy_pass http://127.0.0.1:8890/;
  }
}
```
