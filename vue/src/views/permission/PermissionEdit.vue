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
                    <el-input v-model="permission.name" :disabled="permission.permissionId"></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input type="textarea" v-model="permission.description"></el-input>
                </el-form-item>

                <el-form-item label="资源">
                    <el-container class="permission-resource-edit-page">
                        <el-header height="30px">
                            <el-form :inline="true" size="mini">
                                <el-form-item label="名称">
                                    <el-input v-model="resourceQueryForm.dimName" placeholder="输入名称"></el-input>
                                </el-form-item>
                                <el-form-item>
                                    <el-button type="primary" @click="loadResources" size="mini" icon="el-icon-search">查询</el-button>
                                </el-form-item>
                            </el-form>
                        </el-header>
                        <el-main>
                            <el-transfer v-model="selectResourceIds" :data="resources" :titles="['未绑定', '未绑定']"
                                         @change="changeResource"></el-transfer>
                        </el-main>
                    </el-container>
                </el-form-item>

                <el-form-item label="策略">
                    <el-container class="permission-policy-edit-page">
                        <el-header height="30px">
                            <el-form :inline="true" size="mini">
                                <el-form-item label="名称">
                                    <el-input v-model="policyQueryForm.dimName" placeholder="输入名称"></el-input>
                                </el-form-item>
                                <el-form-item>
                                    <el-button type="primary" @click="loadPolicies" size="mini" icon="el-icon-search">查询</el-button>
                                </el-form-item>
                            </el-form>
                        </el-header>
                        <el-main>
                            <el-transfer v-model="selectPolicyIds" :data="policies" :titles="['未绑定', '未绑定']"
                                         @change="changePolicy"></el-transfer>
                        </el-main>
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
                    <el-switch v-model="permission.isEnabled" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
                </el-form-item>
                <el-form-item>
                    <el-button v-if="!permission.permissionId" type="primary" @click="addPermission" size="mini">新增</el-button>
                    <el-button v-if="permission.permissionId" type="primary" @click="updatePermission" size="mini">保存</el-button>
                </el-form-item>
            </el-form>
        </el-main>
    </el-container>
</template>


<script>

    import { mapState, mapActions } from 'vuex';

    export default {
        data() {
            return {
                permission: {
                    resources: [],
                    policies: []
                },

                resourceQueryForm: {
                    name: '',
                    size: 100
                },

                policyQueryForm: {
                    name: '',
                    size: 100
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
                    params: this.resourceQueryForm
                }).then(response => {
                    let resources = response.data.data;
                    let resourceIds = [];
                    for (let resource of resources) {
                        resource.key = resource.resourceId;
                        resource.label = resource.name;
                        resourceIds.push(resource.resourceId);
                    }
                    this.concatResources(resources, resourceIds);
                });
            },
            changeResource(resourceIds, d, mvResourceIds) {
                if (d === 'right') {
                    // 主要用于去除重复
                    this.permission.resources = this.permission.resources.filter(resource => mvResourceIds.indexOf(resource.resourceId) < 0)
                    // 获取新增的
                    let resources = this.resources.filter(resource => mvResourceIds.indexOf(resource.resourceId) >= 0)
                    this.permission.resources = this.permission.resources.concat(resources)
                } else {
                    // 删除里面的
                    this.permission.resources = this.permission.resources.filter(resource => mvResourceIds.indexOf(resource.resourceId) < 0)
                }
            },
            concatResources(resources, resourceIds) {
                // 把不存在的加入进来
                let permissionResources = this.permission.resources.filter(resource => resourceIds.indexOf(resource.resourceId) < 0)
                resources = resources.concat(permissionResources);
                this.resources = resources;
                // 每次查询完，需要把已有的加入
                this.selectResourceIds = this.permission.resources.map(resource => resource.resourceId)
            },

            // ========== 策略相关  ==========
            loadPolicies() {
                this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/client/${this.permission.clientId}/policy`, {
                    params: this.policyQueryForm
                }).then(response => {
                    let policies = response.data.data;
                    let policyIds = [];
                    for (let policy of policies) {
                        policy.key = policy.policyId;
                        policy.label = policy.name;
                        policyIds.push(policy.policyId);
                    }
                    this.concatPolicies(policies, policyIds);
                });
            },
            changePolicy(policyIds, d, mvPolicyIds) {
                if (d === 'right') {
                    // 主要用于去除重复
                    this.permission.policies = this.permission.policies.filter(policy => mvPolicyIds.indexOf(policy.policyId) < 0)
                    // 获取新增的
                    let policies = this.policies.filter(policy => mvPolicyIds.indexOf(policy.policyId) >= 0)
                    this.permission.policies = this.permission.policies.concat(policies)
                } else {
                    // 删除里面的
                    this.permission.policies = this.permission.policies.filter(policy => mvPolicyIds.indexOf(policy.policyId) < 0)
                }
            },
            concatPolicies(policies, policyIds) {
                // 把不存在的加入进来
                let permissionPolicies = this.permission.policies.filter(policy => policyIds.indexOf(policy.policyId) < 0)
                policies = policies.concat(permissionPolicies);
                this.policies = policies;
                // 每次查询完，需要把已有的加入
                this.selectPolicyIds = this.permission.policies.map(policy => policy.policyId)
            },

            // ========== 权限相关 ==========
            loadPermission() {
                this.startProgress({ speed: 'fast' });
                this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/client/${this.permission.clientId}/permission/${this.permission.permissionId}`).then(response => {
                    this.permission = response.data;
                    this.loadResources();
                    this.loadPolicies();
                }).finally(() => this.stopProgress());
            },
            addPermission() {
                this.$ajax.post(`/admin/realm/${this.user.realm.realmId}/client/${this.permission.clientId}/permission`, this.permission).then(response => {
                    this.permission = response.data;
                    this.loadPermission();
                })
            },
            updatePermission() {
                this.$ajax.put(`/admin/realm/${this.user.realm.realmId}/client/${this.permission.clientId}/permission/${this.permission.permissionId}`, this.permission).then(response => {
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
                    display: block!important;
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
                    display: block!important;
                }
            }
        }
    }
</style>
