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
    <el-container class="policy-page">
        <el-header class="padding-top-xs" height="80px">
            <el-row>
                <el-form ref="searchForm" :inline="true" size="mini">
                    <el-form-item label="名称">
                        <el-input v-model="queryForm.dimName" placeholder="输入名称"></el-input>
                    </el-form-item>
                    <el-form-item label="启用">
                        <el-select v-model="queryForm.isEnabled" clearable>
                            <el-option v-for="item in $constants.enableTypes" :key="item.value" :label="item.label"
                                       :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item label="类型">
                        <el-select v-model="queryForm.type" clearable>
                            <el-option v-for="item in $constants.policyTypes" :key="item.value" :label="item.label"
                                       :value="item.value"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadPolicies(1)" size="mini" icon="el-icon-search">查询
                        </el-button>
                        <el-button type="primary" @click="() => {
                            $router.push({path: '/policy/policy-edit',query: {clientId: clientId}})
                        }" size="mini" icon="el-icon-circle-plus">新增
                        </el-button>
                        <el-button type="primary" @click="batchEnable(true)" size="mini">批量启用</el-button>
                        <el-button type="primary" @click="batchEnable(false)" size="mini">批量停用</el-button>
                    </el-form-item>
                </el-form>
            </el-row>
        </el-header>

        <el-main>
            <el-table :data="policies" size="mini" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="50"></el-table-column>
                <el-table-column prop="policyId" label="ID"></el-table-column>
                <el-table-column prop="name" label="名称"></el-table-column>
                <el-table-column prop="description" label="描述"></el-table-column>
                <el-table-column label="类型">
                    <template slot-scope="scope">
                        {{ $constants.policyTypes.filter(type => type.value === scope.row.type).length > 0 ?
                            $constants.policyTypes.filter(type => type.value === scope.row.type)[0].label : '' }}
                    </template>
                </el-table-column>
                <el-table-column label="是否启用">
                    <template slot-scope="scope">
                        {{ scope.row.isEnabled ? "已启用" : "未启用" }}
                    </template>
                </el-table-column>
                <el-table-column label="操作" align="center" width="100">
                    <template slot-scope="scope">
                        <div class="operations">
                            <i class="el-icon-edit" @click="() => {
                                $router.push({path: '/policy/policy-edit',query: {clientId: clientId, policyId: scope.row.policyId}})
                            }"></i>
                            <i @click="()=>{deletePolicies([scope.row.policyId])}" class="el-icon-delete"></i>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
        </el-main>

        <el-footer>
            <el-pagination background layout="prev, pager, next" :total="queryForm.total"
                           :current-page.sync="queryForm.current"
                           :page-size="queryForm.size" @current-change="loadPolicies">
            </el-pagination>
        </el-footer>

    </el-container>
</template>


<script>

import {mapActions, mapState} from 'vuex';

export default {
    props: {
        clientId: {
            type: Number,
            default: null
        },
    },

    data() {
        return {
            queryForm: {
                current: 1,
                size: 10,
                total: -1,
            },
            policies: [],
            selectPolicies: [],
        };
    },

    computed: {
        ...mapState('session', ['user']),
    },

    created() {
        pages.policy = this;
        this.loadPolicies();
    },


    methods: {
        ...mapActions('ui', ['startProgress', 'stopProgress']),

        resetPageForm() {
            this.queryForm.current = 1;
            this.queryForm.total = -1;
        },

        loadPolicies(current) {
            if (1 === current) {
                this.resetPageForm();
            }
            this.startProgress();
            return this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/client/${this.clientId}/policy`, {
                params: this.queryForm
            }).then(response => {
                const page = response.data;
                this.queryForm.total = page.total >= 0 ? page.total : this.queryForm.total;
                this.policies = page.data;
            }).finally(() => this.stopProgress());
        },

        handleSelectionChange(val) {
            this.selectPolicies = val;
        },

        batchEnable(v) {
            let policyIds = [];
            if (this.selectPolicies && this.selectPolicies.length > 0 ) {
                this.selectPolicies.forEach(policy => policyIds.push(policy.policyId))
            }
            this.startProgress();
            return this.$ajax.post(`/admin/realm/${this.user.realm.realmId}/client/${this.clientId}/policy/batch`, {
                type: this.$constants.batchMethod.UPDATE, isEnabled: v, policyIds: policyIds
            }).then(response => {
                this.loadPolicies();
            }).finally(() => this.stopProgress());
        },

        deletePolicies(policyIds) {
            this.$confirm('确认删除, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.dialogProcessing = true;
                this.$ajax.post(`/admin/realm/${this.user.realm.realmId}/client/${this.clientId}/policy/batch`, {
                    type: this.$constants.batchMethod.DELETE, policyIds: policyIds
                }).then(() => {
                    this.loadPolicies();
                }).finally(() => this.dialogProcessing = false);
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });
            });
        },

    }

}
</script>


<style lang="scss">
.resource-page {
    .el-table {
        .operations {
            .el-button {
                font-size: 10px;
            }
        }
    }
}
</style>
