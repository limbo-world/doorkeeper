<template>
    <el-container class="account-page">
        <el-header class="padding-top-xs" height="50px">
            <el-form ref="searchForm" :inline="true" size="mini">
                <el-form-item label="账号">
                    <el-input v-model="queryParam.nick" placeholder="输入昵称" @input="resetPageParam"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadAccounts" size="mini" icon="el-icon-search">查询</el-button>
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
                    <el-table-column prop="isAdmin" label="管理员" align="center" width="80">
                        <template slot-scope="scope">
                            {{ scope.row.isAdmin ? "是" : "否"}}
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" align="center">
                        <template slot-scope="scope">
                            <div class="operations">
                                <i v-if="!scope.row.isSuperAdmin && isAdminProjectSelected" class="el-icon-delete" @click="deleteAccount(scope.row)"></i>
                            </div>
                        </template>
                    </el-table-column>
                </el-table>
            </el-row>
        </el-main>

        <el-footer>
            <el-pagination background layout="prev, pager, next" :total="queryParam.total"
                           :current-page.sync="queryParam.current"
                           :page-size="queryParam.size" @current-change="loadAccounts">
            </el-pagination>
        </el-footer>

        <el-dialog title="添加管理员" :visible.sync="dialogOpened" width="50%" class="edit-dialog"
                   @close="closeEditDialog(false)">
            <account-edit :account="account" @cancel="closeEditDialog(false)" ref="accountEdit"/>
            <span slot="footer" class="dialog-footer">
                <el-button @click="closeEditDialog">取 消</el-button>
                <el-button type="primary" @click="dialogConfirm">确 定</el-button>
            </span>
        </el-dialog>

        <el-dialog title="项目绑定" :visible.sync="projectDialogOpened" width="60%" class="edit-dialog"
                   @close="projectDialogOpened = false">
            <account-project :account="account" @cancel="projectDialogOpened = false" ref="projectEdit"/>
            <span slot="footer" class="dialog-footer">
                <el-button @click="projectDialogOpened = false">取 消</el-button>
                <el-button type="primary" @click="projectDialogConfirm">确 定</el-button>
            </span>
        </el-dialog>

        <el-dialog title="授权" :visible.sync="grantDialogOpened" width="50%" class="edit-dialog"
                   @close="closeGrantDialog">
            <span slot="footer" class="dialog-footer">
                <el-button @click="closeGrantDialog">取 消</el-button>
                <el-button type="primary" @click="confirmGrantDialog">确 定</el-button>
            </span>
        </el-dialog>

    </el-container>
</template>


<script>

    import AccountEdit from './AccountEdit';
    import { mapState, mapActions } from 'vuex';

    export default {
        components: {
            AccountEdit,
        },

        data() {
            return {
                queryParam: {
                    nick: '',
                    current: 1,
                    size: 10,
                    total: -1,
                },

                accounts: [],

                account: {},
                dialogOpened: false,

                projectDialogOpened: false,

                grantAccountId: null,
                grantDialogOpened: false,

            };
        },

        computed: {
            ...mapState('session', ['user']),


            isAdminProjectSelected() { // 是否选中管理端项目
                return this.user.account.accountProjectId === this.user.account.currentProjectId;
            },
        },

        created() {
            pages.account = this;
            this.loadAccounts();
        },


        methods: {
            ...mapActions('ui', ['startProgress', 'stopProgress']),

            resetPageParam() {
                this.queryParam.current = 1;
                this.queryParam.total = -1;
            },

            loadAccounts() {
                this.startProgress();
                return this.$ajax.get('/account/query', {params: this.queryParam}).then(response => {
                    const page = response.data;
                    if (page.total > -1) {
                        this.queryParam.total = page.total;
                    }
                    this.accounts = page.data;
                }).finally(() => this.stopProgress());
            },

            closeEditDialog() {
                this.account = {};
                this.dialogOpened = false;
            },

            deleteAccount(account) {
                this.$confirm('此操作将永久删除该用户, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$ajax.delete(`/admin/${account.accountId}`).then(() => {
                        this.$message.success('删除成功。');
                        this.loadAccounts();
                    });
                }).catch(() => {});
            },

            dialogConfirm() {
                this.$refs.accountEdit.saveAccount().then(() => {
                    this.dialogOpened = false;
                    this.loadAccounts();
                });
            },

            // ----------------- 项目绑定
            showProjectDialog(account) {
                this.account = account;
                this.projectDialogOpened = true;
                if (this.$refs.projectEdit) {
                    this.$refs.projectEdit.loadProjects();
                }
            },
            projectDialogConfirm() {
                this.$refs.projectEdit.saveAccountProjects().then(() => {
                    this.projectDialogOpened = false;
                    this.$message.success('绑定成功。');
                });
            },


            // -------------------  授权相关
            openGrantDialog(account) {
                this.grantAccountId = account.accountId;
                this.grantDialogOpened = true;
            },

            confirmGrantDialog() {
                const loading = this.$loading();
                this.$refs.grant.saveGrant().then(() => {
                    this.closeGrantDialog();
                }).finally(() => loading.close());
            },

            closeGrantDialog() {
                this.grantDialogOpened = false;
                this.grantAccountId = null;
                this.$refs.grant.clearData();
            }
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
