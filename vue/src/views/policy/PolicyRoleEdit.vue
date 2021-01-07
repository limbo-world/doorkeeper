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
    <el-form-item label="角色" prop="name">
        <el-container class="policy-role-edit-page">
            <el-header height="30px">
                <el-form ref="searchForm" :inline="true" size="mini">
                    <el-form-item label="委托方">
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

            <el-table :data="roles" size="mini">
                <el-table-column prop="roleId" label="ID"></el-table-column>
                <el-table-column prop="name" label="名称"></el-table-column>
                <el-table-column prop="description" label="描述"></el-table-column>
                <el-table-column label="是否绑定">
                    <template slot-scope="scope">
                        <el-switch :value="scope.row.bind ? true : false"
                                   @change="v => {bindRole(v, scope.row.roleId)}"
                                   active-color="#13ce66"
                                   inactive-color="#ff4949"></el-switch>
                    </template>
                </el-table-column>
            </el-table>

        </el-container>
    </el-form-item>
</template>


<script>

export default {
    props: {
        policyId: {
            type: Number,
            default: null
        },
        policyRoles: {
            type: Array,
            default: []
        }
    },

    data() {
        return {
            queryForm: {
                name: '',
            },
            clients: [],
            roles: [],
        };
    },

    created() {
        pages.policyRoleEdit = this;
        this.loadClients();
    },

    methods: {
        loadClients() {
            this.$ajax.get('/admin/client', {params: {addRealmId: true}}).then(response => {
                let clients = [{clientId: 0, name: "域"}]
                if (response.data && response.data.length > 0) {
                    clients = clients.concat(response.data)
                }
                this.clients = clients;
            });
        },

        loadRoles() {
            this.$ajax.get('/admin/role', {
                params: {
                    ...this.queryForm, addRealmId: true
                }
            }).then(response => {
                this.roles = response.data;
                this.roleBindShow()
            });
        },
        bindRole(v, roleId) {
            this.policyRoles = this.policyRoles.filter(policyRole => policyRole.roleId !== roleId)
            if (v) { // 新增
                this.policyRoles.push({roleId: roleId})
            }
            this.roleBindShow()
            this.$emit('bind-policy-roles', this.policyRoles)
        },
        roleBindShow() {
            if (this.roles && this.roles.length > 0) {
                for (let i = 0; i < this.roles.length; i++) {
                    this.$set(this.roles, i, {...this.roles[i], bind: false})
                }
                for (let policyRole of this.policyRoles) {
                    for (let i = 0; i < this.roles.length; i++) {
                        if (policyRole.roleId === this.roles[i].roleId) {
                            this.$set(this.roles, i, {...this.roles[i], bind: true})
                        }
                    }
                }
            }
        }
    }
}
</script>
