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
        <el-header class="padding-top-xs" height="50px">
            <el-form ref="searchForm" :inline="true" size="mini">
                <el-form-item label="账号">
                    <el-input v-model="queryForm.username" placeholder="输入账号"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadAccounts(true)" size="mini" icon="el-icon-search">查询</el-button>
                    <el-button type="primary" @click="addAccount" size="mini" icon="el-icon-circle-plus">新增</el-button>
                </el-form-item>
            </el-form>
        </el-header>

        <el-main>
            <el-row>
                <el-table :data="accounts" size="mini">
                    <el-table-column prop="accountId" label="ID"></el-table-column>
                    <el-table-column prop="username" label="账号"></el-table-column>
                    <el-table-column prop="accountDescribe" label="描述"></el-table-column>
                    <el-table-column prop="isAdmin" label="等级" align="center" width="80">
                        <template slot-scope="scope">
                            {{ scope.row.isAdmin ? (scope.row.isSuperAdmin ? "超级管理员" : "管理员") : "普通用户"}}
                        </template>
                    </el-table-column>
                    <el-table-column prop="lastLogin" label="最后登录时间" width="120"></el-table-column>
                    <el-table-column label="绑定角色" align="center" width="80">
                        <template slot-scope="scope">
                            <div class="operations">
                                <i class="el-icon-edit" @click="editRole(scope.row)"></i>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="绑定项目" align="center" width="80">
                        <template slot-scope="scope">
                            <div class="operations">
                                <!--<i v-if="!scope.row.isSuperAdmin && isAdminProjectSelected" class="el-icon-delete" @click="deleteAccount(scope.row)"></i>-->
                                <i class="el-icon-edit" @click="editRole(scope.row)"></i>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" align="center" width="100">
                        <template slot-scope="scope">
                            <div class="operations">
                                <!--<i v-if="!scope.row.isSuperAdmin && isAdminProjectSelected" class="el-icon-delete" @click="deleteAccount(scope.row)"></i>-->
                                <i class="el-icon-edit" @click="editRole(scope.row)"></i>
                                <i class="el-icon-delete" @click="() => {
                                deleteRole([scope.row.roleId])
                            }"></i>
                            </div>
                        </template>
                    </el-table-column>
                </el-table>
            </el-row>
        </el-main>

        <el-footer>
            <el-pagination background layout="prev, pager, next" :total="queryForm.total"
                           :current-page.sync="queryForm.current"
                           :page-size="queryForm.size" @current-change="loadAccounts">
            </el-pagination>
        </el-footer>

        <el-dialog :title="`${dialogOpenMode}账户`" :visible.sync="accountDialogOpened" width="50%" class="edit-dialog"
                   @close="closeEditDialog(false)">
            <admin-edit :account="account" @cancel="closeEditDialog(false)" ref="accountEdit"></admin-edit>
            <span slot="footer" class="dialog-footer">
                <el-button @click="closeEditDialog">取 消</el-button>
                <el-button type="primary" @click="dialogConfirm">确 定</el-button>
            </span>
        </el-dialog>

        <el-dialog :title="`${dialogOpenMode}账户角色`" :visible.sync="projectDialogOpened" width="60%" class="edit-dialog"
                   @close="projectDialogOpened = false">
            <account-project :account="account" @cancel="projectDialogOpened = false" ref="projectEdit"></account-project>
            <span slot="footer" class="dialog-footer">
                <el-button @click="projectDialogOpened = false">取 消</el-button>
                <el-button type="primary" @click="projectDialogConfirm">确 定</el-button>
            </span>
        </el-dialog>

        <el-dialog :title="`${dialogOpenMode}账户项目`" :visible.sync="grantDialogOpened" width="50%" class="edit-dialog"
                   @close="closeGrantDialog">
            <span slot="footer" class="dialog-footer">
                <el-button @click="closeGrantDialog">取 消</el-button>
                <el-button type="primary" @click="confirmGrantDialog">确 定</el-button>
            </span>
        </el-dialog>

    </el-container>
</template>


<script>

    import AdminEdit from './AdminEdit';
    import {mapActions, mapState} from 'vuex';

    export default {
        components: {
            AdminEdit,
        },

        data() {
            return {
                queryForm: {
                    current: 1,
                    size: 10,
                    total: -1,
                },

                accounts: [],

                account: {},
                dialogOpenMode: '',
                accountDialogOpened: false,
                roleDialogOpened: false,
                projectDialogOpened: false,
            };
        },

        computed: {
            ...mapState('session', ['user']),
        },

        created() {
            pages.account = this;
            this.loadAccounts();
        },


        methods: {
            ...mapActions('ui', ['startProgress', 'stopProgress']),

            resetPageForm() {
                this.queryForm.current = 1;
                this.queryForm.total = -1;
            },

            loadAccounts(resetPage) {
                if (resetPage) {
                    this.resetPageForm();
                }
                this.startProgress();
                return this.$ajax.get('/account/query', {params: this.queryForm}).then(response => {
                    const page = response.data;
                    this.queryForm.total = page.total >= 0 ? page.total : this.queryForm.total;
                    this.accounts = page.data;
                }).finally(() => this.stopProgress());
            },

            // ========= 账户相关 ============
            addAccount() {
                this.account = {};
                this.dialogOpenMode = '新增';
                this.accountDialogOpened = true;
            },
            editAccount(account) {
                this.account = account;
                this.dialogOpenMode = '修改';
                this.accountDialogOpened = true;
            },

            // ========= 角色相关 ============
            viewRole(project) {
                this.project = project;
                this.dialogOpenMode = '查看';
                this.roleDialogOpened = true;
            },
            editRole(project) {
                this.project = project;
                this.dialogOpenMode = '修改';
                this.roleDialogOpened = true;
            },

            // ========= 项目相关 ============
            viewProject(project) {
                this.project = project;
                this.dialogOpenMode = '查看';
                this.projectDialogOpened = true;
            },
            editProject(project) {
                this.project = project;
                this.dialogOpenMode = '修改';
                this.projectDialogOpened = true;
            },
        }

    }
</script>


<style lang="scss">
    .account-page {
        .el-table {
            .operations {
                .el-button {
                    font-size: 10px;
                }
            }
        }

        .edit-dialog {
            .el-dialog {
                min-width: 500px;
                .el-dialog__body {
                    max-height: 500px;
                    overflow: auto;
                }
            }
        }
    }
</style>
