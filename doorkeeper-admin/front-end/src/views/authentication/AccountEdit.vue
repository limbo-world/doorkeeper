<template>
    <el-container>
        <el-main>
            <el-form :model="account" label-width="120px" size="mini" class="edit-form"
                     :rules="rules" ref="editForm">
                <el-form-item label="登录用户名" prop="username">
                    <el-input v-model="account.username" :maxlength="50"></el-input>
                </el-form-item>
                <el-form-item label="登录密码" prop="password">
                    <el-input v-model="account.password" :maxlength="50" type="password"></el-input>
                </el-form-item>
                <el-form-item label="确认登录密码" prop="confirmPassword">
                    <el-input v-model="account.confirmPassword" :maxlength="50" type="password"></el-input>
                </el-form-item>
                <el-form-item label="昵称" prop="nick">
                    <el-input v-model="account.nick" :maxlength="32"></el-input>
                </el-form-item>
            </el-form>
        </el-main>
    </el-container>
</template>


<script>

    import Rules from '../../utils/ValidateRules';

    export default {
        props: {
            account: {
                type: Object,
                default() {
                    return {
                        accountId: null,
                        username: '',
                        password: '',
                        confirmPassword: '',
                    }
                }
            }
        },

        data() {
            const confirmPassword = (rule, value, cb) => {
                if (this.account.password !== value) {
                    cb(new Error('两次输入密码不一致'));
                } else {
                    cb();
                }
            };

            return {
                rules: {
                    username: [Rules.required('登录用户名'), Rules.length(3, 32), ],
                    password: [ Rules.required('登录密码'), Rules.length(6, 32), ],
                    confirmPassword: [
                        Rules.required('确认密码'),
                        Rules.length(6, 32),
                        {
                            validator: confirmPassword,
                            trigger: 'blur'
                        }
                    ],
                    nick: [ Rules.required('昵称'), Rules.length(2, 16) ]
                }
            };
        },

        created() {
            pages.accountEdit = this;
        },

        methods: {

            clearData() {
                if (this.$refs.editForm) {
                    this.$refs.editForm.clearValidate();
                }
            },

            saveAccount() {
                const loading = this.$loading();
                return new Promise((resolve, reject) => {
                    this.$refs.editForm.validate(valid => {
                        if (!valid) {
                            reject();
                            return;
                        }
                        const account = this.account;
                        const prom = this.doAddAccount(account);
                        prom.then(() => {
                            this.clearData();
                            resolve();
                        }).catch(reject);
                    });
                }).finally(() => loading.close())
            },

            doAddAccount(account) {
                return this.$ajax.post('/admin', account);
            },
        }
    }
</script>
