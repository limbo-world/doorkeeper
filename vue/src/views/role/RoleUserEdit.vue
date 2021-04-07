<template>
    <el-container class="project-page">
        <el-header class="padding-top-xs" height="50px">
            <el-form ref="searchForm" :inline="true" size="mini">
                <el-form-item label="名称">
                    <el-input v-model="queryForm.dimName" placeholder="输入名称"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadUsers(1)" size="mini" icon="el-icon-search">查询</el-button>
                    <el-button type="primary" @click="usersJoin" size="mini" icon="el-icon-search">加入</el-button>
                    <el-button type="primary" @click="usersLeave" size="mini" icon="el-icon-search">移除</el-button>
                </el-form-item>
            </el-form>
        </el-header>

        <el-main>
            <el-table :data="users" size="mini" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="50"></el-table-column>
                <el-table-column prop="userId" label="ID"></el-table-column>
                <el-table-column prop="username" label="用户名"></el-table-column>
                <el-table-column prop="nickname" label="昵称"></el-table-column>
                <el-table-column prop="description" label="描述"></el-table-column>
                <el-table-column label="是否绑定">
                    <template slot-scope="scope">
                        {{scope.row.userRoleId ? "已绑定" : "未绑定"}}
                    </template>
                </el-table-column>
            </el-table>
        </el-main>

        <el-footer>
            <el-pagination background layout="prev, pager, next" :total="queryForm.total"
                           :current-page.sync="queryForm.current"
                           :page-size="queryForm.size" @current-change="loadUsers">
            </el-pagination>
        </el-footer>

    </el-container>
</template>


<script>

import {mapState, mapActions} from 'vuex';

export default {
    props: {
        roleId: {
            type: Number,
            default: null
        },
        clientId: {
            type: Number,
            default: 0
        },
    },

    data() {
        return {
            queryForm: {
                current: 1,
                size: 10,
                total: -1
            },

            users: [],
            selectUsers: [],
            roleUsers: []
        }
    },

    computed: {
        ...mapState('session', ['user']),
    },

    created() {
        pages.roleUser = this;
        this.loadUsers();
    },

    methods: {
        ...mapActions('ui', ['startProgress', 'stopProgress']),

        resetPageForm() {
            this.queryForm.current = 1;
            this.queryForm.total = -1;
        },
        loadUsers(current) {
            if (1 === current) {
                this.resetPageForm();
            }
            this.startProgress();

            axios.all([
                this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/user`, {params: this.queryForm}),
                this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/role/${this.roleId}/user`)
            ]).then(axios.spread((usersResponse, roleUsersResponse) => {
                const page = usersResponse.data;
                this.queryForm.total = page.total >= 0 ? page.total : this.queryForm.total;
                let users = page.data;
                let roleUsers = roleUsersResponse.data;
                for (let roleUser of roleUsers) {
                    for (let user of users){
                        if (roleUser.userId === user.userId) {
                            user.roleId = roleUser.roleId;
                            user.userRoleId = roleUser.userRoleId;
                        }
                    }
                }
                this.roleUsers = roleUsers;
                this.users = users;
            })).finally(() => this.stopProgress());
        },

        handleSelectionChange(val) {
            this.selectUsers = val;
        },
        usersJoin() {
            let userIds = [];
            for (let user of this.selectUsers) {
                if (!user.userRoleId) {
                    userIds.push(user.userId);
                }
            }
            const loading = this.$loading();
            this.$ajax.post(`/admin/realm/${this.user.realm.realmId}/role/${this.roleId}/user/batch`, {
                userIds: userIds, type: this.$constants.batchMethod.SAVE
            }).then(response => {
                this.loadUsers();
            }).finally(() => loading.close());
        },

        usersLeave() {
            let userIds = [];
            for (let user of this.selectUsers) {
                userIds.push(user.userId);
            }
            const loading = this.$loading();
            this.$ajax.post(`/admin/realm/${this.user.realm.realmId}/role/${this.roleId}/user/batch`, {
                userIds: userIds, type: this.$constants.batchMethod.DELETE
            }).then(response => {
                this.loadUsers();
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
