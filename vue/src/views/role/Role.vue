<template>
    <el-container class="project-page">
        <el-header class="padding-top-xs" height="50px">
            <el-form ref="searchForm" :inline="true" size="mini">
                <el-form-item label="名称">
                    <el-input v-model="queryForm.name" placeholder="输入名称"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadClients(1)" size="mini" icon="el-icon-search">查询</el-button>
                    <el-button type="primary" @click="() =>{dialogOpened = true;dialogOpenMode='新增'}" size="mini" icon="el-icon-circle-plus">新增</el-button>
                </el-form-item>
            </el-form>
        </el-header>

        <el-main>
            <el-table :data="roles" size="mini">
                <el-table-column prop="roleId" label="ID"></el-table-column>
                <el-table-column prop="name" label="名称"></el-table-column>
                <el-table-column prop="description" label="描述"></el-table-column>
                <el-table-column label="是否启用">
                    <template slot-scope="scope">
                        {{scope.row.isEnabled ? "已启用" : "未启用"}}
                    </template>
                </el-table-column>
                <el-table-column label="默认添加">
                    <template slot-scope="scope">
                        {{scope.row.isDefault ? "是" : "否"}}
                    </template>
                </el-table-column>
                <el-table-column label="操作">
                    <template slot-scope="scope">
                        <div class="operations">
                            <template>
                                <i @click="()=>{toClientEdit(scope.row.clientId)}" class="el-icon-edit"></i>
                            </template>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
        </el-main>

        <el-footer>
            <el-pagination background layout="prev, pager, next" :total="queryForm.total" :page-size="queryForm.size"
                           :current-page.sync="queryForm.current" @current-change="loadRoles">
            </el-pagination>
        </el-footer>

        <el-dialog title="新增" :visible.sync="dialogOpened" width="50%" class="edit-dialog" :before-close="preventCloseWhenProcessing">
            <el-form :model="role" label-width="80px" size="mini" class="edit-form" ref="editForm">
                <el-form-item label="名称">
                    <el-input v-model="role.name"></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input type="textarea" v-model="role.description"></el-input>
                </el-form-item>
                <el-form-item label="默认添加">
                    <el-switch v-model="role.isDefault" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
                </el-form-item>
                <el-form-item label="是否启用">
                    <el-switch v-model="role.isEnabled" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
                </el-form-item>
            </el-form>
            <el-footer class="text-right">
                <el-button @click="() => {role = {}; dialogOpened = false;}" :disabled="dialogProcessing">取 消</el-button>
                <el-button type="primary" @click="addRole" :loading="dialogProcessing"
                           :disabled="dialogProcessing">确 定</el-button>
            </el-footer>
        </el-dialog>

    </el-container>
</template>



<script>
    import { mapState, mapActions } from 'vuex';

    export default {
        data() {
            return {
                queryForm: {
                    name: '',
                    current: 1,
                    size: 10,
                    total: -1,
                },

                roles: [],

                role: {},
                dialogOpened: false,
                dialogOpenMode: '',
                dialogProcessing: false,
            }
        },

        computed: {
            ...mapState('session', ['user']),
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
                this.$ajax.get('/admin/role', {params: {...this.queryForm, addRealmId: true}}).then(response => {
                    const page = response.data;
                    this.queryForm.total = page.total >= 0 ? page.total : this.queryForm.total;
                    this.roles = page.data;
                }).finally(() => this.stopProgress());
            },

            addRole() {
                this.dialogProcessing = true;
                this.$ajax.post('/admin/role', {...this.role, addRealmId: true}).then(() => {
                    this.loadRoles();
                    this.dialogOpened = false;
                }).finally(() => this.dialogProcessing = false);
            },

            preventCloseWhenProcessing() {
                if (this.dialogProcessing) {
                    return false;
                }

                this.role = {};
                this.dialogOpened = false;
            },

            toClientEdit(clientId) {

            },

        }

    }
</script>

<style lang="scss">
    .project-page {
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
