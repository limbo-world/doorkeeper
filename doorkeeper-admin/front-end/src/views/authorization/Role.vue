<template>
    <el-container class="menu-page">
        <el-header class="padding-top-xs" height="50px">
            <el-form :inline="true" size="mini">
                <el-form-item label="名称/编码">
                    <el-input v-model="queryForm.keyword" placeholder="输入关键字" @input="initPageParam"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadRoles" size="mini" icon="el-icon-search">查询</el-button>
                    <el-button type="primary" @click="addRole" size="mini" icon="el-icon-circle-plus">添加角色</el-button>
                </el-form-item>
            </el-form>
        </el-header>

        <el-main>
            <el-table :data="roles" ref="roleTable" size="mini">
                <!--<el-table-column type="selection" width="55"></el-table-column>-->
                <el-table-column align="left" prop="roleName" label="名称"></el-table-column>
                <el-table-column align="center" prop="roleDesc" label="描述信息"></el-table-column>
                <el-table-column align="center" prop="accounts" label="授权用户">
                    <template slot-scope="scope">
                        <el-link type="primary" @click="grantRole(scope.row)">点击修改授权</el-link>
                    </template>
                </el-table-column>
                <el-table-column align="center" label="操作" width="120">
                    <template slot-scope="scope">
                        <div class="operations">
                            <template v-if="!scope.row.isDefault">
                                <i class="el-icon-edit" @click="editRole(scope.row)"></i>
                                <i class="el-icon-delete" @click="deleteRole(scope.row)"></i>
                            </template>
                            <template>
                                <i class="el-icon-view" @click="viewRole(scope.row)"></i>
                            </template>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
        </el-main>

        <el-footer>
            <el-pagination :page-size="queryForm.size" :total="queryForm.total" :current-page.sync="queryForm.current"
                           layout="prev, pager, next" background @current-change="loadRoles">
            </el-pagination>
        </el-footer>


        <el-dialog :title="dialogTitle" :visible.sync="dialogOpened" width="50%" class="edit-dialog"
                   @close="closeEditDialog(false)">
            <role-edit v-if="dialogOpenMode !== 'grant'" :role="role" @cancel="closeEditDialog(false)"
                       @confirm="closeEditDialog(true)" ref="roleEdit" :view-mode="dialogOpenMode === 'view'">
            </role-edit>
            <role-grant v-else :role="role" ref="roleGrant"></role-grant>

            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogCancel">取 消</el-button>
                <el-button type="primary" @click="dialogConfirm">确 定</el-button>
            </span>
        </el-dialog>

    </el-container>
</template>



<script>
    import { mapActions } from 'vuex';
    import RoleEdit from './RoleEdit';
    import RoleGrant from './RoleGrant';

    export default {
        components: {
            RoleEdit, RoleGrant,
        },

        data() {
            return {
                queryForm: {
                    keyword: '',
                    current: 1,
                    size: 10,
                    total: -1
                },

                roles: [],

                role: {},
                dialogOpened: false,
                dialogOpenMode: 'add',

                selectedMenuCodeList: [],
            }
        },

        computed: {
            dialogTitle() {
                return this.dialogOpenMode === 'add' ? '新增角色'
                        : this.dialogOpened === 'update' ? '修改角色'
                        : this.dialogOpenMode === 'grant' ? '授权角色 - ' + this.role.roleName : '未知操作';
            }
        },

        created() {
            pages.role = this;

            this.loadRoles();
        },

        methods: {
            ...mapActions('ui', ['startProgress', 'stopProgress']),

            initPageParam() {
                this.queryForm.current = 1;
                this.queryForm.size = 10;
                this.queryForm.total = -1;
            },

            loadRoles() {
                this.startProgress();
                this.$ajax.get('/role/query', {
                    params: this.queryForm
                }).then(response => {
                    const page = response.data;
                    this.queryForm.total = page.total >= 0 ? page.total : this.queryForm.total;

                    this.roles = page.data;
                }).finally(() => this.stopProgress());
            },

            addRole() {
                this.role = {
                    roleName: '',
                    menus: [],
                    accounts: [],
                };
                this.dialogOpenMode = 'add';
                this.dialogOpened = true;
            },

            editRole(role) {
                this.role = role;
                this.dialogOpenMode = 'update';
                this.dialogOpened = true;
            },

            viewRole(role) {
                this.role = role;
                this.dialogOpenMode = 'view';
                this.dialogOpened = true;
            },

            closeEditDialog(refresh) {
                this.role = {};
                this.dialogOpened = false;
                if (refresh) {
                    this.loadRoles();
                }
            },

            deleteRole(role) {
                this.$ajax.delete(`/role/${role.roleId}`)
                    .then(() => {
                        this.$message.success('删除成功。');
                        this.loadRoles();
                    });
            },

            // 角色授权
            grantRole(role) {
                this.role = role;
                this.dialogOpenMode = 'grant';
                this.dialogOpened = true;
            },

            dialogCancel(refresh) {
                const prom = this.dialogOpenMode === 'grant'
                    ? this.$immediate(() => this.$refs.roleGrant.clearData())
                    : this.$immediate(() => this.$refs.roleEdit.clearData());
                prom.then(() => {
                    this.role = {};
                    this.dialogOpened = false;
                    if (refresh) {
                        this.loadRoles()
                    }
                }).catch(err => err);
            },

            dialogConfirm() {
                const prom = this.dialogOpenMode === 'grant'
                    ? this.$refs.roleGrant.saveGrant()
                    : this.$refs.roleEdit.saveRole();
                prom.then(() => {
                    this.role = {};
                    this.dialogOpened = false;
                    this.loadRoles()
                }).catch(err => err);
            },
        }

    }
</script>



<style lang="scss">
    .menu-page {

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
