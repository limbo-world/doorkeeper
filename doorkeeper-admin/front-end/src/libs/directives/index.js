import AnimateDirective from './animate/index';


export default {

    install: function (Vue) {

        // 基于animate.css的动画指令
        window.AnimateCss.config.scrollContainer = '#app';
        window.AnimateCss.config.scrollBottom = -100;
        Vue.directive('animate', AnimateDirective);

    }

}
