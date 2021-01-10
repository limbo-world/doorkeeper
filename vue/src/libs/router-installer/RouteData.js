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
                    },
                    {
                        path: '/client/client-edit',
                        component: _import('client/ClientEditLayout'),
                    },
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
                path: '/resource',
                component: RouterView,
                redirect: '/resource/resource',
                children: [
                    {
                        path: '/resource/resource',
                        component: _import('resource/Resource'),
                    },
                    {
                        path: '/resource/resource-edit',
                        component: _import('resource/ResourceEdit'),
                    },
                ]
            },
            {
                path: '/policy',
                component: RouterView,
                redirect: '/policy/policy',
                children: [
                    {
                        path: '/policy/policy',
                        component: _import('policy/Policy'),
                    },
                    {
                        path: '/policy/policy-edit',
                        component: _import('policy/PolicyEdit'),
                    },
                ]
            },
            {
                path: '/permission',
                component: RouterView,
                redirect: '/permission/permission',
                children: [
                    {
                        path: '/permission/permission',
                        component: _import('permission/Permission'),
                    },
                    {
                        path: '/permission/permission-edit',
                        component: _import('permission/PermissionEdit'),
                    },
                ]
            },
            {
                path: '/user',
                component: RouterView,
                redirect: '/user/user',
                children: [
                    {
                        path: '/user/user',
                        component: _import('user/User'),
                    },
                    {
                        path: '/user/user-edit',
                        component: _import('user/UserEdit'),
                    },
                ]
            },
        ]
    },

];
