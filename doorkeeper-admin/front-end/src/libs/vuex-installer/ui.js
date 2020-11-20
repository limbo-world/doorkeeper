import { http } from '../axios-installer/index';
import { MenuRoute } from '../router-installer/MenuData';

/**
 * 进度条自动增长速度
 */
const ProgressGrowSpeed = {
    slow: 2,
    fast: 10,
};

const createProgressGrowContext = option => {
    return {
        // 进度条自动增长任务ID
        progressAutoGrowTaskId: null,
        // 自增长进度从多少开始
        from: option && option.from ? option.from : 20,
        // 自增长到多少进度时停止，停止后必须手动结束进度条，才能达到100%，
        // 如果该值设置为>100，则自增长到100，并且在达到100%时隐藏进度条
        to: option && option.to != null && option.to > 0 ? option.to : 90,
        // 自增长速度
        speed: option && ProgressGrowSpeed[option.speed] ? option.speed : 'fast',
    };
};

export default {
    namespaced: true,

    state: {

        // 已选择的菜单
        selectedMenu: null,

        // 菜单是否展示
        isMenuHidden: false,

        // 面包屑结构，数组内元素会直接作为$router.push()方法的参数
        breadcrumbs: [],

        // 进度条是否可见
        progressVisible: false,

        // 进度条进度
        progress: 0,

        // 进度条自增长任务上下文
        progressContext: createProgressGrowContext(),
    },

    mutations: {

        // 设置选中菜单，同时更新面包屑结构
        setSelectedMenu: (state, param) => {
            state.selectedMenu = param.menu;

            // 计算菜单结构，赋值给面包屑结构
            const breadcrumbs = [];
            const recursion = ms => {
                for (const m of ms) {
                    breadcrumbs.push(m);

                    if (m.route === param.menu.route) {
                        return true;
                    } else if (m.children && m.children.length > 0 && recursion(m.children)) {
                        return true;
                    }
                    breadcrumbs.pop();
                }
                return false;
            };
            recursion(param.menus);

            // 将菜单对象转换为路由对象
            state.breadcrumbs = breadcrumbs.map(m => {
                return {
                    path: m.route,
                    name: m.menuName,
                }
            });
        },

        // 切换菜单展示状态
        toggleMenu: state => {
            state.isMenuHidden = !state.isMenuHidden;
        },

        /**
         * 设置面包屑结构
         * 一般不需要手动设置，在设置选中菜单的时候会自动生成面包屑结构
         * 除非是自己想要设置的面包屑中存在不在菜单中的路由
         */
        setBreadcrumbs: (state, breadcrumbs) => { state.breadcrumbs = breadcrumbs; },

        setProgress(state, progress) { state.progress = progress; },

        setProgressVisible(state, progressVisible) { state.progressVisible = progressVisible; },

        setProgressContext(state, context) { state.progressContext = context; },

        resetProgress(state, from) {
            state.progressVisible = true;
            state.progress = from;
        },

        clearProgressTask(state) {
            if (state.progressContext.progressAutoGrowTaskId) {
                clearInterval(state.progressContext.progressAutoGrowTaskId);
                state.progressContext.progressAutoGrowTaskId = null;
            }
        }
    },

    actions: {

        /**
         * 启动一次进度条展示，可以设置自动增长到多少，增长速度
         * @param option
         *      {
         *          to: 90,        // 自增长到多少进度停止，停止后将
         *          speed: 'slow', // 增长速度，取值有 slow\fast
         *      }
         */
        startProgress({ dispatch, commit, state }, option) {
            const ctx = createProgressGrowContext(option);

            // 先取消上一次的自动增长任务，如果存在
            commit('clearProgressTask');

            // 设置当前进度为0，设置进度条可见
            commit('resetProgress', ctx.from);

            // 启动新的自增长任务
            ctx.progressAutoGrowTaskId = setInterval(() => {
                if (state.progressContext.progressAutoGrowTaskId !== ctx.progressAutoGrowTaskId) {
                    return;
                }

                let newProgress = state.progress + ProgressGrowSpeed[ctx.speed];
                if (newProgress >= ctx.to || newProgress >= 100) {
                    newProgress = ctx.to;
                }

                // 如果已经达到100%，停止进度条
                if(newProgress >= 100) {
                    commit('setProgress', 100);
                    commit('clearProgressTask');
                    setTimeout(() => {
                        dispatch('hideProgress');
                    }, 300);
                } else {
                    commit('setProgress', newProgress);
                }
            }, 50);
            commit('setProgressContext', ctx);
        },

        /**
         * 手动停止进度条展示，会将进度设置为100，然后隐藏进度条
         */
        stopProgress({ dispatch, commit }) {
            commit('clearProgressTask');
            commit('setProgress', 100);
            setTimeout(() => {
                dispatch('hideProgress');
            }, 300);
        },

        hideProgress({ commit }) {
            commit('setProgressVisible', false);
            setTimeout(() => {
                commit('setProgress', 0);
            }, 300);
        }
    }
}
