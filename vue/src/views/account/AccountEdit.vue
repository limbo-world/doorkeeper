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
            <el-form :model="account" label-width="120px" size="mini" class="edit-form" ref="editForm">
                <el-form-item label="账号" prop="username" v-if="'新增' === openMode"
                              :rules="{required: true, message: '请填写账号', trigger: 'blur'}">
                    <el-input v-model="account.username"></el-input>
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


    export default {
        props: {
            account: {
                type: Object,
                default() {}
            },
            openMode: {
                type: Boolean,
                default: true,
            }
        },

        data() {
            return {
            };
        },

        created() {
            pages.adminEdit = this;
        },

        methods: {

            clearData() {
                if (this.$refs.editForm) {
                    this.$refs.editForm.clearValidate();
                }
            },
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
                return this.$ajax.post('/account', account);
            },

            doUpdateAccount(account) {
                return this.$ajax.put(`/account/${account.accountId}`, account);
            },

            clearData() {
                if (this.$refs.editForm) {
                    this.$refs.editForm.clearValidate();
                }
            }
        }
    }
</script>
