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
            <el-form :model="policy" label-width="120px" size="mini" class="edit-form" ref="editForm">
                <el-form-item label="名称" prop="name">
                    <el-input v-model="policy.name" :disabled="policy.policyId"></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input type="textarea" v-model="policy.description"></el-input>
                </el-form-item>
                <el-form-item label="类型">
                    <el-select v-model="policy.type">
                        <el-option v-for="item in policyTypes" :key="item.value" :label="item.label"
                                   :value="item.value"></el-option>
                    </el-select>
                </el-form-item>
                <template>
                    <policy-role-edit v-if="policy.type === policyTypes[0].value" @bind-policy-roles="roles => {policy.roles = roles}"
                                      :policy-id="policy.policyId" :client-id="policy.clientId"></policy-role-edit>
                </template>
                <el-form-item label="执行逻辑">
                    <el-select v-model="policy.intention">
                        <el-option v-for="item in intentions" :key="item.value" :label="item.label"
                                   :value="item.value"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="是否启用">
                    <el-switch v-model="policy.isEnabled" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
                </el-form-item>
                <el-form-item>
                    <el-button v-if="!policy.policyId" type="primary" @click="addPolicy" size="mini">新增</el-button>
                    <el-button v-if="policy.policyId" type="primary" @click="updatePolicy" size="mini">保存</el-button>
                </el-form-item>
            </el-form>
        </el-main>
    </el-container>
</template>


<script>

    import PolicyRoleEdit from "@/views/policy/PolicyRoleEdit";
    import { mapState, mapActions } from 'vuex';
    import AppConstants from "@/utils/AppConstants";

    export default {
        components: {
            PolicyRoleEdit
        },
        data() {
            return {
                policy: {
                    roles: []
                },
                policyTypes: AppConstants.policyTypes,
                intentions: AppConstants.intentions,
            };
        },

        computed: {
            ...mapState('session', ['realm']),
        },

        created() {
            pages.policyEdit = this;
            this.policy.clientId = this.$route.query.clientId;
            if (this.$route.query.policyId) {
                this.policy.policyId = this.$route.query.policyId;
                this.loadPolicy();
            }
        },

        methods: {
            ...mapActions('ui', ['startProgress', 'stopProgress']),

            // ========== 资源相关 ==========
            loadPolicy() {
                this.startProgress({ speed: 'fast' });
                this.$ajax.get(`/admin/realm/${this.realm.realmId}/client/${this.clientId}/policy/${this.policy.policyId}`).then(response => {
                    this.policy = response.data;
                }).finally(() => this.stopProgress());
            },
            addPolicy() {
                this.$ajax.post(`/admin/realm/${this.realm.realmId}/client/${this.clientId}/policy`, this.policy).then(response => {
                    this.loadPolicy();
                })
            },
            updatePolicy() {
                this.$ajax.put(`/admin/realm/${this.realm.realmId}/client/${this.clientId}/policy/${this.policy.policyId}`, this.policy).then(response => {
                    this.loadPolicy();
                })
            },
        }
    }
</script>
