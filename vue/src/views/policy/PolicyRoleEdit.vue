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
    <el-form-item label="角色">
        <div class="policy-role-edit-div">
            <el-transfer v-model="selectRealmRoleIds" :data="realmRoles" :titles="['域角色', '已绑定']"
                         @change="changeRole" filterable></el-transfer>
            <el-transfer v-model="selectClientRoleIds" :data="clientRoles" :titles="['委托方角色', '已绑定']"
                         @change="changeRole" filterable style="margin-top: 30px"></el-transfer>
        </div>
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
        policyRoles: {
            type: Array,
            default: []
        }
    },

    data() {
        return {
            realmRoles: [],
            clientRoles: [],
            selectRealmRoleIds: [],
            selectClientRoleIds: [],
        };
    },
    computed: {
        ...mapState('session', ['user']),
    },
    created() {
        pages.policyRoleEdit = this;
        this.loadRealmRoles();
        this.loadClientRoles();
    },

    methods: {
        loadRealmRoles() {
            this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/role`, {
                params: {size: 1000, clientId: 0}
            }).then(response => {
                let roles = response.data.data;
                let roleIds = [];
                for (let role of roles) {
                    role.key = role.roleId;
                    role.label = role.name;
                    roleIds.push(role.roleId);
                }
                this.realmRoles = roles;
                // 加入已有的
                let hasIds = this.policyRoles.map(r => r.roleId);
                let selectRealmRoleIds = [];
                for (let id of roleIds) {
                    if (hasIds.indexOf(id) >= 0) {
                        selectRealmRoleIds.push(id);
                    }
                }
                this.selectRealmRoleIds = selectRealmRoleIds;
            });
        },
        loadClientRoles() {
            this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/role`, {
                params: {size: 1000, clientId: this.clientId}
            }).then(response => {
                let roles = response.data.data;
                let roleIds = [];
                for (let role of roles) {
                    role.key = role.roleId;
                    role.label = role.name;
                    roleIds.push(role.roleId);
                }
                this.clientRoles = roles;
                // 加入已有的
                let hasIds = this.policyRoles.map(r => r.roleId);
                let selectClientRoleIds = [];
                for (let id of roleIds) {
                    if (hasIds.indexOf(id) >= 0) {
                        selectClientRoleIds.push(id);
                    }
                }
                this.selectClientRoleIds = selectClientRoleIds;
            });
        },
        changeRole(roleIds, d, mvRoleIds) {
            let policyRoles = [];
            for (let role of this.realmRoles) {
                if (this.selectRealmRoleIds.indexOf(role.roleId) >= 0) {
                    policyRoles.push(role);
                }
            }
            for (let role of this.clientRoles) {
                if (this.selectClientRoleIds.indexOf(role.roleId) >= 0) {
                    policyRoles.push(role);
                }
            }
            this.$emit('bind-policy-roles', policyRoles)
        },
    }
}
</script>

<style lang="scss">
.policy-role-edit-div {
    .el-transfer {
        .el-transfer-panel {
            width: 350px;

            .el-transfer-panel__item {
                margin-left: 0;
                display: block !important;
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
