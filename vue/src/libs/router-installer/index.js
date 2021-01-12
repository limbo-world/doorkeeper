import Router from 'vue-router'
import Routes from './RouteData'
import store from "../vuex-installer";

// 创建一个全局唯一的Router实例
// 路由使用history模式后，在切换到子路由后，刷新页面会返回404
// 因为这时候uri是子路由，不是根路径 / ，就不会定向到打包后的index.html
// 因此也就无法初始化Vue，这时候需要添加一个nginx的配置，让所有的请求在nginx都转发到index.html
/*
  location / {
	    # 前端全部路由跳转到根目录，为了使history路由模式下，刷新不报404
	    proxy_pass http://127.0.0.1:8002/index.html;
	}

	# 最近router官方给出了一个解决方法
	location / {
    try_files $uri $uri/ /index.html;
  }
 */
const RouterInstance = new Router({
    mode: 'hash',
    routes: Routes
});


/*
 * 单页面情况下，两个路由跳转之后，scrollTop也被保留，导致可能到新的页面后没有在顶部，此处做记录
 */
const ScrollTopCache = {};
RouterInstance.beforeEach((to, from, next) => {
    // 初始化新路由的scrollTop
    if (ScrollTopCache[to.fullPath] == null) {
        ScrollTopCache[to.fullPath] = 0;
    }
    // 记录上一个路由的scrollTop
    if (from) {
        // warn 这里的document.getElementById('app')不能缓存，缓存下来的取scrollTop有误
        const $app = document.getElementById('app');
        ScrollTopCache[from.fullPath] = $app.scrollTop;
    }

    // 执行会话检查，如果是不需要登录的应用，这一行注释，直接执行next()
    checkSession(to, from, next)
});
RouterInstance.afterEach((to) => {
    // 进入新路由之后，要滚动到记录的位置，需要在页面渲染完成后滚动，这里先不优雅的在100毫秒后执行
    setTimeout(() => {
        const $app = document.getElementById('app');
        $app.scrollTo(0, ScrollTopCache[to.fullPath]);
    }, 100);
});

const RouterInstaller = {
    install: function (Vue) {
        Vue.use(Router);

        // FIX vue-router3.0以上版本，跳转到现在已经在的路由会报错
        const originalPush = Router.prototype.push;
        Router.prototype.push = function(to) {
            return originalPush.call(this, to).catch(err => err);
        }
    },

    createRouter: function () {
        return RouterInstance;
    }
};

// 检查会话，会话不存在应跳转到登录页
const checkSession = (to, from, next) => {
    // 加载会话
    store.dispatch('session/loadSession').then(() => {
        // 加载用户拥有的域
        store.dispatch('session/loadRealms').then(() => {
            const realm = store.state.session.realm
            const realms = store.state.session.realms
            let needChange = true;
            // 设置当前选中的域 如果已经有选了则不需要切换了
            if (realm) {
                needChange = false;
                // 如果已选的不在列表里面 也需要切换
                let realmIds = realms.map(realm => realm.realmId);
                if (realmIds.indexOf(realm.realmId) < 0) {
                    needChange = true;
                }
            }
            if (needChange) {
                store.dispatch('session/changeRealm', realms[0], false).then(() => {
                    next();
                }).catch(reject => {
                    console.log("realm切换失败", reject)
                })
            } else {
                next();
            }
        }).catch(reject => {
            console.log("realms加载失败", reject)
        })
    }).catch(() => {
        if (to.path === '/login') {
            next();
        } else {
            // 获取会话失败，应该是未登录导致，需要到登录页去
            // FIXME 后面可以考虑加子错误码，根据子错误码类型来决定是继续向下还是跳到登录页
            next({
                path: '/login'
            });
        }
    });
};

export default RouterInstaller;
