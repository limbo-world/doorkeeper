import Responsive from './responsive';

const ResponsiveMixin = {};

export default {
    install(Vue) {
        this._responsive = new Responsive(Vue);
        this._responsive.setDefaultResponsiveName('pc');
        this._responsive.defineResponsive('mobile', 768);
        this._responsive.isSafari = /Safari/.test(navigator.userAgent) && !/Chrome/.test(navigator.userAgent);
        const that = this;

        Object.defineProperty(Vue.prototype, '$responsive', {
            get() {
                // 此处this指向Vue.prototype
                return that._responsive;
            }
        });
        Vue.mixin(ResponsiveMixin);
    }
}
