<template>
    <el-container class="permission-page">
        <el-header class="padding-top-xs" height="50px">
            <el-form ref="searchForm" :inline="true" size="mini">
                <el-form-item label="名称/编码/描述">
                    <el-input v-model="queryForm.keyword" placeholder="输入关键字"></el-input>
                </el-form-item>
                <el-form-item label="API">
                    <el-input v-model="queryForm.api" placeholder="输入API路径"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadPermissions" size="mini" icon="el-icon-search">查询</el-button>
                    <el-button type="primary" @click="addPermission" size="mini" icon="el-icon-circle-plus">添加权限</el-button>
                </el-form-item>
            </el-form>
        </el-header>

        <el-main>
            <el-table :data="permissions" size="mini">
                <el-table-column prop="permCode" label="编码"></el-table-column>
                <el-table-column prop="permName" label="名称"></el-table-column>
                <el-table-column prop="permDesc" label="描述信息"></el-table-column>
                <el-table-column prop="isOnline" label="是否已启用" height="30px">
                    <template slot-scope="scope">
                        <div class="el-form--mini">
                            <el-switch :value="scope.row.isOnline" class="block el-switch--mini" size="mini"
                                       @change="permissionOnlineChanged(scope.row)"></el-switch>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="API列表" width="300px">
                    <template slot-scope="scope">
                        <div v-for="api in scope.row.apiList" :key="api">
                            {{api}}
                        </div>
                    </template>
                </el-table-column>
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
                           layout="prev, pager, next" background @current-change="loadPermissions">
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

                    const permissions = page.data;
                    permissions.forEach(p => {
                        if (!p.apiList) {
                            p.apiList = p.api ? p.api.split(',') : [];
                        }
                    });
                    this.permissions = permissions;
                }).finally(() => this.stopProgress());
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
                    this.loadPermissions();
                });
            },

            deletePermission(perm) {
                this.$ajax.delete(`/permission/${perm.permCode}`)
                    .then(() => {
                        this.$message.success('删除成功。');
                        this.loadPermissions();
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
