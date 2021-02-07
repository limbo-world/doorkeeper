<!-- START doctoc generated TOC please keep comment here to allow auto update -->
<!-- DON'T EDIT THIS SECTION, INSTEAD RE-RUN doctoc TO UPDATE -->
**Table of Contents**  *generated with [DocToc](https://github.com/thlorenz/doctoc)*

- [vue-admin](#vue-admin)
  - [Project setup](#project-setup)
    - [Compiles and hot-reloads for development](#compiles-and-hot-reloads-for-development)
    - [Compiles and minifies for production](#compiles-and-minifies-for-production)
    - [Lints and fixes files](#lints-and-fixes-files)
    - [Customize configuration](#customize-configuration)
    - [nginx](#nginx)

<!-- END doctoc generated TOC please keep comment here to allow auto update -->

# vue-admin

## Project setup
```
npm install
```

### Compiles and hot-reloads for development
```
npm run serve
```

### Compiles and minifies for production
```
npm run build
```

### Lints and fixes files
```
npm run lint
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).


### nginx

```http request
server {
    listen       80;
    server_name authc.limbotest.com;

    location ~* ^.+\.(ttf|eot|woff|woff2|jpg|jpeg|gif|png|svg|ico|html|lasso|pl|txt|fla|swf|zip|wav|json|js|js.map|css|css.map|less)$ {
        # 前端跳转
        proxy_pass http://127.0.0.1:8080;
    }

    location /api/ {
        # 后端接口
        proxy_pass http://127.0.0.1:8995/;
    }

}
```
