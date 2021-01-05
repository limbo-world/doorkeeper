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
        <el-header class="padding-top-xs" height="80px">
            <el-row>
                <el-form ref="searchForm" :inline="true" size="mini">
                    <el-form-item label="账号">
                        <el-input v-model="queryForm.dimName" placeholder="输入名称"></el-input>
                    </el-form-item>
                    <el-form-item label="URI">
                        <el-input v-model="queryForm.dimUri" placeholder="uri"></el-input>
                    </el-form-item>
                    <el-form-item label="标签名">
                        <el-input v-model="queryForm.dimKey"></el-input>
                    </el-form-item>
                    <el-form-item label="标签值">
                        <el-input v-model="queryForm.dimValue"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-button type="primary" @click="loadResources(1)" size="mini" icon="el-icon-search">查询
                        </el-button>
                        <el-button type="primary" @click="() => {
                            $router.push({path: '/resource/resource-edit',query: {clientId: clientId}})
                        }" size="mini" icon="el-icon-circle-plus">新增
                        </el-button>
                        <el-button type="primary" @click="batchBindAccount(selectAccounts)" size="mini">批量启用</el-button>
                        <el-button type="primary" @click="batchBindAccount(selectAccounts)" size="mini">批量停用</el-button>
                    </el-form-item>
                </el-form>
            </el-row>
        </el-header>

        <el-main>
            <el-table :data="accounts" size="mini" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="50"></el-table-column>
                <el-table-column prop="resourceId" label="ID"></el-table-column>
                <el-table-column prop="description" label="描述"></el-table-column>
                <el-table-column label="是否启用">
                    <template slot-scope="scope">
                        {{ scope.row.isEnabled ? "已启用" : "未启用" }}
                    </template>
                </el-table-column>
                <el-table-column label="操作" align="center" width="100">
                    <template slot-scope="scope">
                        <div class="operations">
                            <i v-if="!scope.row.isSuperAdmin" class="el-icon-edit" @click="editAccount(scope.row)"></i>
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
            return this.$ajax.get('/admin/resource', {
                params: {...this.queryForm, clientId: this.clientId, addRealmId: true}
            }).then(response => {
                const page = response.data;
                this.queryForm.total = page.total >= 0 ? page.total : this.queryForm.total;
                this.resources = page.data;
            }).finally(() => this.stopProgress());
        },

        handleSelectionChange(val) {
            this.selectResources = val;
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
