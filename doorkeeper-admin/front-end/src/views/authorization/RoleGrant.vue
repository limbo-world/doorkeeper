<template>
    <el-container class="role-grant-page">
        <el-main>
            <div class="flex justify-center">
                <el-transfer v-model="selectedAccountIds" :data="accounts"
                             filterable :filter-method="searchAccount" filter-placeholder="输入用户昵称"
                             :titles="['用户列表', '已授权用户']" :button-texts="['取消授权', '添加授权']"
                             :props="{ key: 'accountId', label: 'nick' }" style="width: 555px;">
                </el-transfer>
            </div>
        </el-main>
    </el-container>
</template>

<script>
    export default {
        props: {
            role: {
                type: Object,
                default() {
                    return {};
                }
            }
        },

        data() {
            return {
                accounts: [],

                selectedAccountIds: [],

                searchAccount(query, account) {
                    return account.nick.indexOf(query) > -1;
                },
            }
        },

        watch: {
            role: {
                immediate: true,
                handler(newValue, oldValue) {
                    (this.accounts.length !== 0 ? this.$immediate() : this.loadAccounts()).then(() => {
                        const newId = newValue ? newValue.roleId : null;
                        const oldId = oldValue ? oldValue.roleId : null;
                        if (newId !== oldId && newValue.roleId != null) {
                            this.loadRole();
                        }
                    });
                }
            }
        },


        created() {
            pages.roleGrant = this;
        },

        methods: {
            loadRole(){
                this.$ajax.get(`/role/${this.role.roleId}`).then(response => {
                    this.role = response.data;
                    this.selectedAccountIds = this.role.accounts ? this.role.accounts.map(a => a.accountId) : [];
                });
            },

            loadAccounts() {
                return this.$ajax.get('/account').then(response => {
                    this.accounts = response.data;
                });
            },



            clearData() {
                this.accounts = [];
                this.selectedAccountIds = new Set();
            },

            saveGrant() {
                const loading = this.$loading();
                return new Promise((resolve, reject) => {
                    this.$ajax.put(`/role/${this.role.roleId}/grant`, {
                        accountIds: this.selectedAccountIds
                    }).then(resolve).catch(reject);
                }).finally(() => loading.close());
            },
        }
    }
</script>


<style lang="scss">
    .role-grant-page {
        .el-main, .el-footer {
            padding: 0;
        }

        .el-footer {
            margin-top: 20px;
        }
    }
</style>
