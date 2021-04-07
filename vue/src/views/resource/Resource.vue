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
    <el-container class="resource-page">
        <el-header class="padding-top-xs" height="100px">
            <el-row>
                <el-form ref="searchForm" :inline="true" size="mini">
                    <el-form-item label="名称">
                        <el-input v-model="queryForm.dimName" placeholder="输入名称"></el-input>
                    </el-form-item>
                    <el-form-item label="URI">
                        <el-input v-model="queryForm.dimUri" placeholder="uri"></el-input>
                    </el-form-item>
                    <el-form-item label="标签名">
                        <el-input v-model="queryForm.dimK"></el-input>
                    </el-form-item>
                    <el-form-item label="标签值">
                        <el-input v-model="queryForm.dimV"></el-input>
                    </el-form-item>
                    <el-form-item label="启用">
                        <el-select v-model="queryForm.isEnabled" clearable>
                            <el-option key="已启用" label="已启用" :value="true"></el-option>
                            <el-option key="未启用" label="未启用" :value="false"></el-option>
                        </el-select>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadResources(1)" size="mini" icon="el-icon-search">查询
                        </el-button>
                        <el-button type="primary" @click="() => {
                            $router.push({path: '/resource/resource-edit',query: {clientId: clientId}})
                        }" size="mini" icon="el-icon-circle-plus">新增
                        </el-button>
                        <el-button type="primary" @click="batchEnable(true)" size="mini">批量启用</el-button>
                        <el-button type="primary" @click="batchEnable(false)" size="mini">批量停用</el-button>
                    </el-form-item>
                </el-form>
            </el-row>
        </el-header>

        <el-main>
            <el-table :data="resources" size="mini" height="300" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="50"></el-table-column>
                <el-table-column prop="resourceId" label="ID"></el-table-column>
                <el-table-column prop="name" label="名称"></el-table-column>
                <el-table-column prop="description" label="描述"></el-table-column>
                <el-table-column label="是否启用">
                    <template slot-scope="scope">
                        {{ scope.row.isEnabled ? "已启用" : "未启用" }}
                    </template>
                </el-table-column>
                <el-table-column label="操作" align="center" width="100">
                    <template slot-scope="scope">
                        <div class="operations">
                            <i class="el-icon-edit" @click="() => {
                                $router.push({path: '/resource/resource-edit',query: {
                                    clientId: clientId, resourceId: scope.row.resourceId}
                                })}"></i>
                            <i @click="()=>{deleteResources([scope.row.resourceId])}" class="el-icon-delete"></i>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
        </el-main>

        <el-footer>
            <el-pagination background layout="prev, pager, next" :total="queryForm.total"
                           :current-page.sync="queryForm.current"
                           :page-size="queryForm.size" @current-change="loadResources">
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
            resources: [],
            selectResources: [],
        };
    },

    computed: {
        ...mapState('session', ['user', 'authExpEvaluator']),
    },

    created() {
        pages.resource = this;
        this.loadResources();
    },


    methods: {
        ...mapActions('ui', ['startProgress', 'stopProgress']),

        resetPageForm() {
            this.queryForm.current = 1;
            this.queryForm.total = -1;
        },

        loadResources(current) {
            if (1 === current) {
                this.resetPageForm();
            }
            this.startProgress();
            return this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/client/${this.clientId}/resource`, {
                params: this.queryForm
            }).then(response => {
                const page = response.data;
                this.queryForm.total = page.total >= 0 ? page.total : this.queryForm.total;
                this.resources = page.data;
            }).finally(() => this.stopProgress());
        },

        handleSelectionChange(val) {
            this.selectResources = val;
        },

        batchEnable(v) {
            let resourceIds = [];
            if (this.selectResources && this.selectResources.length > 0) {
                this.selectResources.forEach(resource => resourceIds.push(resource.resourceId))
            }
            this.startProgress();
            return this.$ajax.post(`/admin/realm/${this.user.realm.realmId}/client/${this.clientId}/resource/batch`, {
                type: this.$constants.batchMethod.UPDATE, isEnabled: v, resourceIds: resourceIds
            }).then(response => {
                this.loadResources();
            }).finally(() => this.stopProgress());
        },

        deleteResources(resourceIds) {
            this.$confirm('确认删除, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.dialogProcessing = true;
                this.$ajax.post(`/admin/realm/${this.user.realm.realmId}/client/${this.clientId}/resource/batch`, {
                    type: this.$constants.batchMethod.DELETE, resourceIds: resourceIds
                }).then(() => {
                    this.loadResources();
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
