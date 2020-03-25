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
