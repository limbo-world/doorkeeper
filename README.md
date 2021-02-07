<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [介绍](#%E4%BB%8B%E7%BB%8D)
  - [技术选型](#%E6%8A%80%E6%9C%AF%E9%80%89%E5%9E%8B)
  - [界面展示](#%E7%95%8C%E9%9D%A2%E5%B1%95%E7%A4%BA)
  - [概念](#%E6%A6%82%E5%BF%B5)
- [使用说明](#%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E)
- [安装教程](#%E5%AE%89%E8%A3%85%E6%95%99%E7%A8%8B)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->


# 介绍

![logo](https://github.com/LimboHome/limbo-authc/raw/master/doc/logo.jpg)

doorkeeper是一个可扩展的权限认证管理平台，可以在此平台上快速接入权限管理，**不是认证授权框架**(Shiro\Spring Security)，但可以集成认证授权框架一起使用。

这是一个独立的权限配置管理服务的脚手架，用于将登录认证、权限验证、权限管理从业务中抽离，便于业务系统快速接入权限功能。

## 技术选型
- Vue、ElementUI 前端组件库；
- SpringBoot Spring全家桶作为应用主框架；
- jwt 令牌认证；
- MyBatis、MyBatisPlus DAO层；
- MySQL 持久化关系数据库；

## 界面展示

## 概念

- **域** :   
  域表示独立的一块区域，域与域之间的所有数据是隔离的，如用户等。Doorkeeper属于特殊的域，用于数据管理操作。

- **用户/用户组** :   
  用户/用户组在域下唯一，一个用户可以加入多个用户组。用户/用户组都可以进行角色绑定。
  
- **委托方** :   
  根据项目需求，委托方可以是一个模块，也可以是一个项目。比如，定义域为仓储项目，那么委托方可以是库位模块等。
  
- **角色** :   
  可以给域或者委托方创建角色，主要用于绑定后的权限鉴定。
  
- **资源** :   
  资源是一个最小的认证单位，可以通过URI，也可以通过名称或者标签进行定义，平台提供了相应的搜索功能。
  比如定义一组资源为菜单，资源名称为菜单ID，用户可以通过搜索是否有对应ID的资源权限来达到菜单访问的能力。
  
- **策略** :   
  策略是通过不同权限访问的规则定义。
    
   > 目前策略类型有： 1. 角色 2. 用户 3. 用户组 4. 参数
     
- **权限** :   
  权限其实就是真正定义资源和访问策略的绑定关系的一个模型。

# 使用说明

初始账户 admin admin

 1. 域的创建和管理

![创建项目](https://github.com/LimboHome/limbo-authc/raw/master/doc/project.jpg)

 2. 委托方管理

![项目账户](https://github.com/LimboHome/limbo-authc/raw/master/doc/project-account.jpg)

 3. 委托方数据管理，资源、角色、策略、权限
 
![项目账户](https://github.com/LimboHome/limbo-authc/raw/master/doc/permission.jpg)
![项目账户](https://github.com/LimboHome/limbo-authc/raw/master/doc/role.jpg)
![项目账户](https://github.com/LimboHome/limbo-authc/raw/master/doc/account.jpg) 

 4. 根据需要进行权限设置后，第三方系统，只需要如下自定义拦截器，调用接口判断是否有权限访问接口(此处可接入授权认证框架Shiro等)

```
请求接口时，额外带上Header
Authorization:   登录后获取到的token
```

 7. 接口文档
```
http://ip:host/api-docs.html
```

# 安装教程
  
1. 将 `init/init-table.sql` 导入对应数据库

2. 修改配置文件

3. 根目录下执行命令打包编译，如开发环境

```
mvn clean package -Dmaven.test.skip=true -P dev
```

4. 初始化数据，访问接口，初始管理员账户 admin 密码 admin

```
http://ip:host/init
```

4. vue 目录下执行 npm install & npm run build，本地调试可以使用 npm run serve

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
