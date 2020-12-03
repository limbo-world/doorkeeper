
Vue.config.productionTip = false;

// ElementUI按需引入
/*import ElementInstaller from './libs/element-installer/index';
Vue.use(ElementInstaller);*/
Vue.use(ELEMENT);

// Router路由信息引入
import RouterInstaller from './libs/router-installer';
Vue.use(RouterInstaller);
const router = RouterInstaller.createRouter();

// Vuex引入
import store from './libs/vuex-installer';

// I18N国际化
import I18nInstaller from './libs/i18n-installer';
Vue.use(I18nInstaller);
const i18n = I18nInstaller.createI18n();

// Axios请求
import AxiosInstaller from './libs/axios-installer';
Vue.use(AxiosInstaller);

// 响应式效果监听
import ResponsiveInstaller from './libs/responsive-installer';
Vue.use(ResponsiveInstaller);

// storage缓存
import CacheInstaller from './libs/cache-installer';
Vue.use(CacheInstaller);

// 自定义指令
import DirectivesInstaller from './libs/directives';
Vue.use(DirectivesInstaller);

// 通过$set变更data中变量的属性，达到强制刷新渲染的效果，性能不高，慎用
Vue.prototype.$refreshData = function(variable) {
    this.$delete(variable, '$refreshTicker');
    this.$set(variable, '$refreshTicker', Date.now());
};

// 立即执行的Primise
Vue.prototype.$immediate = function(fun) {
    return new Promise(resolve => {
        if (typeof fun === 'function') {
            fun();
        }
        resolve();
    });
};

// 引入单页面组件
import App from './App.vue';
new Vue({
    router,
    store,
    i18n,
    render: h => h(App),
}).$mount('#app');

console.info(process.env)
