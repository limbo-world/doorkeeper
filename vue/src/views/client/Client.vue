<template>
    <el-container class="project-page">
        <el-header class="padding-top-xs" height="50px">
            <el-form ref="searchForm" :inline="true" size="mini">
                <el-form-item label="名称">
                    <el-input v-model="queryForm.name" placeholder="输入名称"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadClients(1)" size="mini" icon="el-icon-search">查询</el-button>
                    <el-button type="primary" @click="() =>{dialogOpened = true;}" size="mini" icon="el-icon-circle-plus">新增</el-button>
                </el-form-item>
            </el-form>
        </el-header>

        <el-main>
            <el-table :data="clients" size="mini">
                <el-table-column prop="clientId" label="ID"></el-table-column>
                <el-table-column prop="name" label="名称"></el-table-column>
                <el-table-column prop="description" label="描述"></el-table-column>
                <el-table-column label="是否启用">
                    <template slot-scope="scope">
                        {{scope.row.isEnabled ? "已启用" : "未启用"}}
                    </template>
                </el-table-column>
                <el-table-column label="操作">
                    <template slot-scope="scope">
                        <div class="operations">
                            <template>
                                <i @change="()=>{
                                    toClientEdit(scope.row.clientId)
                                }" class="el-icon-edit"></i>
                                <i class="el-icon-delete"></i>
                            </template>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
        </el-main>

        <el-footer>
            <el-pagination background layout="prev, pager, next" :total="queryForm.total" :page-size="queryForm.size"
                           :current-page.sync="queryForm.current" @current-change="loadProjects">
            </el-pagination>
        </el-footer>


        <el-dialog title="新增" :visible.sync="dialogOpened" width="50%" class="edit-dialog" :before-close="preventCloseWhenProcessing">
            <el-form :model="client" label-width="80px" size="mini" class="edit-form" ref="editForm">
                <el-form-item label="名称">
                    <el-input v-model="client.name" placeholder="请输入名称"></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input v-model="client.description" placeholder="请输入描述"></el-input>
                </el-form-item>
                <el-form-item label="是否启用">
                    <el-switch v-model="client.isEnabled"
                               active-color="#13ce66"
                               inactive-color="#ff4949"></el-switch>
                </el-form-item>
            </el-form>
            <el-footer class="text-right">
                <el-button @click="() => {client = {}; dialogOpened = false;}" :disabled="dialogProcessing">取 消</el-button>
                <el-button type="primary" @click="addClient" :loading="dialogProcessing"
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

                clients: [],

                client: {},
                dialogOpened: false,
                dialogProcessing: false,
            }
        },

        computed: {
            ...mapState('session', ['user']),
        },

        created() {
            pages.client = this;

            this.loadClients();
        },

        methods: {
            ...mapActions('ui', ['startProgress', 'stopProgress']),

            resetPageForm() {
                this.queryForm.current = 1;
                this.queryForm.total = -1;
            },

            loadClients(current) {
                if (1 === current) {
                    this.resetPageForm();
                }
                this.startProgress();
                this.$ajax.get('/admin/client', {params: this.queryForm}).then(response => {
                    const page = response.data;
                    this.queryForm.total = page.total >= 0 ? page.total : this.queryForm.total;
                    this.clients = page.data;
                }).finally(() => this.stopProgress());
            },

            addClient() {
                this.dialogProcessing = true;
                this.$ajax.post('/admin/client', {...this.client, realmId: this.user.realm.realmId}).then(() => {
                    this.loadClients();
                    this.dialogOpened = false;
                }).finally(() => this.dialogProcessing = false);
            },

            preventCloseWhenProcessing() {
                if (this.dialogProcessing) {
                    return false;
                }

                this.project = {};
                this.dialogOpened = false;
            },

            toClientEdit(project) {
                const json = JSON.stringify({projectId: project.projectId, projectName: project.projectName,
                    isAdminProject: project.isAdminProject})
                this.$router.push({path: '/project/project-account',
                    query: {params: json}
                })
            }
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
