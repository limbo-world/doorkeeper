import AnimateDirective from './animate/index';
import AuthDirective from './auth/index';


export default {

    install: function (Vue) {

        // 基于animate.css的动画指令
        window.AnimateCss.config.scrollContainer = '#app';
        window.AnimateCss.config.scrollBottom = -100;
        Vue.directive('animate', AnimateDirective);

        // 基于权限表达式的权限控制
        Vue.directive('auth', AuthDirective);

    }

}
