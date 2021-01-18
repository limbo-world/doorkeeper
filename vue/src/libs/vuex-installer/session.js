import {http} from '../axios-installer';
import TimeUnit from '../cache-installer/time-unit';
import {Message, MessageBox, Loading} from 'element-ui';
import {MenuRoute} from "../router-installer/MenuData";
import AuthExpressionEvaluator from "@/libs/directives/auth/AuthExpressionEvaluator.ts";

const setTokenCache = (token) => {
    window.localCache.set('session/token', token, 15, TimeUnit.Days);
};

const getTokenCache = () => {
    return window.localCache.getSync('session/token');
};

const setSessionUserCache = (user) => {
    window.localCache.set('session/user', user, 15, TimeUnit.Days);
};

const getSessionUserCache = () => {
    return window.localCache.getSync('session/user');
};

export default {
    namespaced: true,

    state: {
        // 用户信息
        user: null,

        realms: [],

        // 全部菜单
        menus: [],

        // 权限表达式计算器
        authExpEvaluator: null,
    },

    getters: {
        user(state) {
            // 获取user信息，如果state中不存在，会尝试从sessionCache中读取，如果sessionCache中也没有则返回null；
            if (!state.user) {
                return getSessionUserCache();
            }
            return state.user;
        }
    },

    mutations: {
        // 设置state中的菜单
        setUser: (state, user) => {
            state.user = user;
        },
        setRealms: (state, realms) => {
            state.realms = realms;
        },

        // 设置state中的菜单
        setMenu: (state, menus) => {
            state.menus = menus;
        },

        // 设置权限表达式计算器
        setAuthExpEvaluator: (state, evaluator) => {
            state.authExpEvaluator = evaluator;
        }
    },

    actions: {
        /**
         * 登录，登录成功后会返回授权信息，授权信息将设置到state和sessionCache
         */
        login({commit}, param) {
            return http.get('/login', {params: {...param, realmId: process.env.VUE_APP_realmId}}).then(response => {
                if (response.code !== 200) {
                    MessageBox({
                        message: response.msg,
                        type: 'error',
                        duration: 5 * 1000
                    });
                    return Promise.reject();
                }
                const token = response.data;
                setTokenCache(token);
                commit('setUser', {});
                setSessionUserCache({})
                return Promise.resolve();
            });
        },

        /**
         * 注销，退出登录
         */
        logout({commit}) {
            commit('setUser', null);
            window.localCache.remove('session/token');
            commit('setMenu', []);
            window.location.href = '#/login';
        },

        /**
         * 从后台读取会话信息，如果成功读取到会话，会更新到state和sessionCache
         */
        loadSession({state, commit}) {
            return http.get('/session/user-info', {
                ignoreException: {401: true}
            }).then(response => {
                // 有会话 设置到state中，并更新sessionCache
                const user = response.data;
                let localUser = state.user;
                if (localUser && localUser.realm) {
                    user.realm = localUser.realm;
                }

                // 如果本地有选中的realm则继续选中
                let sessionUserCache = getSessionUserCache();
                if (sessionUserCache && sessionUserCache.realm) {
                    user.realm = sessionUserCache.realm;
                }

                commit('setUser', user);
                setSessionUserCache(user)

                // 刷新会话信息
                http.get('/session/refresh', {
                    ignoreException: {401: true}
                }).then(response => {
                    const token = response.data;
                    setTokenCache(token);
                })
                return Promise.resolve();
            });
        },

        loadRealms({state, commit}) {
            return http.get(`/admin/realm`).then(response => {
                // 有会话 设置到state中，并更新sessionCache
                const realms = response.data;
                commit('setRealms', realms);
                return Promise.resolve();
            });
        },

        /**
         * 初始化权限校验计算器
         */
        initEvaluator({state, commit}) {
            if (state.authExpEvaluator) {
                return Promise.resolve(state.authExpEvaluator);
            }

            // 从后台异步获取权限
            return http.get('/session/grant-info').then(response => {
                // 根据后台返回的授权信息，生成计算器
                const grantInfo = response.data;
                let roleIds = [];
                if (grantInfo && grantInfo.roles) {
                    roleIds = grantInfo.roles.map(r => (r.roleId).toString());
                }
                let evaluator = new AuthExpressionEvaluator(roleIds);
                commit('setAuthExpEvaluator', evaluator);

                return Promise.resolve(evaluator);
            });
        },

        // 从后台或其他地方加载菜单信息
        loadMenus({state, commit, dispatch}) {
            // 已经加载完成时不再加载
            if (state.menus && state.menus.length > 0) {
                return Promise.resolve(state.menus);
            }
            return dispatch('initEvaluator').then(evaluator => {
                // 组装菜单树
                const menus = organizeMenu(evaluator, state.user);
                commit('setMenu', menus);

                return Promise.resolve(menus);
            })
        },

        /**
         * 切换当前选中的域
         */
        changeRealm({state, commit}, realm) {
            return new Promise((resolve, reject) => {
                let user = state.user;
                user.realm = realm;
                commit('setUser', user);
                setSessionUserCache(user)
                resolve();
            }).then(() => {
                window.location.href = "/";
            }).catch(reject => {
                console.log("realm切换失败", reject)
            })
        },

    }
}


const organizeMenu = (evaluator) => {
    const menus = [];
    // 遍历MenuRoute数组，计算auth表达式的值
    // 如果子菜单全部不展示，则父菜单也不展示
    MenuRoute.forEach(menu => {
        const m = organizeSingleMenu(menu, evaluator);
        if (m) {
            menus.push(m);
        }
    })
    return menus;
};

const organizeSingleMenu = (menu, evaluator) => {
    if (menu.auth && !evaluator.evaluate(menu.auth)) {
        return null;
    }

    const m = {...menu}
    if (menu.children && menu.children.length > 0) {
        m.children = [];
        for (let child of menu.children) {
            let cm = organizeSingleMenu(child, evaluator);
            if (cm) {
                m.children.push(cm);
            }
        }
        return m.children.length <= 0 ? null : m;
    } else {
        delete m.children;
        return m;
    }

}
