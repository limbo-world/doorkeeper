<template>
    <el-container class="permission-page">
        <el-header class="padding-top-xs" height="50px">
            <el-form ref="searchForm" :inline="true" size="mini">
                <el-form-item label="名称">
                    <el-input v-model="queryForm.permissionName" placeholder="输入名称"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadPermissions" size="mini" icon="el-icon-search">查询</el-button>
                    <el-button type="primary" @click="addPermission" size="mini" icon="el-icon-circle-plus">添加权限</el-button>
                </el-form-item>
            </el-form>
        </el-header>

        <el-main>
            <el-table :data="permissions" size="mini">
                <el-table-column prop="permissionName" label="名称"></el-table-column>
                <el-table-column prop="permissionDescribe" label="描述"></el-table-column>
                <el-table-column prop="isOnline" label="是否启用" width="100">
                    <template slot-scope="scope">
                        <div class="el-form--mini">
                            <el-switch :value="scope.row.isOnline" class="block el-switch--mini" size="mini"
                                       @change="permissionOnlineChanged(scope.row)"></el-switch>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="150" align="center">
                    <template slot-scope="scope">
                        <div class="operations">
                            <i class="el-icon-view" @click="viewPermission(scope.row)"></i>
                            <i class="el-icon-edit" @click="editPermission(scope.row)"></i>
                            <i class="el-icon-delete" @click="() => {
                                deletePermission([scope.row.permissionId])
                            }"></i>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
        </el-main>

        <el-footer>
            <el-pagination :page-size="queryForm.size" :total="queryForm.total" :current-page.sync="queryForm.current"
                           layout="prev, pager, next" background @current-change="loadPermissions">
            </el-pagination>
        </el-footer>


        <el-dialog :title="`${dialogOpenMode}权限`" @close="dialogCancel"
                   :visible.sync="dialogOpened" width="70%" class="edit-dialog" @opened="beforeDialogOpen">
            <permission-edit :permission="permission" ref="permissionEdit" :open-mode="dialogOpenMode"></permission-edit>
            <el-footer class="text-right">
                <el-button @click="dialogCancel">取 消</el-button>
                <el-button type="primary" v-if="'查看' !== dialogOpenMode" @click="dialogConfirm">确 定</el-button>
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

                permissions: [],

                permission: {},
                dialogOpened: false,
                dialogOpenMode: 'add',
            }
        },

        created() {
            pages.permission = this;

            this.loadPermissions();
        },

        methods: {
            ...mapActions('ui', ['startProgress', 'stopProgress']),

            loadPermissions() {
                this.startProgress({ speed: 'fast' });
                this.$ajax.get('/permission/query', {
                    params: this.queryForm
                }).then(response => {
                    const page = response.data;
                    this.queryForm.total = page.total >= 0 ? page.total : this.queryForm.total;
                    this.permissions = page.data;
                }).finally(() => this.stopProgress());
            },

            beforeDialogOpen() {
                this.$refs.permissionEdit.preOpen();
            },

            permissionOnlineChanged(perm) {
                const loading = this.$loading();
                perm.isOnline = !perm.isOnline;
                this.$ajax.put(`/permission/${perm.permCode}`, perm).then(response => {
                    this.$message.success(`已修改${perm.isOnline ? '上线' : '下线'}`);
                    this.loadPermissions();
                }).finally(() => loading.close());
            },

            addPermission() {
                this.permission = {};
                this.dialogOpenMode = '新增';
                this.dialogOpened = true;
            },

            editPermission(perm) {
                this.permission = perm;
                this.dialogOpenMode = '修改';
                this.dialogOpened = true;
            },

            viewPermission(perm) {
                this.permission = perm;
                this.dialogOpenMode = '查看';
                this.dialogOpened = true;
            },

            dialogCancel() {
                this.$refs.permissionEdit.clearData();
                this.dialogOpened = false;
            },

            dialogConfirm() {
                this.$refs.permissionEdit.confirmEdit().then(() => {
                    this.dialogOpened = false;
                    if ('新增' === this.dialogOpenMode) {
                        this.queryForm.total = -1;
                    }
                    this.loadPermissions();
                });
            },

            deletePermission(permissionIds) {
                this.$confirm('确认删除?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$ajax.delete(`/permission`, {data: permissionIds})
                        .then(() => {
                            this.$message. success('删除成功。');
                            this.queryForm.total = -1;
                            this.loadPermissions();
                        });
                }).catch(() => {
                    this.$message.info("已取消删除");
                });
            },

        }

    }
</script>



<style lang="scss" scoped>
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
