/*
    这里声明了一个Vue的插件，用于按需引入ElementUI中的组件
    就不用全部引入的方式了，组件在这里import并且在install方法中Vue.use之后才能在页面中使用，否则需要在页面中单独引入
    比如现在引入了Container，页面可以直接用el-container
    现在没有引入NavMenu，页面中如果要用就在这里引入一下，或者在页面中import
 */

import 'element-ui/lib/theme-chalk/index.css';
import {Aside, Container, Footer, Header, Input, Loading, Main, Message,} from 'element-ui';


const installer = {
    install: function (Vue) {
        // Container布局
        Vue.use(Container);
        Vue.use(Header);
        Vue.use(Aside);
        Vue.use(Main);
        Vue.use(Footer);

        // Loading
        Vue.use(Loading);
        Vue.prototype.$loading = Loading.service;

        // 消息提示，直接用Vue.use引入的话会自动弹出来
        // Vue.use(Message);
        Vue.component(Message.name, Message);
        Vue.prototype.$message = Message;


        // 表单组件
        Vue.use(Input);
    }
};

export default installer;
