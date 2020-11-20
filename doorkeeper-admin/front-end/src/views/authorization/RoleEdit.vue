<template>
    <el-container class="page-role-edit" v-loading="loading">
        <el-main>
            <el-form :model="role" label-width="80px" size="mini" class="edit-form"
                     :rules="rules" ref="editForm" :disabled="viewMode">
                <el-form-item label="名称" prop="roleName" :rules="{required: true, message: '请填写名称', trigger: 'blur'}">
                    <el-input v-model="role.roleName"></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input type="textarea" v-model="role.roleDesc"></el-input>
                </el-form-item>
                <el-form-item label="菜单列表" >
                    <menu-tree :menuCodeList="selectedMenuCode" :selectable="true" :searchable="true"
                               ref="menuTree"></menu-tree>
                </el-form-item>
                <el-form-item label="权限策略">
                    <el-button type="primary" icon="el-icon-plus" @click="addPermPolicyVisible = true">添加策略</el-button>
                </el-form-item>
                <el-card class="box-card">
                    <el-table :data="permPolicies" border size="mini">
                        <el-table-column label="权限编码" prop="permCode"></el-table-column>
                        <el-table-column label="权限名称" prop="permName"></el-table-column>
                        <el-table-column label="策略" align="center" width="200px">
                            <template slot-scope="scope">
                                <el-switch v-model="scope.row.allowed"
                                           active-color="#13ce66" inactive-color="#ff4949"
                                           active-text="允许" inactive-text="拒绝"></el-switch>
                            </template>
                        </el-table-column>
                        <el-table-column width="35px" v-if="!viewMode">
                            <template slot-scope="scope">
                                <i class="el-icon-delete pointer" @click="permPolicies.splice(scope.$index, 1)"></i>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-card>
            </el-form>
        </el-main>

        <el-dialog title="添加权限策略" :visible.sync="addPermPolicyVisible" :append-to-body="true" class="add-perm-policy-dialog">
            <el-row>
                <el-form label-width="80px" size="mini">
                    <el-form-item label="权限">
                        <el-input v-model="permPolicy.permSearchKeyword" class="max-width-200" suffix-icon="el-icon-search"></el-input>
                    </el-form-item>
                    <el-form-item>
                        <el-radio-group v-model="permPolicy.permCode">
                            <el-radio v-for="p in toAddPolicyPermissions" :key="p.permCode" :label="p.permCode"
                                      :disabled="permPolicyAvailable(p.permCode)">
                                {{p.permName}}
                            </el-radio>
                        </el-radio-group>
                    </el-form-item>
                    <el-form-item label="策略">
                        <el-switch v-model="permPolicy.allowed"
                                   active-color="#13ce66" inactive-color="#ff4949"
                                   active-text="允许" inactive-text="拒绝"></el-switch>
                    </el-form-item>
                </el-form>
            </el-row>

            <span slot="footer" class="dialog-footer">
                <el-button @click="addPermPolicyVisible = false">取 消</el-button>
                <el-button type="primary" @click="addPermPolicy">确 定</el-button>
            </span>
        </el-dialog>

    </el-container>
</template>


<script>

    import MenuTree from './MenuTree';

    export default {

        components: {
            MenuTree,
        },

        props: {
            role: {
                type: Object,
                default() {
                    return {
                        menuCodeList: []
                    }
                }
            },

            viewMode: {
                type: Boolean,
                default: true,
            }
        },

        data() {
            return {
                loading: true,

                // 权限
                permSearchKeyword: '',

                // 权限策略
                permissions: [],
                addPermPolicyVisible: false,
                permPolicy: {
                    permSearchKeyword: '',
                    permCode: '',
                    allowed: true,
                },
                permPolicies: [],
            };
        },

        computed: {
            selectedMenuCode() {
                return this.role.menus ? this.role.menus.map(m => m.menuCode) : [];
            },
            toAddPolicyPermissions() {
                const keyword = this.permPolicy.permSearchKeyword;
                if (!keyword) {
                    return this.permissions;
                } else {
                    return this.permissions.filter(p => p.permName.indexOf(keyword) >= 0);
                }
            }
        },

        watch: {
            role: {
                immediate: true,
                handler(newValue, oldValue) {
                    const newId = newValue && newValue.roleId;
                    const oldId = oldValue && oldValue.roleId;
                    if (oldValue == null || newId !== oldId) {
                        this.clearData();

                        this.loading = true;
                        this.loadPermissions().then(() => {
                            this.loading = true;
                            Promise.all([
                                this.$refs.menuTree.loadMenus(),
                                (newValue.roleId ? this.loadRole(newId) : this.$immediate()),
                            ]).then(() => this.loading = false);
                        }).finally(() => this.loading = false);
                    }
                }
            }
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

                        const $menuTree = this.$refs.menuTree;
                        role.menuCodeList = $menuTree.selectedMenuCodeList;
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
    .add-perm-policy-dialog {
        .el-radio {
            margin: 10px 30px 10px 0;
        }
    }
</style>
