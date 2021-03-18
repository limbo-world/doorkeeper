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
    <el-container class="user-edit-page">
        <el-main>
            <el-form :model="user" label-width="120px" size="mini" class="edit-form" ref="editForm" :rules="rules">
                <el-form-item label="用户名">
                    <el-input v-model="user.username" :disabled="user.userId"></el-input>
                </el-form-item>
                <el-form-item label="昵称">
                    <el-input v-model="user.nickname"></el-input>
                </el-form-item>
                <el-form-item label="密码">
                    <el-input type="password" v-model="user.newPassword"></el-input>
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                    <el-input type="password" v-model="user.confirmPassword"></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input type="textarea" v-model="user.description"></el-input>
                </el-form-item>
                <el-form-item label="是否启用">
                    <el-switch v-model="user.isEnabled" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="confirmEdit" size="mini">保存</el-button>
                </el-form-item>
            </el-form>
        </el-main>
    </el-container>
</template>


<script>

import {mapState, mapActions} from 'vuex';

export default {
    data() {
        const confirmPassword = (rule, value, cb) => {
            if (this.user.newPassword !== value) {
                cb(new Error('两次输入密码不一致'));
            } else {
                cb();
            }
        };

        return {
            rules: {
                confirmPassword: [
                    {
                        validator: confirmPassword,
                        trigger: 'blur'
                    }
                ],
            },
            user: {
                newPassword: '',
                confirmPassword: ''
            },
        };
    },
    computed: {
        stateUser: mapState('session', ['user']).user,
    },
    created() {
        pages.userEdit = this;
        if (this.$route.query.userId) {
            this.user.userId = this.$route.query.userId;
            this.loadUser();
        }
    },

    methods: {
        ...mapActions('ui', ['startProgress', 'stopProgress']),

        loadUser() {
            this.startProgress({speed: 'fast'});
            this.$ajax.get(`/admin/realm/${this.stateUser.realm.realmId}/user/${this.user.userId}`).then(response => {
                this.user = response.data;
            }).finally(() => this.stopProgress());
        },

        confirmEdit() {
            let formValid = false;
            this.$refs.editForm.validate(valid => {
                formValid = valid
            });
            if (!formValid) {
                return
            }
            const loading = this.$loading();
            return new Promise((resolve, reject) => {
                if (!this.user.userId) {
                    this.addUser().then(response => {
                        this.user = response.data;
                        this.loadUser();
                        resolve();
                    }).catch(reject);
                } else {
                    this.updateUser().then(() => {
                        this.loadUser();
                        resolve();
                    }).catch(reject);
                }
            }).finally(() => loading.close())
        },
        addUser() {
            return this.$ajax.post(`/admin/realm/${this.stateUser.realm.realmId}/user`, this.user);
        },
        updateUser() {
            return this.$ajax.put(`/admin/realm/${this.stateUser.realm.realmId}/user/${this.user.userId}`, this.user);
        },
    }
}
</script>

<style lang="scss">
.resource-edit-page {
    .uri-row {
        margin-bottom: 10px;
    }

    .tag-lab {
        margin-right: 10px;
    }
}
</style>
