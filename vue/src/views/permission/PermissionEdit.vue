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
    <el-container>
        <el-main>
            <el-form :model="permission" label-width="120px" size="mini" class="edit-form" ref="editForm">
                <el-form-item label="名称" prop="name">
                    <el-input v-model="permission.name"></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input type="textarea" v-model="permission.description"></el-input>
                </el-form-item>

                <el-form-item label="资源">
                    <el-container class="permission-resource-edit-page">
                        <el-transfer v-model="selectResourceIds" :data="resources" :titles="['未绑定', '已绑定']"></el-transfer>
                    </el-container>
                </el-form-item>

                <el-form-item label="策略">
                    <el-container class="permission-policy-edit-page">
                        <el-transfer v-model="selectPolicyIds" :data="policies" :titles="['未绑定', '已绑定']"></el-transfer>
                    </el-container>
                </el-form-item>

                <el-form-item label="判断逻辑">
                    <el-select v-model="permission.logic">
                        <el-option v-for="item in $constants.logics" :key="item.value" :label="item.label"
                                   :value="item.value"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="执行逻辑">
                    <el-select v-model="permission.intention">
                        <el-option v-for="item in $constants.intentions" :key="item.value" :label="item.label"
                                   :value="item.value"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="是否启用">
                    <el-switch v-model="permission.isEnabled" active-color="#13ce66"
                               inactive-color="#ff4949"></el-switch>
                </el-form-item>
                <el-form-item>
                    <el-button v-if="!permission.permissionId" type="primary" @click="addPermission" size="mini">新增
                    </el-button>
                    <el-button v-if="permission.permissionId" type="primary" @click="updatePermission" size="mini">保存
                    </el-button>
                </el-form-item>
            </el-form>
        </el-main>
    </el-container>
</template>


<script>

import {mapState, mapActions} from 'vuex';

export default {
    data() {
        return {
            permission: {
                resources: [],
                policies: []
            },

            resources: [],
            selectResourceIds: [],

            policies: [],
            selectPolicyIds: []
        };
    },

    computed: {
        ...mapState('session', ['user']),
    },

    created() {
        pages.permissionEdit = this;
        this.permission.clientId = this.$route.query.clientId;
        this.loadResources();
        this.loadPolicies();
        if (this.$route.query.permissionId) {
            this.permission.permissionId = this.$route.query.permissionId;
            this.loadPermission();
        }
    },

    methods: {
        ...mapActions('ui', ['startProgress', 'stopProgress']),

        // ========== 资源相关  ==========
        loadResources() {
            this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/client/${this.permission.clientId}/resource`, {
                params: {needAll: true}
            }).then(response => {
                let resources = response.data.data;
                resources.forEach(resource => {
                    resource.key = resource.resourceId;
                    resource.label = resource.name;
                })
                this.resources = resources;
            });
        },

        // ========== 策略相关  ==========
        loadPolicies() {
            this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/client/${this.permission.clientId}/policy`, {
                params: {needAll: true}
            }).then(response => {
                let policies = response.data.data;
                for (let policy of policies) {
                    policy.key = policy.policyId;
                    policy.label = policy.name;
                }
                this.policies = policies;
            });
        },

        // ========== 权限相关 ==========
        loadPermission() {
            this.startProgress({speed: 'fast'});
            this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/client/${this.permission.clientId}/permission/${this.permission.permissionId}`).then(response => {
                this.permission = response.data;
                this.selectPolicyIds = this.permission.policies.map(policy => policy.policyId)
                this.selectResourceIds = this.permission.resources.map(resource => resource.resourceId)
            }).finally(() => this.stopProgress());
        },
        addPermission() {
            let addParam = {...this.permission, resourceIds: this.selectResourceIds, policyIds: this.selectPolicyIds}
            this.$ajax.post(`/admin/realm/${this.user.realm.realmId}/client/${this.permission.clientId}/permission`, addParam).then(response => {
                this.permission = response.data;
                this.$message.success("新增成功")
                this.loadPermission();
            })
        },
        updatePermission() {
            let updateParam = {...this.permission, resourceIds: this.selectResourceIds, policyIds: this.selectPolicyIds}
            this.$ajax.put(`/admin/realm/${this.user.realm.realmId}/client/${this.permission.clientId}/permission/${this.permission.permissionId}`, updateParam).then(response => {
                this.$message.success("更新成功")
                this.loadPermission();
            })
        },
    }
}
</script>

<style lang="scss">
.permission-resource-edit-page {
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

.permission-policy-edit-page {
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
