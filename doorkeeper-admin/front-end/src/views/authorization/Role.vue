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
    <el-container class="menu-page">
        <el-header class="padding-top-xs" height="50px">
            <el-form :inline="true" size="mini">
                <el-form-item label="名称">
                    <el-input v-model="queryForm.roleName" placeholder="请输入名称"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadRoles(1)" size="mini" icon="el-icon-search">查询</el-button>
                    <el-button type="primary" @click="addRole" size="mini" icon="el-icon-circle-plus">新增</el-button>
                </el-form-item>
            </el-form>
        </el-header>

        <el-main>
            <el-table :data="roles" ref="roleTable" size="mini">
                <!--<el-table-column type="selection" width="55"></el-table-column>-->
                <el-table-column align="left" prop="roleName" label="名称" width="150"></el-table-column>
                <el-table-column align="center" prop="roleDescribe" label="描述"></el-table-column>
                <el-table-column align="center" label="操作" width="150">
                    <template slot-scope="scope">
                        <div class="operations">
                            <i class="el-icon-view" @click="viewRole(scope.row)"></i>
                            <i class="el-icon-edit" @click="editRole(scope.row)"></i>
                            <i class="el-icon-delete" @click="() => {
                                deleteRole([scope.row.roleId])
                            }"></i>
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


        <el-dialog :title="`${dialogOpenMode}角色`" :visible.sync="dialogOpened" width="70%" class="edit-dialog"
                   @close="dialogCancel" @opened="beforeDialogOpen">
            <role-edit :role="role" ref="roleEdit" :open-mode="dialogOpenMode"></role-edit>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogCancel">取 消</el-button>
                <el-button type="primary" v-if="'查看' !== dialogOpenMode" @click="dialogConfirm">确 定</el-button>
            </span>
        </el-dialog>

    </el-container>
</template>



<script>
    import { mapActions } from 'vuex';
    import RoleEdit from './RoleEdit';

    export default {
        components: {
            RoleEdit,
        },

        data() {
            return {
                queryForm: {
                    current: 1,
                    size: 10,
                    total: -1
                },

                roles: [],

                role: {},
                dialogOpened: false,
                dialogOpenMode: '',
            }
        },

        created() {
            pages.role = this;

            this.loadRoles();
        },

        methods: {
            ...mapActions('ui', ['startProgress', 'stopProgress']),

            resetPageForm() {
                this.queryForm.current = 1;
                this.queryForm.total = -1;
            },

            loadRoles(current) {
                if (1 === current) {
                    this.resetPageForm();
                }
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
                this.role = {};
                this.dialogOpenMode = '新增';
                this.dialogOpened = true;
            },

            editRole(role) {
                this.role = role;
                this.dialogOpenMode = '修改';
                this.dialogOpened = true;
            },

            viewRole(role) {
                this.role = role;
                this.dialogOpenMode = '查看';
                this.dialogOpened = true;
            },

            beforeDialogOpen() {
                this.$refs.roleEdit.preOpen();
            },

            deleteRole(roleIds) {
                this.$confirm('确认删除?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$ajax.delete(`/role`, {data: roleIds})
                        .then(() => {
                            this.$message.success('删除成功。');
                            this.loadRoles(1);
                        });
                }).catch(() => {
                    this.$message.info("已取消删除");
                });
            },

            dialogCancel() {
                this.$refs.roleEdit.clearData();
                this.dialogOpened = false;
            },

            dialogConfirm() {
                this.$refs.roleEdit.confirmEdit().then(() => {
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
