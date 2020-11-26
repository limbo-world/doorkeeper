<template>
    <el-container v-loading="loading" class="account-project-page">
        <el-main>
            <el-transfer
                :titles="['待选择', '已选择']" :button-texts="['取消绑定', '添加绑定']"
                filterable :filter-method="filterMethod" filter-placeholder="请输入项目名称"
                v-model="accountProjects" :data="projects">
            </el-transfer>
        </el-main>
    </el-container>
</template>


<script>
    export default {
        props: {
            account: {
                type: Object,
                default() {
                    return {
                        accountId: null,
                    }
                }
            }
        },

        data() {
            return {
                loading: true,
                projects: [],
                accountProjects: [],
                filterMethod(query, item) {
                    return item.label.indexOf(query) > -1;
                }
            };
        },

        created() {
            pages.projectEdit = this;
            this.loadProjects();
        },

        methods: {
            // 加载所有项目和账户已绑定项目
            loadProjects() {
                this.loading = true;
                Promise.all([
                    this.$ajax.get(`/admin-project`),
                    this.$ajax.get(`/admin-project/${this.account.accountId}`)
                ]).then(res => {
                    const loadedProjects = res[0].data;
                    const loadedAccountProjects = res[1].data;
                    let projects = [];
                    let accountProjects = [];
                    loadedProjects.forEach(project => {
                        projects.push({
                            label: project.projectName,
                            key: project.projectId,
                        })
                    });
                    loadedAccountProjects.forEach(project => {
                        accountProjects.push(project.projectId)
                    });
                    this.projects = projects;
                    this.accountProjects = accountProjects;
                }).finally(() => this.loading = false);
            },

            saveAccountProjects() {
                const loading = this.$loading();
                return new Promise((resolve, reject) => {
                    this.$ajax.put(`/admin-project/${this.account.accountId}`, {projectIds: this.accountProjects}).then(res => {
                        resolve(res);
                    }).catch(reject);
                }).finally(() => loading.close())
            }
        }
    }
</script>


<style lang="scss">

    .account-project-page {

        .el-main {
            padding: 0;

            .el-transfer {
                width: 100%;

                display: flex;
                justify-content: center;
                align-items: center;

                .el-transfer-panel {
                    flex: 1;
                }
            }

            .perm-policy-card {
                margin-top: 20px;
                width: 100%;
            }
        }
    }
</style>
