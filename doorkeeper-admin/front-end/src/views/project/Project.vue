<template>
    <el-container class="project-page">
        <el-header class="padding-top-xs" height="50px">
            <el-form ref="searchForm" :inline="true" size="mini">
                <el-form-item label="项目名称">
                    <el-input v-model="queryParam.projectName" placeholder="输入项目名称"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadProjects" size="mini" icon="el-icon-search">查询</el-button>
                    <el-button type="primary" @click="() =>{
                            dialogOpened = true;
                        }" size="mini" icon="el-icon-circle-plus">添加项目</el-button>
                </el-form-item>
            </el-form>
        </el-header>

        <el-main>
            <el-table :data="projects" size="mini">
                <el-table-column prop="projectId" label="项目ID"></el-table-column>
                <el-table-column prop="projectName" label="项目名称"></el-table-column>
                <el-table-column prop="projectDescribe" label="项目描述"></el-table-column>
                <el-table-column label="secret">
                    <template slot-scope="scope">
                        <div class="operations" v-if="scope.row.projectSecret">
                            <span>{{scope.row.projectSecret}}</span>
                            <el-link type="primary" @click="hideSecret(scope.$index)">隐藏</el-link>
                        </div>
                        <div class="operations" v-else>
                            <span>******</span>
                            <el-link type="primary" @click="getSecret(scope.row.projectId, scope.$index)">显示</el-link>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column prop="gmtCreated" label="创建时间"></el-table-column>
                <el-table-column prop="gmtModified" label="修改时间"></el-table-column>
                <el-table-column label="操作">
                    <template slot-scope="scope">
                        <div class="operations">
                            <template v-if="!scope.row.isDefault && !isAdminProject(scope.row.projectId)">
                                <i class="el-icon-edit" @click="() =>{
                                    project = {...scope.row};
                                    dialogOpened = true;
                                }"></i>
                                <i class="el-icon-delete" @click="deleteProject(scope.row)"></i>
                            </template>
                        </div>
                    </template>
                </el-table-column>
            </el-table>
        </el-main>

        <el-footer>
            <el-pagination background layout="prev, pager, next" :total="queryParam.total" :page-size="queryParam.size"
                           :current-page.sync="queryParam.current" @current-change="loadProjects">
            </el-pagination>
        </el-footer>


        <el-dialog title="编辑" :visible.sync="dialogOpened" width="50%" class="edit-dialog" :before-close="preventCloseWhenProcessing">
            <el-form :model="project" label-width="80px" size="mini" class="edit-form"
                     :rules="rules" ref="editForm">
                <el-form-item label="项目名称">
                    <el-input v-model="project.projectName" placeholder="项目名称"></el-input>
                </el-form-item>
                <el-form-item label="项目秘钥">
                    <el-input v-model="project.projectSecret" placeholder="项目秘钥"></el-input>
                </el-form-item>
                <el-form-item label="项目描述">
                    <el-input type="textarea" v-model="project.projectDescribe" placeholder="项目描述"></el-input>
                </el-form-item>
            </el-form>
            <el-footer class="text-right">
                <el-button @click="() => {
                    project = {};
                    dialogOpened = false;
                }" :disabled="dialogProcessing">取 消</el-button>
                <el-button type="primary" @click="editProject" :loading="dialogProcessing"
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
                queryParam: {
                    projectName: '',
                    current: 1,
                    size: 10,
                    total: -1,
                },

                projects: [],

                project: {
                },
                dialogOpened: false,
                dialogProcessing: false,
            }
        },

        computed: {
            ...mapState('session', ['user']),
        },

        created() {
            pages.project = this;

            this.loadProjects();
        },

        methods: {
            ...mapActions('ui', ['startProgress', 'stopProgress']),

            loadProjects() {
                this.startProgress();
                this.$ajax.get('/project/query', {params: this.queryParam}).then(response => {
                    const page = response.data;
                    if (page.total > -1) {
                        this.queryParam.total = page.total;
                    }

                    this.projects = page.data;
                }).finally(() => this.stopProgress());
            },

            getSecret(projectId, idx) {
                this.$ajax.get(`/project/${projectId}/secret`).then(response => {
                    let project = this.projects[idx];
                    project.projectSecret = response.data;

                    Vue.set(this.projects, idx, project);
                });
            },

            hideSecret(idx) {
                let project = this.projects[idx];
                project.projectSecret = null;

                Vue.set(this.projects, idx, project);
            },

            isAdminProject(projectId) {
                return this.user.account.accountProjectId === projectId;
            },

            editProject() {
                let project = this.project;
                if (!project.projectName || project.projectName.length <= 0) {
                    this.$message.error('项目名称不能为空！');
                    return;
                }

                this.dialogProcessing = true;
                if (project.projectId) {
                    // 更新操作
                    this.$ajax.put(`/project/${project.projectId}`, project).then(() => {
                        this.dialogOpened = false;
                        this.$message.success('更新成功。');
                        this.project = {};
                        this.loadProjects();
                    }).finally(() => this.dialogProcessing = false);
                } else {
                    // 新增操作
                    this.$ajax.post('/project', project).then(() => {
                        window.location.reload();
                    }).finally(() => this.dialogProcessing = false);
                }

            },

            deleteProject(project) {
                this.$confirm('此操作将永久删除该项目, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$ajax.delete(`/project/${project.projectId}`).then(() => {
                        this.$message.success('删除成功。');
                        this.loadProjects();
                    })
                }).catch(err => err);
            },

            preventCloseWhenProcessing() {
                if (this.dialogProcessing) {
                    return false;
                }

                this.project = {};
                this.dialogOpened = false;
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
