<template>
    <el-container class="user-role-page">
        <el-header class="padding-top-xs" height="50px">
            <el-form ref="searchForm" :inline="true" size="mini">
                <el-form-item label="名称">
                    <el-input v-model="queryForm.dimName" placeholder="输入名称"></el-input>
                </el-form-item>
                <el-form-item label="加入">
                    <el-select v-model="queryForm.isJoin" clearable>
                        <el-option :key="true" label="已加入" :value="true"></el-option>
                        <el-option :key="false" label="未加入" :value="false"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadGroupUsers(1)" size="mini" icon="el-icon-search">查询
                    </el-button>
                    <el-button type="primary" @click="usersJoin" size="mini" icon="el-icon-search">加入</el-button>
                    <el-button type="primary" @click="usersLeave" size="mini" icon="el-icon-search">移除</el-button>
                </el-form-item>
            </el-form>
        </el-header>

        <el-main>
            <el-table :data="users" size="mini" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="50"></el-table-column>
                <el-table-column prop="userId" label="用户ID"></el-table-column>
                <el-table-column prop="username" label="用户名"></el-table-column>
                <el-table-column prop="nickname" label="昵称"></el-table-column>
                <el-table-column prop="description" label="描述"></el-table-column>
                <el-table-column label="是否加入">
                    <template slot-scope="scope">
                        {{ scope.row.groupUserId ? '已加入' : '未加入' }}
                    </template>
                </el-table-column>
            </el-table>
        </el-main>

        <el-footer>
            <el-pagination background layout="prev, pager, next" :total="queryForm.total"
                           :current-page.sync="queryForm.current"
                           :page-size="queryForm.size" @current-change="loadGroupUsers">
            </el-pagination>
        </el-footer>

    </el-container>
</template>


<script>

import AppConstants from "@/utils/AppConstants";
import {mapState, mapActions} from 'vuex';

export default {
    props: {
        groupId: {
            type: Number,
            default: null
        }
    },

    data() {
        return {
            queryForm: {
                current: 1,
                size: 10,
                total: -1,
                isJoin: true
            },
            users: [],
            selectUsers: []
        }
    },

    computed: {
        ...mapState('session', ['user']),
    },

    created() {
        pages.groupUserEdit = this;
        this.loadGroupUsers();
    },

    methods: {
        ...mapActions('ui', ['startProgress', 'stopProgress']),

        resetPageForm() {
            this.queryForm.current = 1;
            this.queryForm.total = -1;
        },
        loadGroupUsers(current) {
            if (1 === current) {
                this.resetPageForm();
            }
            this.startProgress();
            this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/group/${this.groupId}/group-user`, {
                params: this.queryForm
            }).then(response => {
                const page = response.data;
                this.queryForm.total = page.total >= 0 ? page.total : this.queryForm.total;
                this.users = page.data;
            }).finally(() => this.stopProgress());
        },

        handleSelectionChange(val) {
            this.selectUsers = val;
        },
        usersJoin() {
            let userIds = [];
            for (let user of this.selectUsers) {
                if (!user.groupUserId) {
                    userIds.push(user.userId);
                }
            }
            const loading = this.$loading();
            this.$ajax.post(`/admin/realm/${this.user.realm.realmId}/group/${this.groupId}/group-user/batch`, {
                userIds: userIds, type: this.$constants.batchMethod.SAVE
            }).then(response => {
                this.loadGroupUsers();
            }).finally(() => loading.close());
        },

        usersLeave() {
            let userIds = [];
            for (let user of this.selectUsers) {
                userIds.push(user.userId);
            }
            const loading = this.$loading();
            this.$ajax.post(`/admin/realm/${this.user.realm.realmId}/group/${this.groupId}/group-user/batch`, {
                userIds: userIds, type: this.$constants.batchMethod.DELETE
            }).then(response => {
                this.loadGroupUsers();
            }).finally(() => loading.close());
        }
    }

}
</script>

<style lang="scss">
.project-page {
    .el-table {
        .cell {
            min-height: 22px;
        }
    }
}
</style>
