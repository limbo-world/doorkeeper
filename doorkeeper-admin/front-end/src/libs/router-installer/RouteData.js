const _import = file => () => import('@/views/' + file + '.vue');
const Layout = _import('layout/Layout');
const RouterView = _import('layout/RouterView');

// 路由信息在此声明，应当包含所有的路由信息
export default [
    {
        path: '/',
        redirect: '/login'
    },

    // 登录
    {
        path: '/login',
        component: _import('login/Login')
    },

    {
        path: '/',
        component: Layout,
        redirect: '/home',
        children: [
            // 首页
            {
                path: '/home',
                component: _import('home/Home')
            },

            // 项目管理
            {
                path: '/project',
                component: RouterView,
                redirect: '/project/project',
                children: [
                    {
                        path: '/project/project',
                        component: _import('project/Project'),
                    }
                ]
            },

            // 认证管理
            {
                path: '/account',
                component: RouterView,
                redirect: '/account/account',
                children: [
                    {
                        path: '/account/account',
                        component: _import('account/AccountPack'),
                    },
                ]
            },

            // 授权管理
            {
                path: '/authorization',
                component: RouterView,
                redirect: '/authorization/permission',
                children: [
                    {
                        path: '/authorization/permission',
                        component: _import('authorization/Permission')
                    },
                    {
                        path: '/authorization/role',
                        component: _import('authorization/Role')
                    }
                ]
            },

            // 操作日志
            {
                path: '/log',
                component: RouterView,
                redirect: '/log/b-log',
                children: [
                    {
                        path: '/log/b-log',
                        component: _import('log/BLog')
                    }
                ]
            }
        ]
    },

];
