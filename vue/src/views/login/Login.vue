<template>

    <el-container class="login">
        <el-main>
            <el-row>
                <el-col :xs="1" :sm="4" :md="8" :lg="12" :xl="12" style="display: flex; justify-content: center;">
                    <img src="../../assets/images/logo.svg" style="width: 50%" />
                </el-col>
                <el-col :xs="22" :sm="16" :md="12" :lg="8" :xl="8">
                    <el-form :model="loginForm" :rules="loginFormRule" ref="loginForm" label-position="top"
                             class="shadow login-form">
                        <el-form-item label="登录名" prop="username">
                            <el-input v-model="loginForm.username" placeholder="请输入账号"></el-input>
                        </el-form-item>
                        <el-form-item label="密码" prop="password">
                            <el-input v-model="loginForm.password" type="password" placeholder="请输入密码"></el-input>
                        </el-form-item>
                        <el-form-item>
                            <el-button type="primary" class="login-btn" @click="login"
                                       :loading="loginProcessing" :disabled="loginProcessing">登录</el-button>
                        </el-form-item>
                    </el-form>
                </el-col>
                <el-col :xs="1" :sm="4" :md="4" :lg="4" :xl="4">
                    <div class="has-height"></div>
                </el-col>
            </el-row>
        </el-main>

    </el-container>

</template>


<script>
    import '@/assets/styles/reset.scss';
    import '@/libs/colorui/colorui.css';

    import { mapActions } from 'vuex';

    export default {
        data() {
            return {
                loginForm: {},
                loginFormRule: {
                    username: [
                        {required: true, message: '请填写账号', trigger: 'blur'},
                    ],
                    password: [
                        {required: true, message: '请填写密码', trigger: 'blur'},
                    ]
                },
                loginProcessing: false,
            }
        },

        /**
         * 前置路由守卫，在进入路由前，检测是否已经存在会话，存在则进入首页
         * 因为在路由守卫中无法取到vm实例，又不想直接引入全局vuex，这里就用了调用接口的方法去判断
         * 比较好的方法是通过session module的getter获取user，判断user存不存在
         */
        beforeRouteEnter(to, from, next) {
            window.$ajax.get('/session/user-info',  {
                ignoreException: { 401: true }
            }).then(response => {
                next('/home');
            }).catch(() => {
                next();
            })
        },

        created() {
            window.vue = this;
        },

        methods: {
            ...mapActions('session', ['loadMenus']),

            login() {
                this.loginProcessing = true;
                this.$refs.loginForm.validate(valid => {
                    if (!valid) {
                        this.loginProcessing = false;
                        return;
                    }

                    this.$store.dispatch('session/login', this.loginForm).then(() => {
                        this.loadMenus().then(menus => {
                            this.$router.push({
                                path: '/home',
                            });
                        }).catch(err => {
                            this.$message.error('加载菜单失败！' + err.msg);
                            console.error(err);
                        });
                    }).finally(() => this.loginProcessing = false);
                });
            }
        }
    }
</script>

<style lang="scss">
    .login.el-container {
        height: 100%;
        background-color: white;

        .el-main {
            padding: 0;
        }

        .el-row {
            height: 100%;
        }

        .el-col {
            height: 100%;
            display: flex;
            align-items: center;
        }

        .login-form {
            background: #fff;
            border-radius: 10px;
            box-shadow: 0 0 4px 0 rgba(238, 223, 223, .6), 0 0 20px 0 hsla(0, 0%, 88.2%, .6);
            padding: 50px 50px;
            width: 100%;
            .login-btn {
                width: 100%;
            }
            .register-link {
                position: absolute;
                right: 0;
                bottom: -30px;
                height: 20px;
                line-height: 20px;
            }
        }

        .bg-white {
            background: #ffffff;
            min-height: 36px;
        }

        #captchaImg {
            height: 36px;
            width: 100%;
            min-width: 180px;
            margin-top: 20px;
        }
    }
</style>
