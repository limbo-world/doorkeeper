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
    <el-container class="account-page">
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
                    <el-form-item>
                        <el-button type="primary" @click="loadUsers(1)" size="mini" icon="el-icon-search">查询
                        </el-button>
                        <el-button type="primary" @click="() => {
                            $router.push({path: '/user/user-edit'})
                            }" size="mini" icon="el-icon-circle-plus">新增</el-button>
                        <el-button type="primary" @click="batchEnable(true)" size="mini">批量启用</el-button>
                        <el-button type="primary" @click="batchEnable(false)" size="mini">批量停用</el-button>
                    </el-form-item>
                </el-form>
            </el-row>
        </el-header>

        <el-main>
            <el-table :data="users" size="mini" @selection-change="handleSelectionChange">
                <el-table-column type="selection" width="50"></el-table-column>
                <el-table-column prop="userId" label="ID"></el-table-column>
                <el-table-column prop="username" label="用户名"></el-table-column>
                <el-table-column prop="nickname" label="昵称"></el-table-column>
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
                                $router.push({path: '/user/user-edit',query: {
                                    userId: scope.row.userId}})}"></i>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
        </el-main>

        <el-footer>
            <el-pagination background layout="prev, pager, next" :total="queryForm.total"
                           :current-page.sync="queryForm.current"
                           :page-size="queryForm.size" @current-change="loadPermissions">
            </el-pagination>
        </el-footer>

    </el-container>
</template>


<script>
import {mapActions, mapState} from 'vuex';

export default {
    data() {
        return {
            queryForm: {
                current: 1,
                size: 10,
                total: -1,
            },
            users: [],
            selectUsers: []
        };
    },

    computed: {
        ...mapState('session', ['user', 'authExpEvaluator']),
    },

    created() {
        pages.permission = this;
        this.loadUsers();
    },


    methods: {
        ...mapActions('ui', ['startProgress', 'stopProgress']),

        resetPageForm() {
            this.queryForm.current = 1;
            this.queryForm.total = -1;
        },

        loadUsers(current) {
            if (1 === current) {
                this.resetPageForm();
            }
            this.startProgress();
            return this.$ajax.get('/admin/user', {
                params: {...this.queryForm, addRealmId: true}
            }).then(response => {
                const page = response.data;
                this.queryForm.total = page.total >= 0 ? page.total : this.queryForm.total;
                this.users = page.data;
            }).finally(() => this.stopProgress());
        },

        handleSelectionChange(val) {
            this.selectPermissions = val;
        },

        batchEnable(v) {
            let permissionIds = [];
            if (this.selectPermissions && this.selectPermissions.length > 0 ) {
                this.selectPermissions.forEach(permission => permissionIds.push(permission.permissionId))
            }
            this.startProgress();
            return this.$ajax.post('/admin/permission/batch', {
                type: this.$constants.batchMethod.UPDATE, isEnabled: v, permissionIds: permissionIds
            }).then(response => {
                this.loadPermissions();
            }).finally(() => this.stopProgress());
        }

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
