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
    <el-container class="permission-page">
        <el-header class="padding-top-xs" height="50px">
            <el-form ref="searchForm" :inline="true" size="mini">
                <el-form-item label="名称">
                    <el-input v-model="queryForm.apiName" placeholder="输入API名称"></el-input>
                </el-form-item>
                <el-form-item label="类型">
                    <el-select v-model="queryForm.apiMethod" placeholder="请选择">
                        <el-option key="GET" label="GET" value="GET"></el-option>
                        <el-option key="POST" label="POST" value="POST"></el-option>
                        <el-option key="PUT" label="PUT" value="PUT"></el-option>
                        <el-option key="DELETE" label="DELETE" value="DELETE"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="Url">
                    <el-input v-model="queryForm.apiUrl" placeholder="输入API路径"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadApis" size="mini" icon="el-icon-search">查询</el-button>
                    <el-button type="primary" @click="addPermission" size="mini" icon="el-icon-circle-plus">添加</el-button>
                </el-form-item>
            </el-form>
        </el-header>

        <el-main>
            <el-table :data="apis" size="mini">
                <el-table-column prop="apiName" label="名称"></el-table-column>
                <el-table-column prop="apiMethod" label="类型"></el-table-column>
                <el-table-column prop="apiUrl" label="url"></el-table-column>
                <el-table-column prop="apiDescribe" label="描述"></el-table-column>
                <el-table-column label="操作" width="120" align="center">
                    <template slot-scope="scope">
                        <div class="operations">
                            <template v-if="!scope.row.isDefault">
                                <i class="el-icon-edit" @click="editPermission(scope.row)"></i>
                                <i class="el-icon-delete" @click="deletePermission(scope.row)"></i>
                            </template>
                            <template v-else>
                                <i class="el-icon-view" @click="viewPermission(scope.row)"></i>
                            </template>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
        </el-main>

        <el-footer>
            <el-pagination :page-size="queryForm.size" :total="queryForm.total" :current-page.sync="queryForm.current"
                           layout="prev, pager, next" background @current-change="loadApis">
            </el-pagination>
        </el-footer>


        <el-dialog :title="`${dialogOpenMode === 'add' ? '新增' : '修改'}权限`" @close="dialogCancel"
                   :visible.sync="dialogOpened" width="50%" class="edit-dialog">
            <permission-edit :permission="permission" ref="permissionEdit" :view-mode="dialogOpenMode === 'view'"></permission-edit>
            <el-footer class="text-right">
                <el-button @click="dialogCancel">取 消</el-button>
                <el-button type="primary" @click="dialogConfirm">确 定</el-button>
            </el-footer>
        </el-dialog>

    </el-container>
</template>



<script>
    import { mapActions } from 'vuex';
    import PermissionEdit from './PermissionEdit';

    export default {
        components: {
            PermissionEdit
        },

        data() {
            return {
                queryForm: {
                    keyword: '',
                    api: '',
                    current: 1,
                    size: 10,
                    total: -1
                },

                apis: [],

                permission: {},
                dialogOpened: false,
                dialogOpenMode: 'add',
            }
        },

        created() {
            pages.permission = this;

            this.loadApis();
        },

        methods: {
            ...mapActions('ui', ['startProgress', 'stopProgress']),

            loadApis() {
                this.startProgress({ speed: 'fast' });
                this.$ajax.get('/api/query', {
                    params: this.queryForm
                }).then(response => {
                    const page = response.data;
                    this.queryForm.total = page.total >= 0 ? page.total : this.queryForm.total;
                    this.apis = page.data;
                }).finally(() => this.stopProgress());
            },

            permissionOnlineChanged(perm) {
                const loading = this.$loading();
                perm.isOnline = !perm.isOnline;
                this.$ajax.put(`/permission/${perm.permCode}`, perm).then(response => {
                    this.$message.success(`已修改${perm.isOnline ? '上线' : '下线'}`);
                    this.loadApis();
                }).finally(() => loading.close());
            },

            addPermission() {
                this.permission = {
                    permCode: '',
                    permName: '',
                    apiList: [],
                    isAdd: true,
                };
                this.dialogOpenMode = 'add';
                this.dialogOpened = true;
            },

            editPermission(perm) {
                this.permission = perm;
                this.dialogOpenMode = 'update';
                this.dialogOpened = true;
            },

            viewPermission(perm) {
                this.permission = perm;
                this.dialogOpenMode = 'view';
                this.dialogOpened = true;
            },

            dialogCancel() {
                this.$refs.permissionEdit.clearData();
                this.dialogOpened = false;
            },

            dialogConfirm() {
                this.$refs.permissionEdit.savePermission().then(() => {
                    this.dialogOpened = false;
                    this.loadApis();
                });
            },

            deletePermission(perm) {
                this.$ajax.delete(`/permission/${perm.permCode}`)
                    .then(() => {
                        this.$message.success('删除成功。');
                        this.loadApis();
                    })
            },

        }

    }
</script>



<style lang="scss">
    .permission-page {
        .el-table {
            .cell {
                min-height: 22px;
            }
        }

        .edit-dialog {
            .el-dialog {
                min-width: 500px;
            }
        }
    }
</style>
