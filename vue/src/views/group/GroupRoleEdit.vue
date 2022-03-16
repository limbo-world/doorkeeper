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
                <el-table-column>
                    <template slot="header" slot-scope="scope">
                        <span>是否延伸</span>
                        <el-tooltip class="item" effect="dark" content="开启的情况下，会把角色传递给下级用户组的用户" placement="top-start">
                            <i class="el-icon-question"/>
                        </el-tooltip>
                    </template>
                    <template slot-scope="scope">
                        <el-switch :value="scope.row.isExtend ? true : false"
                                   @change="v => {updateRole(v, scope.row.roleId)}"
                                   active-color="#13ce66"
                                   inactive-color="#ff4949"></el-switch>
                    </template>
                </el-table-column>
            </el-table>
        </el-main>

    </el-container>
</template>


<script>

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
                name: '',
                size: 1000,
            },

            roles: [],
            clients: [],
            groupRoles: [],
        }
    },

    computed: {
        ...mapState('sessionAggregate', ['user']),
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

        loadRoles(current) {
            if (1 === current) {
                this.resetPageForm();
            }
            this.startProgress();
            axios.all([
                this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/role`, {params: this.queryForm}),
                this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/group/${this.groupId}/role`)
            ]).then(axios.spread((rolesResponse, groupRolesResponse) => {
                const page = rolesResponse.data;
                this.queryForm.total = page.total >= 0 ? page.total : this.queryForm.total;
                let roles = page.data;
                let groupRoles = groupRolesResponse.data;
                for (let groupRole of groupRoles) {
                    for (let role of roles){
                        if (groupRole.roleId === role.roleId) {
                            role.groupId = groupRole.groupId;
                            role.groupRoleId = groupRole.groupRoleId;
                            role.isExtend = groupRole.isExtend;
                        }
                    }
                }
                this.groupRoles = groupRoles;
                this.roles = roles;
            })).finally(() => this.stopProgress());
        },

        bindRole(v, roleId) {
            const loading = this.$loading();
            if (v) { // 新增
                this.$ajax.post(`/admin/realm/${this.user.realm.realmId}/group/${this.groupId}/role/batch`, {
                    roles: [{roleId: roleId}], type: this.$constants.batchMethod.SAVE
                }).then(response => {
                    this.loadRoles();
                }).finally(() => loading.close());
            } else { // 删除
                this.$ajax.post(`/admin/realm/${this.user.realm.realmId}/group/${this.groupId}/role/batch`, {
                    roleIds: [roleId], type: this.$constants.batchMethod.DELETE
                }).then(response => {
                    this.loadRoles();
                }).finally(() => loading.close());
            }
        },

        updateRole(v, roleId) {
            const loading = this.$loading();
            this.$ajax.post(`/admin/realm/${this.user.realm.realmId}/group/${this.groupId}/role/batch`, {
                roles: [{roleId: roleId, isExtend: v}], type: this.$constants.batchMethod.UPDATE
            }).then(response => {
                this.loadRoles();
            }).finally(() => loading.close());
        },

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
