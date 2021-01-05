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
            {
                path: '/client',
                component: RouterView,
                redirect: '/client/client',
                children: [
                    {
                        path: '/client/client',
                        component: _import('client/Client'),
                    }
                ]
            },
            {
                path: '/role',
                component: RouterView,
                redirect: '/role/role',
                children: [
                    {
                        path: '/role/role',
                        component: _import('role/Role'),
                    },
                    {
                        path: '/role/role-edit',
                        component: _import('role/RoleEditLayout'),
                    },
                ]
            },
            {
                path: '/account',
                component: RouterView,
                redirect: '/account/account',
                children: [
                    {
                        path: '/account/account',
                        component: _import('account/Account'),
                    },
                ]
            },
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
