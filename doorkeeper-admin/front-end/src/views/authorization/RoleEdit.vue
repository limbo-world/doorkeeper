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
    <el-container class="page-role-edit">
        <el-main>
            <el-form :model="role" label-width="80px" size="mini" class="edit-form"
                     :rules="rules" ref="editForm" :disabled="'查看' === openMode">
                <el-form-item label="名称" prop="roleName" :rules="{required: true, message: '请填写名称', trigger: 'blur'}">
                    <el-input v-model="role.roleName"></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input type="textarea" v-model="role.roleDescribe"></el-input>
                </el-form-item>
                <el-form-item label="默认角色">
                    <el-switch v-model="role.isDefault" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
                </el-form-item>
                <el-form-item label="权限">
                    <el-transfer filterable filter-placeholder="搜索"
                                 :titles="['未选', '已选']" :render-content="renderFunc"
                                 @left-check-change="leftCheckChange" @right-check-change="rightCheckChange"
                                 v-model="transferValue" :data="permissions">
                        <el-button class="transfer-footer" slot="left-footer" size="small" @click="allowPermission">放行</el-button>
                        <el-button class="transfer-footer" slot="left-footer" size="small" @click="refusePermission">拦截</el-button>
                        <el-button class="transfer-footer" slot="right-footer" size="small" @click="deletePermission">删除</el-button>
                    </el-transfer>
                </el-form-item>
            </el-form>
        </el-main>
    </el-container>
</template>


<script>

    export default {
        props: {
            role: {
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
                permissions: [],
                transferValue: [],
                hasPermissions: [],
                leftSelect: [],
                rightSelect: [],
            };
        },

        created() {
            pages.roleEdit = this;
        },

        methods: {
            preOpen() {
                Promise.all([this.loadAllPermission(), this.loadRolePermission()]).then((result) => {
                    const allPermission = result[0].data;
                    const hasPermission = result[1].data;

                    allPermission.forEach(permission => {
                        permission.label = permission.permissionName;
                        permission.key = permission.permissionId;
                    });

                    this.permissions = allPermission;

                    if (hasPermission && hasPermission.length > 0) {
                        hasPermission.forEach(k => {
                            for (let permission of this.permissions) {
                                if (k.permissionId === permission.permissionId) {
                                    permission.policy = k.policy;
                                    permission.rolePermissionId = k.rolePermissionId;
                                    this.hasPermissions.push(permission);
                                    this.transferValue.push(permission.permissionId);
                                    break
                                }
                            }
                        });
                    }
                    this.$forceUpdate();
                });
            },
            loadAllPermission() {
                return this.$ajax.get('/permission');
            },
            loadRolePermission() {
                if (!this.role.roleId) {
                    return new Promise((resolve, reject) => {
                        resolve({data: [], code: 200})
                    })
                }
                return this.$ajax.get('/role-permission', {params: {roleId: this.role.roleId}});
            },
            renderFunc(h, option) {
                if ('allow' === option.policy) {
                    return <span style='color: green;'>{option.label}</span>;
                } else if ('refuse' === option.policy) {
                    return <span style='color: red;'>{option.label}</span>;
                } else {
                    return <span>{option.label}</span>;
                }
            },
            leftCheckChange(keys, key) {
                this.leftSelect = keys;
            },
            rightCheckChange(keys, key) {
                this.rightSelect = keys;
            },
            allowPermission() {
                this.leftSelect.forEach(k => {
                    for (let permission of this.permissions) {
                        if (k === permission.permissionId) {
                            permission.policy = 'allow';
                            this.hasPermissions.push(permission);
                            this.transferValue.push(permission.permissionId);
                            break
                        }
                    }
                });
                this.$forceUpdate();
            },
            refusePermission() {
                this.leftSelect.forEach(k => {
                    for (let permission of this.permissions) {
                        if (k === permission.permissionId) {
                            permission.policy = 'refuse';
                            this.hasPermissions.push(permission);
                            this.transferValue.push(permission.permissionId);
                            break
                        }
                    }
                });
                this.$forceUpdate();
            },
            deletePermission() {
                this.rightSelect.forEach(k => {
                    for (let idx in this.hasPermissions) {
                        let permission = this.hasPermissions[idx];
                        if (k === permission.permissionId) {
                            // 往左边删除的时候，如果有 permissonApiId 需要清空
                            permission.delRolePermissionId = permission.rolePermissionId
                            delete permission.rolePermissionId;
                            delete permission.policy;
                            this.hasPermissions.splice(idx, 1);
                            this.transferValue.splice(idx, 1);
                            break
                        }
                    }
                });
                this.$forceUpdate();
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
                        } else {
                            reject();
                        }
                    });
                }).finally(() => loading.close())
            },

            doAddRole(role) {
                role.rolePermissions = this.hasPermissions;
                return this.$ajax.post('/role', role);
            },

            doUpdateRole(role) {
                let delIds = [];
                this.permissions.forEach(permission => {
                    if (permission.delRolePermissionId) {
                        delIds.push(permission.delRolePermissionId);
                    }
                });
                role.addRolePermissions = this.hasPermissions;
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
    .page-role-edit {
        .el-transfer-panel {
            width: 300px;
            margin-right: 10px;
            .el-transfer-panel__item.el-checkbox {
                margin-left:0;
                display: block;
            }
        }
        .el-transfer {
            .el-transfer__buttons {
                display: none;
            }
        }
    }
</style>
