
import { http } from '../axios-installer';
import TimeUnit from '../cache-installer/time-unit';
import { Message, MessageBox, Loading } from 'element-ui';
import {MenuRoute} from "../router-installer/MenuData";
import AuthExpressionEvaluator from "@/libs/directives/auth/AuthExpressionEvaluator.ts";

const setSessionUserCache = (user) => {
    window.localCache.set('session/user', user, 1, TimeUnit.Days);
};

export default {
    namespaced: true,

    state: {
        // 登录用户
        user: null,

        // 全部菜单
        menus: [],

        // 权限表达式计算器
        authExpEvaluator: null,
    },

    getters: {
        /**
         * 获取user信息，如果state中不存在，会尝试从sessionCache中读取，如果sessionCache中也没有则返回null；
         */
        user(state) {
            if (!state.user) {
                return window.localCache.getSync('session/user');
            }
            return state.user;
        }
    },

    mutations: {
        // 设置state中的菜单
        setUser: (state, user) => {
            state.user = user;
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
        login({ commit }, param) {
            return http.post('/login', param).then(response => {
                if (response.code !== 200) {
                    MessageBox({
                        message: response.msg,
                        type: 'error',
                        duration: 5 * 1000
                    });
                    return Promise.reject();
                }
                commit('setUser', response.data);
                setSessionUserCache(response.data);
                return Promise.resolve();
            });
        },

        /**
         * 注销，退出登录
         */
        logout({ commit }) {
            http.get('/session/logout').then(() => {
                commit('setUser', null);
                window.localCache.remove('session/user');
                commit('setMenu', []);
                window.location.href='#/login';
            });
        },

        /**
         * 从后台读取会话信息，如果成功读取到会话，会更新到state和sessionCache
         */
        loadSession({ commit }) {
            return http.get('/session', {
                ignoreException: { 401: true }
            }).then(response => {
                // 有会话 设置到state中，并更新sessionCache
                commit('setUser', response.data);
                setSessionUserCache(response.data);
                return Promise.resolve();
            });
        },

        /**
         * 初始化权限校验计算器
         */
        initEvaluator({ state, commit }) {
            if (state.authExpEvaluator) {
                return Promise.resolve(state.authExpEvaluator);
            }

            // 从后台异步获取权限
            return http.get('/session/grant-info').then(response => {
                // 根据后台返回的授权信息，生成计算器
                const grantInfo = response.data;
                const account = state.user.account;
                let evaluator = new AuthExpressionEvaluator(
                    grantInfo.roles.map(r => (r.roleId).toString()),
                    account.currentProject.isAdmin
                );
                commit('setAuthExpEvaluator', evaluator);

                return Promise.resolve(evaluator);
            });
        },

        // 从后台或其他地方加载菜单信息
        loadMenus({ state, commit, dispatch }) {
            // 已经加载完成时不再加载
            if (state.menus && state.menus.length > 0) {
                return Promise.resolve(state.menus);
            }
            return dispatch('initEvaluator').then(evaluator => {
                // 组装菜单树
                const menus = organizeMenu(evaluator, state.user.account);
                commit('setMenu', menus);

                return Promise.resolve(menus);
            })
        },

        /**
         * 切换当前选中的店铺
         */
        changeProject({ commit }, projectId) {
            return http.put(`/session/project/${projectId}`).then(response => {
                window.location.reload();
            });
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
    if (!evaluator.evaluate(menu.auth)) {
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
