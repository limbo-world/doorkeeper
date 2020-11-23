<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [介绍](#%E4%BB%8B%E7%BB%8D)
  - [项目架构](#%E9%A1%B9%E7%9B%AE%E6%9E%B6%E6%9E%84)
  - [技术选型](#%E6%8A%80%E6%9C%AF%E9%80%89%E5%9E%8B)
  - [目录结构](#%E7%9B%AE%E5%BD%95%E7%BB%93%E6%9E%84)
  - [概念](#%E6%A6%82%E5%BF%B5)
- [使用说明](#%E4%BD%BF%E7%94%A8%E8%AF%B4%E6%98%8E)
- [设计思想](#%E8%AE%BE%E8%AE%A1%E6%80%9D%E6%83%B3)
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
  
- **Authc Server** : 授权服务，项目、账户、角色、菜单、权限等数据均应通过Authc Server服务操作；
- **Authc DB** : 授权服务使用的DB；
- **Authc Admin Server** : 权限管理后台，是用于管理全部项目权限的系统，它本身也是授权服务中定义的一个项目，也依赖授权服务进行权限管理；开发第三方App时后端的认证和授权校验可以参考这个项目；如果希望自行开发权限管理后台，可以不用这个，但是要注意配置api访问认证密码；
- **Admin DB** : 权限管理后台admin使用的DB，用于管理admin项目中的业务数据；Admin DB和Authc DB可以使用同一个，但因为admin项目本身也是一个业务App，建议不要和Authc DB混用；
- **Admin Redis** : 权限管理后台admin进行数据缓存、会话管理的KV数据库；
- **App** : 其他的业务系统；Authc Admin本质上也是一个App；

## 技术选型
- Vue、Vuex、Vue-router 前端框架；
- ElementUI 前端组件库；
- Tomcat 作为Web容器；
- SpringBoot Spring全家桶作为应用主框架；
- Redis、Redisson 缓存与分布式会话实现；
- Apache Dubbo RPC框架；
- Spring Cloud Eureka RPC框架(待实现)；
- Nacos 配置中心与注册中心；
- MyBatis、MyBatisPlus DAO层；
- MySQL 持久化关系数据库；
- 分库分表(待实现);
- 分布式事务(待实现);

## 目录结构
```
├── init                                // DB初始化脚本  
├── limbo-authc-admin                   // 权限管理后台的web后端 Authc Admin
│   ├── front-end                       // 权限管理后台的web前端 Authc Admin Web
├── limbo-authc-api     
│   ├── limbo-authc-api-dubbo           // 授权服务 Authc Service 
│   └── limbo-authc-api-interfaces      // 授权服务接口
├── limbo-authc-core                    // 授权配置管理接口，Service与DAO层
└── limbo-authc-session                 // 权限管理后台的分布式会话管理
```  

## 概念
权限管理系统中常见的概念。

- **API** :   
  由请求method和uri组成一个API；因在RESTful接口中，往往一个uri在不同的请求method下代表的接口不同；
    
- **权限** :   
  我们将权限定义为"用户完成一个最小操作所需的API集合"，如完成"新增部门"操作需要调用"GET /account"和"POST /department"两个接口，则将"新增部门"权限的API设置为以上两个，拦截器中在调用接口之前，判断是否有对应API的权限；
  
    对于不想进行API级别权限控制的应用来说，可以将权限定义为"页面中用户可以操作的元素"，如"部门管理"菜单中有有"新增部门"按钮，可以根据是否有该权限，来决定是否渲染"新增部门"按钮；
    同样，你可以同时使用以上两种定义，这将会更安全的保护接口；
    
- **菜单** :   
  菜单即普遍意义上的"菜单"，用户在业务系统页面上看到的菜单；如果不想使用这个概念，可以将菜单定义为"权限的集合"；
  
- **角色** :   
  角色是"菜单"的集合，同时角色可以额外配置一些"权限策略"，在除了菜单拥有的权限之外，可以设置角色是否拥有或排除(根据策略)单独的某个(某些)权限；"权限策略"中权限的优先级高于角色拥有的菜单；
    
   > 如菜单"部门管理"中有"部门新增"和"部门查看"权限，角色"人事文员"中有"部门管理"菜单，若给角色"人事文员"配置权限策略"部门新增 拒绝"，则该角色实际上将不会拥有"部门新增"的权限，只有"部门查看"权限；

- **账户** :   
  即业务系统的账户，放在授权服务中管理；账户应该由业务系统调用授权服务接口来创建，权限管理后台无法直接创建业务系统账户，这样便于业务系统在创建账户后初始化一些特有的业务数据；授权服务提供了账户的登录认证操作，但只接收用户名密码参数，业务系统可以在登录认证前添加验证码、滑动验证、短信验证、使用邮箱登录、使用手机登录等操作，或者业务系统希望自行实现登录认证，不适用授权服务提供的接口，也是可以的；

- **项目** :   
  项目代表了全部在授权服务中管理权限配置的业务系统，必须在创建了项目之后，业务系统才能正确调用授权服务的接口；  
  

# 使用说明

 1. 在权限管理后台创建项目

![创建项目](https://github.com/LimboHome/limbo-authc/raw/master/doc/project.jpeg)

 2. 在权限管理后台创建项目的管理员账户，并将项目授权给管理员账户

![账户管理](https://github.com/LimboHome/limbo-authc/raw/master/doc/account.jpeg)

 3. 以项目管理员账户登陆权限管理后台，配置项目的权限、菜单、角色

![权限](https://github.com/LimboHome/limbo-authc/raw/master/doc/permission.jpeg)

![菜单](https://github.com/LimboHome/limbo-authc/raw/master/doc/menu.jpeg)

![菜单](https://github.com/LimboHome/limbo-authc/raw/master/doc/role.jpeg)

 4. 启动业务系统，依赖授权服务，进行账户创建

 5. 业务系统自动为账户授权，或者登陆权限管理后台进行账户的授权

 6. 第三方系统，只需要如下自定义拦截器，自行判断是否有权限访问接口(此处可接入授权认证框架Shiro等)：
 
 6.1 使用limbo-authc的消费者filter（dubbo filter内容详见http://dubbo.apache.org/zh-cn/docs/dev/impls/filter.html）
 
```
新建目录
resources/META-INF/dubbo/org.apache.dubbo.rpc.Filter
在文件中添加如下内容
consumerAuthcFilter=org.limbo.authc.api.interfaces.support.dubbo.ConsumerAthcContextFilter
```
 6.2 配置limbo-authc调用者 并指定filter
```java
@Service
public class AuthorizationDubboConsumer {

    @Delegate
    @Reference(version = "${dubbo.service.version}", filter = {"consumerAuthcFilter"})
    private AuthorizationApi authorizationApi;

}
```
 6.3 配置limbo-authc权限拦截器
```java
public class AuthcInterceptor implements HandlerInterceptor {

    @Autowired
    private AuthorizationApi authorizationApi;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
      Objects.requireNonNull(authorizationApi);
     
      Long accountId = null; // 业务代码去获取limbo-authc的accountId
     
      AuthorizationVO.PermissionCheckParam param = new AuthorizationVO.PermissionCheckParam();
      param.setAccountId(accountId);
      param.setHttpMethod(request.getMethod());
      param.setPath(request.getServletPath());
 
      Response<Boolean> resp = authorizationApi.hasPermission(param);
 
      if (resp.ok() && resp.getData()) {
          return true;
      }
 
      throw new AuthcException();
    }

}
```
 6.4 配置WebConfig 声明 SpringBeanContext 和自定义的拦截器
```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    // SpringBeanContext 用于dubbo拦截器获取spring配置信息
    @Bean
    public SpringBeanContext SpringBeanContext(ApplicationContext applicationContext, Environment environment) {
        SpringBeanContext springBeanContext = new SpringBeanContext();
        springBeanContext.setApplicationContext(applicationContext);
        springBeanContext.setEnvironment(environment);
        return springBeanContext;
    }
    
    // 自定义权限拦截器
    @Bean
    public AuthcInterceptor authcInterceptor() {
        return new AuthcInterceptor();
    }
    
    // 配置权限拦截器需要拦截的请求
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authcInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/ping/**");
    }
}
```
   

# 设计思想
  - 约定  
    项目中有一些"约定"的内容，希望在搭建时不要修改；如果有修改，应确保全局修改；
    - 关于管理后台admin的约定
      1. admin项目的projectId为10000000；
      2. admin项目的secret为BEC00CF81411430FAC6EBDE343AADA86；
      3. admin项目的超级管理员账户名为`admin`、密码为`limbo-authc`，可在项目搭建完成后登陆管理后台修改；
    - 关于授权服务的约定
      1. 每个应用在调用授权服务时，均需要附加隐式参数：项目的projectId、项目的projectCode、项目的secret；其中，projectId和projectCode可以二选一；
      2. 对于管理后台admin，调用授权服务接口时，除1中的三个隐式参数外，还应附加api认证隐式参数，参数值初始为 123567890；
      > 隐式参数名参考org.limbo.authc.api.interfaces.constants.DubboContants.Attachments
  
  - 最小权限  
    权限应该是用户操作的最小单位，在设置权限对应的api时，应当仅考虑该权限所需的api，尽量少使用Ant通配符，尽量指定调用method，防止出现权限范围过大。  
    
    如一个部门管理的页面，页面中仅有部门的CRUD操作，有查询按钮、新增按钮、修改按钮、删除按钮，建议对应四个权限。"查询部门"权限应仅包含查询列表所需的接口，在RESTful风格的接口中，应该指定api仅能通过get方式调用。


# 安装教程

1. 准备所需资源  
  以下资源需预先部署完成，具体如何部署此处不赘述，可参考网络教程；
   1. MySQL数据库 * 2；(如果将Authc Admin与Authc Server的表数据放在一起，那么只需要一个数据库)；
   2. Redis * 1；
   3. Nacos 或 ZooKeeper，用于注册中心；
  
2. 初始化数据库  
   数据库有两个，假定名称为authc_admin_db和authc_db，分表用于Authc Admin服务和Authc Service；如果您只使用一个数据库，可以不做区分；
   1. 在authc_admin_db中依次执行`init/0_初始化DB表.sql`、`init/1_初始化DB数据.sql`；
   2. 在authc_db中依次执行`init/2_初始化Admin项目DB表.sql`、`init/3_初始化Admin项目DB数据.sql`；

mvn clean package -Dmaven.test.skip=true

3. 部署Authc Service  
   - 进入`limbo-authc-api/limbo-authc-api-dubbo`模块，修改`application.yaml`中的配置，修改authc_db数据库连接和Nacos(或zk)注册中心地址；
   > 项目中使用Nacos作为配置中心，application.yaml没有包含全部配置信息，全部配置参考sample.yaml  
   
   > 项目中使用了`jasypt-spring-boot-starter`对配置信息进行了加密，关于jasypt的使用请自行搜索文章学习
   
   - 回到项目根目录，执行maven打包，得到FatJar ；  
     如果只打算在`limbo-authc-api-dubbo`子模块执行打包，需要先行将`limbo-authc-api-interfaces`、`limbo-authc-core` install到本地仓库；
     
   - 启动Authc Service  
     `java -jar limbo-authc-api-dubbo-1.0-SNAPSHOT.jar`
  
4. 部署Authc Admin Service
   - 进入`limbo-authc-admin`模块，修改`application.yaml`配置，同样可参考`sample.yaml`；
   - 回到项目根目录，执行maven打包，得到FatJar；
   - 启动Authc Admin Service；  
     启动服务时可通过jvm参数指定web服务端口`-Dserver.port=8890`；

5. 部署Authc Admin前端
   Authc Admin前端项目基于Vue2.0开发，可以使用三种方式部署  
   - 编译完成后使用Nginx静态文件目录；  
     直接打包完丢到静态文件下，由Nginx映射一下就完事了，配置类似这样：
      ```nginx
      server {
          listen 80;
          server_name authc.limbo.org;
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
   
   - 基于node服务启动；
     如何启动前端服务，目前通过`npm run server`启动，其他启动方式待完善...
     ```nginx
     server {
         listen 80;
         server_name authc.limbo.org;
         
         location ~* ^.+\.(ttf|eot|woff|woff2|jpg|jpeg|gif|png|svg|ico|html|lasso|pl|txt|fla|swf|zip|wav|json|js|js.map|css|css.map|less)$ {
             # 前端跳转
             proxy_pass http://127.0.0.1:8080;
         }

         location /api/ {
             # 后端接口
             proxy_pass http://127.0.0.1:8890/;
         }
     }
     ```
   
   - 编译完成后放到`limbo-authc-admin`模块的WEB-INF目录下，随Authc Admin Service启动；
     需要在执行Authc Admin Service 打包之前在`limbo-authc-admin/front-end`目录下执行`compile.sh`脚本，执行完后会在`limbo-authc-admin/src/main/webapp`目录下生成编译后的前端文件；然后需要修改`limbo-authc-admin`模块中的全部接口加上路径前缀`/api`，拦截器也要做响应修改，只拦截`/api`路径下请求；
     ```nginx
     server {
         listen 80;
         server_name authc.limbo.org;

         location / {
             proxy_pass http://127.0.0.1:8890/;
         }
     }
     ```
     > 注意：这种方式要改动过多，而且我们没有测试过，不保证按教程部署后一定能用！推荐使用前两种方式。

