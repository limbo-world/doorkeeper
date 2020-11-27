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
                         :titles="['未选', '已选']" @change="roleChange"
                         v-model="transferValue" :data="roles">
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
                roles: [],
                transferValue: []
            };
        },

        created() {
            pages.accountEdit = this;
        },

        methods: {

            preOpen() {
                Promise.all([this.loadAllRole(), this.loadAccountRole()]).then((result) => {
                    const all = result[0].data;
                    const has = result[1].data;

                    all.forEach(a => {
                        a.label = a.roleName;
                        a.key = a.roleId;
                    });

                    this.roles = all;

                    if (has && has.length > 0) {
                        has.forEach(k => {
                            for (let role of this.roles) {
                                if (k.roleId === role.roleId) {
                                    role.accountRoleId = k.accountRoleId;
                                    this.transferValue.push(role.roleId);
                                    break
                                }
                            }
                        });
                    }
                    this.$forceUpdate();
                });
            },
            loadAllRole() {
                return this.$ajax.get('/role');
            },
            loadAccountRole() {
                return this.$ajax.get('/account-role', {params: {accountId: this.account.accountId}});
            },

            roleChange(value, direction, movedKeys) { // value 右边剩余的key direction 方向 movedKeys 移动的key
                if ('left' === direction) {
                    movedKeys.forEach(k => {
                        for (let role of this.roles) {
                            if (k === role.accountId) {
                                role.delAccountRoleId = account.delAccountRoleId;
                            }
                        }
                    });
                } else {

                }
            },

            clearData() {
                this.roles = [];
                this.transferValue = [];
            },
            confirmEdit() {
                const loading = this.$loading();
                return new Promise((resolve, reject) => {
                    if ('修改' === this.openMode) {
                        this.doUpdateRole().then(() => {
                            this.clearData();
                            resolve();
                        }).catch(reject);
                    } else {
                        reject();
                    }
                }).finally(() => loading.close())
            },
            doUpdateRole() {
                // 找出apis 有permissionApiId但是不在已选框内的
                let delIds = [];
                let has = [];
                this.roles.forEach(role => {
                    if (role.delAccountRoleId) {
                        delIds.push(role.delAccountRoleId);
                    }
                    for (let k of this.transferValue) {
                        if (role.roleId === k) {
                            role.accountId = this.account.accountId;
                            has.push(role);
                            break
                        }
                    }
                });
                let param = {
                    addAccountRoles: has,
                    deleteRolePermissionIds: delIds
                };
                return this.$ajax.put(`/account-role`, param);
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
