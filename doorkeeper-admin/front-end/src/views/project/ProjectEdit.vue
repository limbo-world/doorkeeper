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
    <el-container class="page-project-edit" v-loading="loading">
        <el-main>
            <el-form :model="project" label-width="80px" size="mini" class="edit-form"
                     :rules="rules" ref="editForm" :disabled="'查看' === openMode">
                <el-form-item label="名称" prop="projectName" :rules="{required: true, message: '请填写名称', trigger: 'blur'}">
                    <el-input v-model="project.projectName" placeholder="项目名称"></el-input>
                </el-form-item>
                <el-form-item label="秘钥" prop="projectSecret" :rules="{required: true, message: '请填写秘钥', trigger: 'blur'}">
                    <el-input v-model="project.projectSecret" placeholder="秘钥"></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input type="textarea" v-model="project.projectDescribe"></el-input>
                </el-form-item>
                <el-form-item label="账号">
                    <el-transfer filterable filter-placeholder="搜索"
                                 :titles="['未选', '已选']" @change="accountChange"
                                 v-model="transferValue" :data="accounts">
                    </el-transfer>
                </el-form-item>
            </el-form>
        </el-main>
    </el-container>
</template>


<script>

    export default {
        props: {
            project: {
                type: Object,
                default() {}
            },

            openMode: {
                type: Boolean,
                default: true,
            }
        },

        data() {
            return {
                accounts: [],
                transferValue: []
            };
        },

        created() {
            pages.projectEdit = this;
        },

        methods: {
            preOpen() {
                Promise.all([this.loadAllAccount(), this.loadAccountProject()]).then((result) => {
                    const allAccount = result[0].data;
                    const hasAccount = result[1].data;

                    allAccount.forEach(account => {
                        account.label = account.username;
                        account.key = account.accountId;
                    });

                    this.accounts = allAccount;

                    if (hasAccount && hasAccount.length > 0) {
                        hasAccount.forEach(k => {
                            for (let account of this.accounts) {
                                if (k.accountId === account.accountId) {
                                    permission.accountProjectId = k.accountProjectId
                                    this.transferValue.push(account.accountId);
                                    break
                                }
                            }
                        });
                    }
                    this.$forceUpdate();
                })
            },
            loadAllAccount() {
                return this.$ajax.get('/account');
            },
            loadAccountProject() {
                if (!this.role.roleId) {
                    return new Promise((resolve, reject) => {
                        resolve({data: [], code: 200})
                    })
                }
                return this.$ajax.get('/account-project', {params: {project: this.project.projectId}});
            },

            accountChange(value, direction, movedKeys) { // value 右边剩余的key direction 方向 movedKeys 移动的key
                if ('left' === direction) {
                    movedKeys.forEach(k => {
                        for (let permission of this.permissions) {
                            if (k === permission.permissionId) {
                                permission.delRolePermissionId = permission.rolePermissionId;
                            }
                        }
                    });
                } else {

                }
            },

            confirmEdit() {
                const loading = this.$loading();
                return new Promise((resolve, reject) => {
                    this.$refs.editForm.validate(valid => {
                        if (!valid) {
                            reject();
                            return;
                        }
                        if ('新增' === this.openMode) {
                            this.doAddRole(this.role).then(() => {
                                this.clearData();
                                resolve();
                            }).catch(reject);
                        } else if ('修改' === this.openMode) {
                            this.doUpdateRole(this.role).then(() => {
                                this.clearData();
                                resolve();
                            }).catch(reject);
                        }
                    });
                }).finally(() => loading.close())
            },

            doAddRole(role) {
                let rolePermissions = [];
                this.transferValue.forEach(k => {
                    for (let permission of this.permissions) {
                        if (k === permission.permissionId) {
                            rolePermissions.push(permission);
                            break;
                        }
                    }
                });
                role.rolePermissions = rolePermissions;
                return this.$ajax.post('/role', role);
            },

            doUpdateRole(role) {
                // 找出apis 有permissionApiId但是不在已选框内的
                let delIds = [];
                let hasPermissions = [];
                this.permissions.forEach(permission => {
                    if (permission.delRolePermissionId) {
                        delIds.push(permission.delRolePermissionId);
                    }
                    for (let k of this.transferValue) {
                        if (permission.permissionId === k) {
                            hasPermissions.push(permission);
                            break
                        }
                    }
                });
                role.addRolePermissions = hasPermissions;
                role.deleteRolePermissionIds = delIds;
                return this.$ajax.put(`/role/${role.roleId}`, role);
            },

            clearData() {
                this.permissions = [];
                this.transferValue = [];
                if (this.$refs.editForm) {
                    this.$refs.editForm.clearValidate();
                }
            }
        }
    }
</script>


<style lang="scss">
    .page-project-edit {
        .el-transfer-panel {
            width: 300px;
            margin-right: 10px;
        }
    }
</style>
