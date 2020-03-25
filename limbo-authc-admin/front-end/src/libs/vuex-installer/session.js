
import { http } from '../axios-installer/index';
import TimeUnit from '../cache-installer/time-unit';
import { Message, MessageBox, Loading } from 'element-ui';
import {MenuRoute} from "../router-installer/MenuData";

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
                }
                commit('setUser', response.data);
                setSessionUserCache(response.data);
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
            });
        },

        // 从后台或其他地方加载菜单信息
        loadMenus({ state, commit }) {
            // 已经加载完成时不再加载
            if (state.menus && state.menus.length > 0) {
                return new Promise(resolve => resolve(state.menus));
            }

            // 从后台异步获取菜单
            return new Promise((resolve, reject) => {
                http.get('/session/menus').then(response => {
                    const menuArr = response.data;
                    const menus = organizeMenu(menuArr);
                    commit('setMenu', menus);
                    resolve(menus);
                }).catch(reject);
            });
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



const organizeMenu = menus => {
    const parent = menus.filter(m => m.parentMenuCode == null);
    parent.forEach(m => {
        m.children = [];
        m.route = MenuRoute[m.menuCode];
    });

    menus.forEach(m => {
        if (m.parentMenuCode == null) {
            return;
        }

        const p = parent.find(p => p.menuCode === m.parentMenuCode);
        p.children.push(m);
        m.route = MenuRoute[m.menuCode];
    });
    return parent;
};
