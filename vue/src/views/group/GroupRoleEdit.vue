<template>
    <el-container class="group-role-page">
        <el-header class="padding-top-xs" height="50px">
            <el-form ref="searchForm" :inline="true" size="mini">
                <el-form-item label="归属">
                    <el-select v-model="queryForm.clientId" filterable>
                        <el-option v-for="item in clients" :key="item.clientId" :label="item.name"
                                   :value="item.clientId"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="名称">
                    <el-input v-model="queryForm.dimName" placeholder="输入名称"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadRoles" size="mini" icon="el-icon-search">查询</el-button>
                </el-form-item>
            </el-form>
        </el-header>

        <el-main>
            <el-table :data="roles" size="mini">
                <el-table-column prop="roleId" label="ID"></el-table-column>
                <el-table-column prop="name" label="名称"></el-table-column>
                <el-table-column prop="description" label="描述"></el-table-column>
                <el-table-column label="是否绑定">
                    <template slot-scope="scope">
                        <el-switch :value="scope.row.groupRoleId ? true : false"
                                   @change="v => {bindRole(v, scope.row.roleId)}"
                                   active-color="#13ce66"
                                   inactive-color="#ff4949"></el-switch>
                    </template>
                </el-table-column>
            </el-table>
        </el-main>

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
                clientId: null,
            },

            roles: [],
            clients: [],
        }
    },

    computed: {
        ...mapState('session', ['user']),
    },

    created() {
        pages.groupRoleEdit = this;
        this.loadClients();
    },

    methods: {
        ...mapActions('ui', ['startProgress', 'stopProgress']),

        loadClients() {
            this.startProgress();
            this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/client`).then(response => {
                let clients = [{clientId: 0, name: "域"}]
                if (response.data && response.data.length > 0) {
                    clients = clients.concat(response.data)
                }
                this.clients = clients;
            }).finally(() => this.stopProgress());
        },

        loadRoles() {
            this.startProgress();
            this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/group/${this.groupId}/group-role`, {
                params: this.queryForm
            }).then(response => {
                this.roles = response.data;
            }).finally(() => this.stopProgress());
        },

        bindRole(v, roleId) {
            const loading = this.$loading();
            this.$ajax.post(`/admin/realm/${this.user.realm.realmId}/group/${this.groupId}/group-role/batch`, {
                roleIds: [roleId], type: v ? this.$constants.batchMethod.SAVE : this.$constants.batchMethod.DELETE
            }).then(response => {
                this.loadRoles();
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
