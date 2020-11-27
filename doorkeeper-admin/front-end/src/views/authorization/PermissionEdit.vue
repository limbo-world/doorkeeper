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
    <el-container>
        <el-main>
            <el-form :model="permission" label-position="left" label-width="80px" size="mini" class="edit-form"
                     :rules="rules" ref="editForm" :disabled="'查看' === openMode">
                <el-form-item label="名称" prop="permissionName"
                              :rules="{required: true, message: '请填写名称', trigger: 'blur'}">
                    <el-input v-model="permission.permissionName" placeholder="权限名称"></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input type="textarea" v-model="permission.permissionDescribe" placeholder="权限描述"></el-input>
                </el-form-item>
                <el-form-item label="API列表">
                    <el-transfer filterable filter-placeholder="搜索"
                                 :titles="['未选', '已选']" :render-content="renderFunc"
                                 @left-check-change="leftCheckChange" @right-check-change="rightCheckChange"
                                 v-model="transferValue" :data="apis">
                        <el-button class="transfer-footer" slot="left-footer" size="small" @click="allowApi">放行</el-button>
                        <el-button class="transfer-footer" slot="left-footer" size="small" @click="refuseApi">拦截</el-button>
                        <el-button class="transfer-footer" slot="right-footer" size="small" @click="deleteApi">删除</el-button>
                    </el-transfer>
                </el-form-item>
            </el-form>
        </el-main>
    </el-container>
</template>


<script>
    export default {
        props: {
            permission: {
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
                apis: [],
                hasApis: [],
                leftSelect: [],
                rightSelect: [],
                transferValue: [],
            };
        },

        created() {
            pages.permissionEdit = this;
        },

        methods: {

            preOpen() {
                Promise.all([this.loadAllApi(), this.loadPermissionApi()]).then((result) => {
                    const allApi = result[0].data;
                    const hasApi = result[1].data;

                    allApi.forEach(api => {
                        api.label = api.apiName;
                        api.key = api.apiId;
                    });

                    this.apis = allApi;

                    // 如果传递了权限ID 查询对应权限的所以已有的api
                    if (hasApi && hasApi.length > 0) {
                        hasApi.forEach(k => {
                            for (let api of this.apis) {
                                if (k.apiId === api.apiId) {
                                    api.policy = k.policy;
                                    api.permissionApiId = k.permissionApiId
                                    this.hasApis.push(api);
                                    this.transferValue.push(api.apiId);
                                    break
                                }
                            }
                        });
                    }
                    this.$forceUpdate();
                });

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
            allowApi() {
                this.leftSelect.forEach(k => {
                    for (let api of this.apis) {
                        if (k === api.apiId) {
                            api.policy = 'allow';
                            this.hasApis.push(api);
                            this.transferValue.push(api.apiId);
                            break
                        }
                    }
                });
                this.$forceUpdate();
            },
            refuseApi() {
                this.leftSelect.forEach(k => {
                    for (let api of this.apis) {
                        if (k === api.apiId) {
                            api.policy = 'refuse';
                            this.hasApis.push(api);
                            this.transferValue.push(api.apiId);
                            break
                        }
                    }
                });
                this.$forceUpdate();
            },
            deleteApi() {
                this.rightSelect.forEach(k => {
                    for (let idx in this.hasApis) {
                        let api = this.hasApis[idx];
                        if (k === api.apiId) {
                            // 往左边删除的时候，如果有 permissonApiId 需要清空
                            api.delPermissionApiId = api.permissionApiId
                            delete api.permissionApiId;
                            delete api.policy;
                            this.hasApis.splice(idx, 1);
                            this.transferValue.splice(idx, 1);
                            break
                        }
                    }
                });
                this.$forceUpdate();
            },
            loadAllApi() {
                return this.$ajax.get('/api');
            },
            loadPermissionApi() {
                if (!this.permission.permissionId) {
                    return new Promise((resolve, reject) => {
                        resolve({data: [], code: 200})
                    })
                }
                return this.$ajax.get('/permission-api', {params: {permissionId: this.permission.permissionId}});
            },


            clearData() {
                // 初始化数据
                this.permission = {};
                this.apis = [];
                this.hasApis = [];
                this.leftSelect = [];
                this.rightSelect = [];
                this.transferValue = [];

                if (this.$refs.editForm) {
                    this.$refs.editForm.clearValidate();
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
                            this.doAddPermission(this.permission).then(() => {
                                this.clearData();
                                resolve();
                            }).catch(reject);
                        } else if ('修改' === this.openMode) {
                            this.doUpdatePermission(this.permission).then(() => {
                                this.clearData();
                                resolve();
                            }).catch(reject);
                        } else {
                            reject();
                        }
                    });
                }).finally(() => loading.close())
            },
            doAddPermission(permission) {
                permission.permissionApis = this.hasApis;
                return this.$ajax.post('/permission', permission);
            },

            doUpdatePermission(permission) {
                // 找出apis 有permissionApiId但是不在已选框内的
                let delIds = [];
                this.apis.forEach(api => {
                    if (api.delPermissionApiId) {
                        delIds.push(api.delPermissionApiId);
                    }
                });
                permission.addPermissionApis = this.hasApis;
                permission.deletePermissionApiIds = delIds;
                return this.$ajax.put(`/permission/${permission.permissionId}`, permission);
            },
        }
    }
</script>

<style lang="scss">
    .edit-form {
        .el-transfer-panel {
            width: 350px;
            margin-right: 10px;
            .el-transfer-panel__item.el-checkbox{
                margin-left:0;
                display: block;
            }
        }
        .el-transfer__buttons {
            display: none;
        }
    }
</style>
