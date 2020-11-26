<template>
    <el-container class="project-page">
        <el-header class="padding-top-xs" height="50px">
            <el-form ref="searchForm" :inline="true" size="mini">
                <el-form-item label="项目名称">
                    <el-input v-model="queryForm.projectName" placeholder="输入项目名称"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadProjects(true)" size="mini" icon="el-icon-search">查询</el-button>
                    <el-button type="primary" @click="addProject" size="mini" icon="el-icon-circle-plus">新增</el-button>
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
                            <i class="el-icon-view" @click="viewProject(scope.row)"></i>
                            <i class="el-icon-edit" @click="editProject(scope.row)"></i>
                            <i class="el-icon-delete" @click="() => {
                                deleteProject([scope.row.projectId])
                            }"></i>
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


        <el-dialog :title="`${dialogOpenMode}项目`" :visible.sync="dialogOpened" width="70%" class="edit-dialog"
                   @close="dialogCancel" @opened="beforeDialogOpen">
            <project-edit :project="project" ref="projectEdit" :open-mode="dialogOpenMode"></project-edit>
            <el-footer class="text-right">
                <el-button @click="dialogCancel">取 消</el-button>
                <el-button type="primary" v-if="'查看' !== dialogOpenMode" @click="dialogConfirm">确 定</el-button>
            </el-footer>
        </el-dialog>

    </el-container>
</template>



<script>
    import ProjectEdit from './ProjectEdit';
    import { mapState, mapActions } from 'vuex';

    export default {
        components: {
            ProjectEdit,
        },

        data() {
            return {
                queryForm: {
                    projectName: '',
                    current: 1,
                    size: 10,
                    total: -1,
                },

                projects: [],

                project: {},
                dialogOpenMode: '',
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

            initPageForm() {
                this.queryForm.current = 1;
                this.queryForm.size = 10;
                this.queryForm.total = -1;
            },

            loadProjects(initPage) {
                if (initPage) {
                    this.initPageForm();
                }
                this.startProgress();
                this.$ajax.get('/project/query', {params: this.queryForm}).then(response => {
                    const page = response.data;
                    this.queryForm.total = page.total >= 0 ? page.total : this.queryForm.total;
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

            addProject() {
                this.project = {};
                this.dialogOpenMode = '新增';
                this.dialogOpened = true;
            },

            editProject(project) {
                this.project = project;
                this.dialogOpenMode = '修改';
                this.dialogOpened = true;
            },

            viewProject(project) {
                this.project = project;
                this.dialogOpenMode = '查看';
                this.dialogOpened = true;
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

            deleteProject(projectIds) {
                this.$confirm('确认删除?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$ajax.delete(`/project`, {data: projectIds}).then(() => {
                        this.$message.success('删除成功。');
                        this.loadProjects(true);
                    })
                }).catch(err => err);
            },

            dialogCancel() {
                this.$refs.projectEdit.clearData();
                this.dialogOpened = false;
            },

            beforeDialogOpen() {
                this.$refs.projectEdit.preOpen();
            },

            dialogConfirm() {
                this.$refs.projectEdit.confirmEdit().then(() => {
                    this.project = {};
                    this.dialogOpened = false;
                    if ('新增' === this.dialogOpenMode) {
                        this.initPageForm()
                    }
                    this.loadProjects();
                });
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
