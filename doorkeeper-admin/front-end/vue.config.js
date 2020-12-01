const VueLoaderPlugin = require("vue-loader/lib/plugin");

module.exports = {
    lintOnSave: false,
    devServer: {
        port: 8080,
        disableHostCheck: true,
    },
    configureWebpack: {
        resolve: {
            extensions: [".ts", ".tsx", ".js", ".json"]
        },
        externals: {
            'vue': 'Vue',
            'vuex': 'Vuex',
            'vue-router': 'VueRouter',
            'vue-i18n': 'VueI18n',
            'element-ui': 'ELEMENT',
            'jsencrypt': 'JSEncrypt'
        },
        module: {
            rules: [
                {
                    test: /\.vue$/,
                    loader: "vue-loader"
                },
                {
                    test: /\.ts?$/,
                    loader: 'ts-loader',
                    exclude: /node_modules/,
                    options: {
                        appendTsSuffixTo: [/\.js$/],
                    }
                }
            ]
        },
        plugins: [
            new VueLoaderPlugin()
        ]
    }
};
