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
    <el-container class="page-account-role-edit">
        <el-main>
            <el-transfer filterable filter-placeholder="搜索"
                         :titles="['未选', '已选']" @change="projectChange"
                         v-model="transferValue" :data="projects">
            </el-transfer>
        </el-main>
    </el-container>
</template>


<script>

    export default {
        props: {
            account: {
                type: Object,
                default: {}
            },
            openMode: {
                type: String,
                default: '',
            }
        },

        data() {

            return {
                projects: [],
                transferValue: []
            };
        },

        created() {
            pages.adminProjectEdit = this;
        },

        methods: {

            preOpen() {
                Promise.all([this.loadAllProject(), this.loadAdminProject()]).then((result) => {
                    const all = result[0].data;
                    const has = result[1].data;

                    all.forEach(a => {
                        a.label = a.projectName;
                        a.key = a.projectId;
                    });

                    this.projects = all;

                    if (has && has.length > 0) {
                        has.forEach(k => {
                            for (let project of this.projects) {
                                if (k.projectId === project.projectId) {
                                    project.accountProjectId = k.accountProjectId;
                                    this.transferValue.push(project.projectId);
                                    break
                                }
                            }
                        });
                    }
                    this.$forceUpdate();
                });
            },
            loadAllProject() {
                return this.$ajax.get('/project');
            },
            loadAdminProject() {
                return this.$ajax.get(`/admin-project`, {param: {accountId: this.account.accountId}});
            },

            projectChange(value, direction, movedKeys) { // value 右边剩余的key direction 方向 movedKeys 移动的key
                if ('left' === direction) {
                    movedKeys.forEach(k => {
                        for (let project of this.projects) {
                            if (k === project.projectId) {
                                project.delAccountProjectId = account.delAccountProjectId;
                            }
                        }
                    });
                } else {

                }
            },

            clearData() {
                this.projects = [];
                this.transferValue = [];
            },
            confirmEdit() {
                const loading = this.$loading();
                return new Promise((resolve, reject) => {
                    if ('修改' === this.openMode) {
                        this.doUpdate().then(() => {
                            this.clearData();
                            resolve();
                        }).catch(reject);
                    } else {
                        reject();
                    }
                }).finally(() => loading.close())
            },
            doUpdate() {
                // 找出apis 有permissionApiId但是不在已选框内的
                let delIds = [];
                let has = [];
                this.projects.forEach(project => {
                    if (project.delAccountProjectId) {
                        delIds.push(project.delAccountProjectId);
                    }
                    for (let k of this.transferValue) {
                        if (project.projectId === k) {
                            project.accountId = this.account.accountId;
                            has.push(project);
                            break
                        }
                    }
                });
                let param = {
                    addAccountProjects: has,
                    deleteAccountProjectIds: delIds
                };
                return this.$ajax.put(`/admin-project`, param);
            },
        }
    }
</script>

<style lang="scss">
    .page-account-role-edit {
        .el-transfer-panel {
            width: 300px;
            margin-right: 10px;
        }
    }
</style>
