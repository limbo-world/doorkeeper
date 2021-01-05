import {Loading, Message, MessageBox} from 'element-ui';
import store from '../vuex-installer';

const request = axios.create({
    baseURL: process.env.VUE_APP_baseUrl, // api的base_url 配置后，请求会拼接对应uri
    timeout: 120000 // request timeout
});

request.sessionHeader = process.env.VUE_APP_sessionHeaderName;
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

    // const user = store.getters['session/user'];
    // if (user && user.securityDigest) {
    //     // 设置会话header
    //     config.headers[request.sessionHeader] = user.sessionId;
    //     // 设置签名header
    //     request.encrypt.setPublicKey(user.securityDigest.publicKey);
    //     let signContent = "url=" + config.url.split("?")[0] + "&sessionId=" + user.sessionId + "&ts=" +Date.now();
    //     config.headers[request.signHeader] = request.encrypt.encrypt(signContent);
    // }

    // 设置认证header
    const user = store.getters['session/user'];
    if (user && user.sessionId) {
        // 设置会话header
        config.headers[request.sessionHeader] = user.sessionId;
    }
    // realm参数
    if (config.params && config.params.addRealmId) {
        config.params.realmId = user.realm.realmId;
    }
    if (config.data && config.data.addRealmId) {
        config.data.realmId = user.realm.realmId;
    }
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
