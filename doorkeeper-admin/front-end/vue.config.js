const VueLoaderPlugin = require("vue-loader/lib/plugin");

module.exports = {
    lintOnSave: false,
    devServer: {
        port: 8080,
        disableHostCheck: true,
    },
    configureWebpack: {
        externals: {
            'vue': 'Vue',
            'vuex': 'Vuex',
            'vue-router': 'VueRouter',
            'vue-i18n': 'VueI18n',
            'element-ui': 'ELEMENT',
            'jsencrypt': 'JSEncrypt'
        },
        module: {
            rules: [{
                test: /\.vue$/,
                loader: "vue-loader"
            }]
        },
        plugins: [
            new VueLoaderPlugin()
        ]
    }
};
