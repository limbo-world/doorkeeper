import CN from '../../assets/i18n/zh-CN';
import US from '../../assets/i18n/en-US';

const installer = {
    install: function (Vue) {
        Vue.use(VueI18n);
    },

    createI18n: function () {
        return new VueI18n({
            locale: 'zh-CN',
            messages: {
                'zh-CN': CN,
                'en-US': US,
            }
        });
    }
};

export default installer;
