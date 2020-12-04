<!--
  - Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
  -
  - Licensed under the Apache License, Version 2.0 (the "License");
  - you may not use this file except in compliance with the License.
  - You may obtain a copy of the License at
  -
  - 	http://www.apache.org/licenses/LICENSE-2.0
  -
  - Unless required by applicable law or agreed to in writing, software
  - distributed under the License is distributed on an "AS IS" BASIS,
  - WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  - See the License for the specific language governing permissions and
  - limitations under the License.
  -->

<template>
    <el-container>
        <el-main>
            <el-form :model="account" label-width="120px" size="mini" class="edit-form"
                     :rules="rules" ref="editForm">
                <el-form-item label="账号" prop="username" v-if="'新增' === openMode">
                    <el-input v-model="account.username"></el-input>
                </el-form-item>
                <el-form-item label="密码" prop="password" v-if="'新增' === openMode">
                    <el-input v-model="account.password" type="password"></el-input>
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword" v-if="'新增' === openMode">
                    <el-input v-model="account.confirmPassword" type="password"></el-input>
                </el-form-item>
                <el-form-item label="昵称" prop="nickname">
                    <el-input v-model="account.nickname"></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input type="textarea" v-model="account.accountDescribe"></el-input>
                </el-form-item>
                <el-form-item label="管理员">
                    <el-switch v-model="account.isAdmin" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
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
                default() {}
            },
            project: {
                type: Object,
                default() {}
            },
            openMode: {
                type: Boolean,
                default: true,
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
                    nickname: [ Rules.required('昵称'), Rules.length(2, 32) ]
                }
            };
        },

        created() {
            pages.projectAccountEdit = this;
        },

        methods: {

            confirmEdit() {
                const loading = this.$loading();
                return new Promise((resolve, reject) => {
                    this.$refs.editForm.validate(valid => {
                        if (!valid) {
                            reject();
                            return;
                        }
                        if ('新增' === this.openMode) {
                            this.doAddAccount(this.account).then(() => {
                                this.clearData();
                                resolve();
                            }).catch(reject);
                        } else if ('修改' === this.openMode) {
                            this.doUpdateAccount(this.account).then(() => {
                                this.clearData();
                                resolve();
                            }).catch(reject);
                        } else {
                            reject();
                        }
                    });
                }).finally(() => loading.close())
            },

            doAddAccount(account) {
                account.projectId = this.project.projectId;
                return this.$ajax.post('/project-account', account);
            },

            doUpdateAccount(account) {
                account.projectId = this.project.projectId;
                return this.$ajax.put(`/project-account/${account.accountId}`, account);
            },

            clearData() {
                if (this.$refs.editForm) {
                    this.$refs.editForm.clearValidate();
                }
            }
        }
    }
</script>
