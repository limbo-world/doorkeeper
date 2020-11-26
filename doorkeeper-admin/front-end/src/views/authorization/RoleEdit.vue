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
                    <!--<el-input type="textarea" v-model="role.roleDescribe"></el-input>-->
                </el-form-item>
                <el-form-item label="权限">
                    <el-transfer filterable filter-placeholder="搜索" v-model="transferValue" :data="permissions">
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

            // 需要加载角色详细信息，将角色拥有的菜单也加载到
            loadRole(roleId) {
                this.loading = true;
                this.$ajax.get(`/role/${roleId}`).then(response => {
                    this.role = response.data;
                    this.permPolicies = this.role.permPolicies.map(p => {
                        return {
                            permCode: p.permCode,
                            permName: this.permissions.find(pm => pm.permCode === p.permCode).permName,
                            allowed: p.policy === 'ALLOWED',
                        }
                    });
                }).finally(() => this.loading = false);
            },

            loadPermissions() {
                return this.$ajax.get(`/permission`).then(response => {
                    this.permissions = response.data;
                });
            },

            permPolicyAvailable(permCode) {
                return this.permPolicies.findIndex(p => p.permCode === permCode) >= 0;
            },

            addPermPolicy() {
                const idx = this.permissions.findIndex(p => p.permCode === this.permPolicy.permCode);
                const perm = this.permissions[idx];
                this.permPolicies.push({
                    permCode: this.permPolicy.permCode,
                    allowed: this.permPolicy.allowed,
                    permName: perm.permName
                });
                this.permPolicy.permSearchKeyword = '';
                this.permPolicy.permCode = '';
                this.permPolicy.allowed = true;
                this.addPermPolicyVisible = false;
            },

            saveRole() {
                if (this.viewMode) {
                    return this.$immediate();
                }

                const loading = this.$loading();
                return new Promise((resolve, reject) => {
                    this.$refs.editForm.validate(valid => {
                        if (!valid) {
                            reject();
                            return;
                        }

                        const role = JSON.parse(JSON.stringify(this.role));
                        delete role.menus;
                        delete role.permissions;
                        delete role.accounts;

                        role.permPolicies = this.permPolicies.map(p => {
                            return {
                                permCode: p.permCode,
                                policy: p.allowed ? 'ALLOWED' : 'REFUSED',
                            }
                        });

                        const prom = role.roleId
                            ? this.doUpdateRole(role)
                            : this.doAddRole(role);
                        prom.then(() => {
                            // 保存成功，清理数据
                            this.clearData();
                            resolve();
                        }).catch(reject);
                    });
                }).finally(() => loading.close());
            },

            doAddRole(role) {
                return this.$ajax.post('/role', role);
            },

            doUpdateRole(role) {
                return this.$ajax.put(`/role/${role.roleId}`, role);
            },

            clearData() {
                this.permPolicies = [];
                if (this.$refs.editForm) {
                    this.$refs.editForm.clearValidate();
                }
            }
        }
    }
</script>


<style lang="scss">
    .page-role-edit {
        .add-perm-policy-dialog {
            .el-radio {
                margin: 10px 30px 10px 0;
            }
        }
    }
</style>
