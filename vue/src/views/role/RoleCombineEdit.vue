<template>
    <el-container class="project-page">
        <el-header class="padding-top-xs" height="50px">
            <el-form ref="searchForm" :inline="true" size="mini">
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
                        <el-switch v-if="scope.row.roleId != roleId" :value="scope.row.roleCombineId ? true : false"
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
                name: '',
                size: 1000
            },

            roles: [],
            clients: [],
            roleCombines: [],

            dialogOpened: false,
            dialogProcessing: false
        }
    },

    computed: {
        ...mapState('session', ['user']),
    },

    created() {
        pages.roleCombine = this;
        this.loadRoles();
    },

    methods: {
        ...mapActions('ui', ['startProgress', 'stopProgress']),

        loadRoles(current) {
            if (1 === current) {
                this.resetPageForm();
            }
            this.startProgress();

            axios.all([
                this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/role`, {params: {...this.queryForm, clientId: this.clientId}}),
                this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/role/${this.roleId}/combine`)
            ]).then(axios.spread((rolesResponse, roleCombinesResponse) => {
                const page = rolesResponse.data;
                this.queryForm.total = page.total >= 0 ? page.total : this.queryForm.total;
                let roles = page.data;
                let roleCombines = roleCombinesResponse.data;
                for (let roleCombine of roleCombines) {
                    for (let role of roles){
                        if (roleCombine.roleId === role.roleId) {
                            role.parentId = roleCombine.parentId;
                            role.roleCombineId = roleCombine.roleCombineId;
                        }
                    }
                }
                this.roleCombines = roleCombines;
                this.roles = roles;
            })).finally(() => this.stopProgress());
        },

        bindRole(v, roleId) {
            const loading = this.$loading();
            this.$ajax.post(`/admin/realm/${this.user.realm.realmId}/role/${this.roleId}/combine/batch`, {
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
