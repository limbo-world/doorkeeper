<template>
    <el-container class="account-grant-page" v-loading="loading">
        <el-main>
            <!-- 授权角色 -->
            <div class="flex justify-center">
                <el-transfer v-model="selectedRoleIds" :data="roles"
                             filterable :filter-method="searchRole" filter-placeholder="输入角色名称"
                             :titles="['角色列表', '已授权角色']" :button-texts="['取消授权', '添加授权']"
                             :props="{ key: 'roleId', label: 'roleName' }">
                </el-transfer>
            </div>

            <!-- 权限策略 -->
            <el-row class="margin-top">
                <el-button type="primary" size="mini" @click="openAddPermPolicyDialog">新增策略</el-button>
            </el-row>
            <div class="flex justify-center margin-top-xs">
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
                    <el-table-column width="35px">
                        <template slot-scope="scope">
                            <i class="el-icon-delete pointer" @click="permPolicies.splice(scope.$index, 1)"></i>
                        </template>
                    </el-table-column>
                </el-table>
            </div>
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

    export default {
        props: {
            accountId: {
                type: Number,
                default: null,
            }
        },

        watch: {
            accountId: {
                immediate: true,
                handler(newValue, oldValue) {
                    this.loading = true;
                    Promise.all([
                        this.roles.length !== 0 ? this.$immediate() : this.loadRoles(),
                        this.permissions.length !== 0 ? this.$immediate() : this.loadPermissions(),
                    ]).then(() => {
                        if (newValue !== oldValue && newValue != null) {
                            this.loadAccount(newValue);
                        }
                    });
                    return newValue;
                }
            }
        },

        data() {
            return {
                roles: [],
                account: {},

                loading: true,
                selectedRoleIds: [],

                // 权限策略
                addPermPolicyVisible: false,
                permissions: [],
                permPolicy: {
                    permSearchKeyword: '',
                    permCode: '',
                    allowed: true,
                },
                permPolicies: [],
            };
        },

        computed: {
            // 选择权限策略时，只能选择没有被添加过的权限
            toAddPolicyPermissions() {
                const keyword = this.permPolicy.permSearchKeyword;
                if (!keyword) {
                    return this.permissions;
                } else {
                    return this.permissions.filter(p => p.permName.indexOf(keyword) >= 0);
                }
            }
        },

        created() {
            pages.accountGrant = this;
        },

        methods: {

            loadRoles() {
                return this.$ajax.get('/role').then(response => {
                    this.roles = response.data;
                });
            },

            loadAccount(accountId) {
                accountId = accountId == null ? this.accountId : accountId;
                this.$ajax.get(`/account/${this.accountId}`).then(response => {
                    this.account = response.data;
                    this.selectedRoleIds = this.account.roles ? this.account.roles.map(r => r.roleId) : [];
                    this.permPolicies = this.account.permPolicies.map(p => {
                        return {
                            permCode: p.permCode,
                            permName: this.permissions.find(pm => pm.permCode === p.permCode).permName,
                            allowed: p.policy === 'ALLOWED',
                        }
                    });
                    this.loading = false
                });
            },

            saveGrant() {
                const accountId = this.account.accountId;
                const param = { accountId };
                param.roleIds = this.selectedRoleIds;
                param.permPolicies = this.permPolicies.map(p => {
                    return {
                        permCode: p.permCode,
                        policy: p.allowed ? 'ALLOWED' : 'REFUSED',
                    }
                });

                return this.$ajax.put(`/account/${accountId}/grant`, param);
            },

            clearData() {
                this.account = null;
                this.accountId = null;
                this.selectedRoleIds = [];
            },

            // ------------------ 账户权限策略相关

            openAddPermPolicyDialog() {
                (this.permissions.length > 0 ? this.$immediate() : this.loadPermissions()).then(() => {
                    this.addPermPolicyVisible = true;
                })
            },

            loadPermissions() {
                this.loading = true;
                return this.$ajax.get(`/permission`).then(response => {
                    this.permissions = response.data;
                }).finally(() => this.loading = false);
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
        }
    }
</script>



<style lang="scss">
    .account-grant-page {

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

    .add-perm-policy-dialog {
        .el-radio {
            margin: 10px 30px 10px 0;
        }
    }
</style>
