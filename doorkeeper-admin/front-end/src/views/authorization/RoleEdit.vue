<template>
    <el-container class="page-role-edit" v-loading="loading">
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
                                 :titles="['未选', '已选']" @change="permissionChange"
                                 v-model="transferValue" :data="permissions">
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
                default() {}
            },

            openMode: {
                type: Boolean,
                default: true,
            }
        },

        data() {
            return {
                permissions: [],
                transferValue: []
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
                                    permission.rolePermissionId = k.rolePermissionId
                                    this.transferValue.push(permission.permissionId);
                                    break
                                }
                            }
                        });
                    }
                    this.$forceUpdate();
                })
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

            permissionChange(value, direction, movedKeys) { // value 右边剩余的key direction 方向 movedKeys 移动的key
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
    .page-role-edit {
        .el-transfer-panel {
            width: 300px;
            margin-right: 10px;
        }
    }
</style>
