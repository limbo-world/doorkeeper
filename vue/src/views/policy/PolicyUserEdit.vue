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
    <el-form-item label="用户">
        <el-container class="policy-user-edit-page">
            <el-header height="30px">
                <el-form :inline="true" size="mini">
                    <el-form-item label="名称">
                        <el-input v-model="queryForm.dimName" placeholder="输入名称"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadUsers" size="mini" icon="el-icon-search">查询</el-button>
                    </el-form-item>
                </el-form>
            </el-header>
            <el-main>
                <el-transfer v-model="selectUserIds" :data="users" :titles="['未绑定', '已绑定']" @change="changeUser"></el-transfer>
            </el-main>
        </el-container>
    </el-form-item>
</template>


<script>

import {mapState, mapActions} from 'vuex';

export default {
    props: {
        policyId: {
            type: Number,
            default: null
        },
        clientId: {
            type: Number,
            default: 0
        },
        policyUsers: {
            type: Array,
            default: []
        }
    },

    data() {
        return {
            queryForm: {
                dimName: ''
            },
            users: [],
            selectUserIds: [],
        };
    },
    computed: {
        ...mapState('session', ['user']),
    },
    created() {
        pages.policyUserEdit = this;

        // 初始化数据
        this.policyUsers.forEach(policyUser => {
            policyUser.key = policyUser.userId;
            policyUser.label = policyUser.username;
        })
        this.concatUsers([], []);
    },
    methods: {
        loadUsers() {
            this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/user`, {params: this.queryForm}).then(response => {
                let users = response.data.data;
                let userIds = [];
                for (let user of users) {
                    user.key = user.userId;
                    user.label = user.username;
                    userIds.push(user.userId);
                }
                this.concatUsers(users, userIds)
            });
        },
        changeUser(userIds, d, mvUserIds) {
            if (d === 'right') {
                // 主要用于去除重复
                this.policyUsers = this.policyUsers.filter(policyUser => mvUserIds.indexOf(policyUser.userId) < 0)
                // 获取新增的role
                let users = this.users.filter(user => mvUserIds.indexOf(user.userId) >= 0)
                this.policyUsers = this.policyUsers.concat(users)
            } else {
                // 删除里面的
                this.policyUsers = this.policyUsers.filter(policyUser => mvUserIds.indexOf(policyUser.userId) < 0)
            }
            this.$emit('bind-policy-users', this.policyUsers)
        },
        concatUsers(users, userIds) {
            // 把不存在的加入进来
            let policyUsers = this.policyUsers.filter(policyUser => userIds.indexOf(policyUser.userId) < 0)
            users = users.concat(policyUsers);
            this.users = users;
            // 每次查询完，需要把已有的加入
            this.selectUserIds = this.policyUsers.map(policyUser => policyUser.userId)
        }
    }
}
</script>

<style lang="scss">
.policy-user-edit-page {
    .el-transfer {
        .el-transfer-panel {
            width: 350px;

            .el-transfer-panel__item {
                margin-left: 0;
                display: block !important;
            }
        }
    }
}
</style>
