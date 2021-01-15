import {Loading, Message, MessageBox} from 'element-ui';
import store from '../vuex-installer';

const getTokenCache = () => {
    return window.localCache.getSync('session/token');
};

const request = axios.create({
    baseURL: process.env.VUE_APP_baseUrl, // api的base_url 配置后，请求会拼接对应uri
    timeout: 120000 // request timeout
});

request.tokenHeader = process.env.VUE_APP_tokenHeaderName;
// request.signHeader = process.env.VUE_APP_signHeaderName;
// request.encrypt = new JSEncrypt();

// 请求拦截器，设置登录认证header
request.interceptors.request.use(config => {
    // 拼接时间戳，防止请求被缓存
    if (config.url.indexOf('?') !== -1) {
        config.url = config.url + '&t=' + Date.now();
    } else {
        config.url = config.url + '?t=' + Date.now();
    }

    // 设置认证header
    config.headers[request.tokenHeader] = getTokenCache();
    return config;
}, error => {
    Promise.reject(error);
});

// 响应拦截器
request.interceptors.response.use(response => {
    const config = response.config;
    const responseCode = response.data.code;
    if (responseCode !== 200) {
        // 如果配置了忽略特定错误码，则不处理为catch
        if (!config.ignoreException || !config.ignoreException[responseCode]) {
            Message.error(response.data.msg);

            // 如果错误信息为未认证，需要提示是否重新登录
            // warning 不能直接跳转到登录页，直接跳转会触发全局前置路由守卫和登录页前置路由守卫，不停地发送session确认请求
            if (responseCode === 401) {
                window.location.href = '#/login'
            }
        }
        // 交给catch处理
        return Promise.reject(response.data);
    }

    response.data.originResponse = response;
    return response.data
}, error => {
    // 对于网络错误，弹出提示
    MessageBox({
        message: error.message,
        type: 'error',
        duration: 5 * 1000
    });
    return Promise.reject(error)
});

const installer = {
    install: function (Vue) {
        Vue.prototype.$ajax = request;
        window.$ajax = request;
    }
};

export default installer;
export const http = request;
