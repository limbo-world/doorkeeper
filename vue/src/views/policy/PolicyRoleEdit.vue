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

            <el-main>
                <el-transfer v-model="selectRoleIds" :data="roles" :titles="['未绑定', '未绑定']"
                             @change="changeRole"></el-transfer>
            </el-main>

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
            selectRoleIds: [],
        };
    },

    created() {
        pages.policyRoleEdit = this;
        this.loadClients();
        // 初始化数据
        this.policyRoles.forEach(policyRole => {
            policyRole.key = policyRole.roleId;
            policyRole.label = policyRole.clientName ? policyRole.clientName + "  " + policyRole.name : "域  " + policyRole.name;
        })
        this.concatRoles([], []);
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
                let roles = response.data;
                let roleIds = [];
                for (let role of roles) {
                    role.key = role.roleId;
                    role.label = role.clientName ? role.clientName + "  " + role.name : "域  " + role.name;
                    roleIds.push(role.roleId);
                }
                this.concatRoles(roles, roleIds);
            });
        },
        changeRole(roleIds, d, mvRoleIds) {
            // policyRoles是所有选择的 他不一定在roles里面
            if (d === 'right') {
                // 主要用于去除重复
                this.policyRoles = this.policyRoles.filter(policyRole => mvRoleIds.indexOf(policyRole.roleId) < 0)
                // 获取新增的role
                let roles = this.roles.filter(role => mvRoleIds.indexOf(role.roleId) >= 0)
                this.policyRoles = this.policyRoles.concat(roles)
            } else {
                // 删除里面的
                this.policyRoles = this.policyRoles.filter(policyRole => mvRoleIds.indexOf(policyRole.roleId) < 0)
            }
            this.$emit('bind-policy-roles', this.policyRoles)
        },
        concatRoles(roles, roleIds) {
            // 把不存在的加入进来
            let policyRoles = this.policyRoles.filter(policyRole => roleIds.indexOf(policyRole.roleId) < 0)
            roles = roles.concat(policyRoles);
            this.roles = roles;
            // 每次查询完，需要把已有的加入
            this.selectRoleIds = this.policyRoles.map(policyRole => policyRole.roleId)
        }
    }
}
</script>

<style lang="scss">
.policy-role-edit-page {
    .el-transfer {
        .el-transfer-panel {
            width: 300px;
            .el-transfer-panel__item {
                margin-left: 0;
                display: block!important;
            }
        }
    }

    // 本来考虑两个穿梭框隐藏掉右边元素达到查询的效果
    //.el-transfer {
    //    display: inline-block;
    //    .el-transfer__buttons {
    //        display: none;
    //    }
    //    .el-transfer-panel {
    //        width: 300px;
    //    }
    //    >div.el-transfer-panel:last-of-type {
    //        display: none;
    //    }
    //}
}
</style>
