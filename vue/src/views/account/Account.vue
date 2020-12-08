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
                    <el-button type="primary" @click="loadAccounts(1)" size="mini" icon="el-icon-search">查询</el-button>
                    <el-button type="primary" @click="addAccount" size="mini" icon="el-icon-circle-plus">新增</el-button>
                </el-form-item>
            </el-form>
        </el-header>

        <el-main>
            <el-row>
                <el-button type="primary" @click="editAccountRole(accountIds)" size="mini">绑定角色</el-button>
                <el-button type="primary" @click="editAccountAdminRole(accountIds)" size="mini">页面权限</el-button>
            </el-row>
            <el-row>
                <el-table :data="accounts" ref="accountTable" size="mini" @selection-change="handleSelectionChange">
                    <el-table-column type="selection" width="55"></el-table-column>
                    <el-table-column prop="accountId" label="ID"></el-table-column>
                    <el-table-column prop="username" label="账号"></el-table-column>
                    <el-table-column prop="nickname" label="昵称"></el-table-column>
                    <el-table-column prop="accountDescribe" label="描述"></el-table-column>
                    <el-table-column prop="isAdmin" label="管理员" align="center" width="80">
                        <template slot-scope="scope">
                            {{scope.row.isAdmin ? "是" : "否"}}
                        </template>
                    </el-table-column>
                    <el-table-column label="绑定角色" align="center" width="100"
                                     v-if="!user.account.currentProject.isAdminProject">
                        <template slot-scope="scope">
                            <div class="operations">
                                <i v-if="!scope.row.isAdmin" class="el-icon-view" @click="viewAccountRole([scope.row.accountId])"></i>
                                <i v-if="!scope.row.isAdmin" class="el-icon-edit" @click="editAccountRole([scope.row.accountId])"></i>
                            </div>
                        </template>
                    </el-table-column>
                    <el-table-column label="页面权限" align="center" width="100">
                        <template slot-scope="scope">
                            <div class="operations">
                                <i v-if="!scope.row.isAdmin" class="el-icon-view" @click="viewAccountAdminRole([scope.row.accountId])"></i>
                                <i v-if="!scope.row.isAdmin" class="el-icon-edit" @click="editAccountAdminRole([scope.row.accountId])"></i>
                            </div>
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
            </el-row>
        </el-main>

        <el-footer>
            <el-pagination background layout="prev, pager, next" :total="queryForm.total"
                           :current-page.sync="queryForm.current"
                           :page-size="queryForm.size" @current-change="loadAccounts">
            </el-pagination>
        </el-footer>

        <el-dialog :title="`${dialogOpenMode}账户`" :visible.sync="accountDialogOpened" width="50%" class="edit-dialog"
                   @close="accountDialogCancel">
            <account-edit :account="account" :open-mode="dialogOpenMode" ref="accountEdit"></account-edit>
            <span slot="footer" class="dialog-footer">
                <el-button @click="accountDialogCancel">取 消</el-button>
                <el-button type="primary" @click="accountDialogConfirm">确 定</el-button>
            </span>
        </el-dialog>

        <el-dialog :title="`${dialogOpenMode}账户角色`" :visible.sync="accountRoleDialogOpened" width="70%" class="edit-dialog"
                   @close="accountRoleDialogCancel" @opened="beforeAccountRoleDialogOpen">
            <account-role-edit :account-ids="accountIds" :open-mode="dialogOpenMode" ref="accountRoleEdit"></account-role-edit>
            <span slot="footer" class="dialog-footer">
                <el-button @click="accountRoleDialogCancel">取 消</el-button>
                <el-button type="primary" @click="accountRoleDialogConfirm">确 定</el-button>
            </span>
        </el-dialog>

        <el-dialog :title="`${dialogOpenMode}页面权限`" :visible.sync="accountAdminRoleDialogOpened" width="70%" class="edit-dialog"
                   @close="accountAdminRoleDialogCancel" @opened="beforeAccountAdminRoleDialogOpen">
            <account-admin-role-edit :account="account" :open-mode="dialogOpenMode" ref="accountAdminRoleEdit"></account-admin-role-edit>
            <span slot="footer" class="dialog-footer">
                <el-button @click="accountAdminRoleDialogCancel">取 消</el-button>
                <el-button type="primary" @click="accountAdminRoleDialogConfirm">确 定</el-button>
            </span>
        </el-dialog>

    </el-container>
</template>


<script>

    import AccountEdit from './AccountEdit';
    import AccountRoleEdit from './AccountRoleEdit';
    import AccountAdminRoleEdit from './AccountAdminRoleEdit';
    import {mapActions, mapState} from 'vuex';

    export default {
        components: {
            AccountEdit, AccountRoleEdit, AccountAdminRoleEdit
        },
        computed: {
            ...mapState('session', ['user']),
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

                accountIds: [],
                dialogOpenMode: '',
                accountDialogOpened: false,
                accountRoleDialogOpened: false,
                accountAdminRoleDialogOpened: false,
            };
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

            loadAccounts(current) {
                if (1 === current) {
                    this.resetPageForm();
                }
                this.startProgress();
                return this.$ajax.get('/account/query', {params: this.queryForm}).then(response => {
                    const page = response.data;
                    this.queryForm.total = page.total >= 0 ? page.total : this.queryForm.total;
                    this.accounts = page.data;
                }).finally(() => this.stopProgress());
            },

            handleSelectionChange(accounts) {
                this.accountIds = []
                accounts.forEach(account => {
                    this.accountIds.push(account.accountId)
                })
            },

            // ========= 账户相关 ============
            addAccount() {
                this.account = {};
                this.dialogOpenMode = '新增';
                this.accountDialogOpened = true;
            },
            editAccount(account) {
                this.account = account;
                console.log(account)
                this.dialogOpenMode = '修改';
                this.accountDialogOpened = true;
            },
            accountDialogConfirm() {
                this.$refs.accountEdit.confirmEdit().then(() => {
                    this.account = {};
                    this.accountDialogOpened = false;
                    if ('新增' === this.dialogOpenMode) {
                        this.resetPageForm()
                    }
                    this.loadAccounts()
                }).catch(err => err);
            },
            accountDialogCancel() {
                this.$refs.accountEdit.clearData();
                this.accountDialogOpened = false;
            },

            // ========= 角色相关 ============
            viewAccountRole(accountIds) {
                this.$refs.accountTable.clearSelection();
                this.accountIds = accountIds;
                this.dialogOpenMode = '查看';
                this.accountRoleDialogOpened = true;
            },
            editAccountRole(accountIds) {
                if (accountIds.length <= 0) {
                    this.$message.info("请选择账户")
                    return
                }
                this.$refs.accountTable.clearSelection();
                this.accountIds = accountIds;
                this.dialogOpenMode = '修改';
                this.accountRoleDialogOpened = true;
            },
            beforeAccountRoleDialogOpen() {
                this.$refs.accountRoleEdit.preOpen();
            },
            accountRoleDialogConfirm() {
                this.$refs.accountRoleEdit.confirmEdit().then(() => {
                    this.accountIds = [];
                    this.accountRoleDialogOpened = false;
                }).catch(err => err);
            },
            accountRoleDialogCancel() {
                this.$refs.accountRoleEdit.clearData();
                this.accountRoleDialogOpened = false;
            },

            // ========= 页面权限相关 ============
            viewAccountAdminRole(accountIds) {
                this.$refs.accountTable.clearSelection();
                this.accountIds = accountIds;
                this.dialogOpenMode = '查看';
                this.accountAdminRoleDialogOpened = true;
            },
            editAccountAdminRole(accountIds) {
                if (accountIds.length <= 0) {
                    this.$message.info("请选择账户")
                    return
                }
                this.$refs.accountTable.clearSelection();
                this.accountIds = accountIds;
                this.dialogOpenMode = '修改';
                this.accountAdminRoleDialogOpened = true;
            },
            beforeAccountAdminRoleDialogOpen() {
                this.$refs.accountAdminRoleEdit.preOpen();
            },
            accountAdminRoleDialogConfirm() {
                this.$refs.accountAdminRoleEdit.confirmEdit().then(() => {
                    this.accountIds = [];
                    this.accountAdminRoleDialogOpened = false;
                }).catch(err => err);
            },
            accountAdminRoleDialogCancel() {
                this.$refs.accountAdminRoleEdit.clearData();
                this.accountAdminRoleDialogOpened = false;
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
