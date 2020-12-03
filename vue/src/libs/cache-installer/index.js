import Cache from './cache';

// 初始化一个缓存
const GlobalCache = new Cache({
    name: 'hk-web'
});

// 定义全局缓存
window.localCache = new Cache({
    name: window.location.host + '-local',
    cacheHolder: window.localStorage
});
window.sessionCache = new Cache({
    name: window.location.host + '-session',
    cacheHolder: window.sessionStorage
});

// TODO 在这里定义默认的key提供者

export default {
    install: function (Vue) {
        Vue.prototype.$cache = GlobalCache;
    }
};
