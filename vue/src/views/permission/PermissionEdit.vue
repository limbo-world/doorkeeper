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
  - See the License for the specific language governing permissionAggregates and
  - limitations under the License.
  -->

<template>
    <el-container>
        <el-main>
            <el-form :model="permissionAggregate" label-width="120px" size="mini" class="edit-form" ref="editForm">
                <el-form-item label="名称" prop="name">
                    <el-input v-model="permissionAggregate.name"></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input type="textarea" v-model="permissionAggregate.description"></el-input>
                </el-form-item>

                <el-form-item label="资源">
                    <el-container class="permissionAggregate-resource-edit-page">
                        <el-transfer v-model="selectResourceIds" :data="resources" :titles="['未绑定', '已绑定']"></el-transfer>
                    </el-container>
                </el-form-item>

                <el-form-item label="策略">
                    <el-container class="permissionAggregate-policy-edit-page">
                        <el-transfer v-model="selectPolicyIds" :data="policies" :titles="['未绑定', '已绑定']"></el-transfer>
                    </el-container>
                </el-form-item>

                <el-form-item label="判断逻辑">
                    <el-select v-model="permissionAggregate.logic">
                        <el-option v-for="item in $constants.logics" :key="item.value" :label="item.label"
                                   :value="item.value"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="执行逻辑">
                    <el-select v-model="permissionAggregate.intention">
                        <el-option v-for="item in $constants.intentions" :key="item.value" :label="item.label"
                                   :value="item.value"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="是否启用">
                    <el-switch v-model="permissionAggregate.isEnabled" active-color="#13ce66"
                               inactive-color="#ff4949"></el-switch>
                </el-form-item>
                <el-form-item>
                    <el-button v-if="!permissionAggregate.permissionId" type="primary" @click="addPermission" size="mini">新增
                    </el-button>
                    <el-button v-if="permissionAggregate.permissionId" type="primary" @click="updatePermission" size="mini">保存
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
            permissionAggregate: {
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
        ...mapState('sessionAggregate', ['user']),
    },

    created() {
        pages.permissionEdit = this;
        this.permissionAggregate.clientId = this.$route.query.clientId;
        this.loadResources();
        this.loadPolicies();
        if (this.$route.query.permissionId) {
            this.permissionAggregate.permissionId = this.$route.query.permissionId;
            this.loadPermission();
        }
    },

    methods: {
        ...mapActions('ui', ['startProgress', 'stopProgress']),

        // ========== 资源相关  ==========
        loadResources() {
            this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/client/${this.permissionAggregate.clientId}/resource`, {
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
            this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/client/${this.permissionAggregate.clientId}/policy`, {
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
            this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/client/${this.permissionAggregate.clientId}/permissionAggregate/${this.permissionAggregate.permissionId}`).then(response => {
                this.permissionAggregate = response.data;
                this.selectPolicyIds = this.permissionAggregate.policies.map(policy => policy.policyId)
                this.selectResourceIds = this.permissionAggregate.resources.map(resource => resource.resourceId)
            }).finally(() => this.stopProgress());
        },
        addPermission() {
            let addParam = {...this.permissionAggregate, resourceIds: this.selectResourceIds, policyIds: this.selectPolicyIds}
            this.$ajax.post(`/admin/realm/${this.user.realm.realmId}/client/${this.permissionAggregate.clientId}/permissionAggregate`, addParam).then(response => {
                this.permissionAggregate = response.data;
                this.$message.success("新增成功")
                this.loadPermission();
            })
        },
        updatePermission() {
            let updateParam = {...this.permissionAggregate, resourceIds: this.selectResourceIds, policyIds: this.selectPolicyIds}
            this.$ajax.put(`/admin/realm/${this.user.realm.realmId}/client/${this.permissionAggregate.clientId}/permissionAggregate/${this.permissionAggregate.permissionId}`, updateParam).then(response => {
                this.$message.success("更新成功")
                this.loadPermission();
            })
        },
    }
}
</script>

<style lang="scss">
.permissionAggregate-resource-edit-page {
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

.permissionAggregate-policy-edit-page {
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
