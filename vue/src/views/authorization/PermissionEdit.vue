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
            <el-form :model="permission" label-position="left" label-width="80px" size="mini" class="permission-edit-form"
                     :rules="rules" ref="editForm" :disabled="'查看' === openMode">
                <el-form-item label="名称" prop="permissionName"
                              :rules="{required: true, message: '请填写名称', trigger: 'blur'}">
                    <el-input v-model="permission.permissionName" placeholder="权限名称"></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input type="textarea" v-model="permission.permissionDescribe" placeholder="权限描述"></el-input>
                </el-form-item>
                <el-form-item label="类型">
                    <el-select v-model="permission.httpMethod" placeholder="请求方式" class="max-width-100 margin-right-xs">
                        <el-option v-for="m in httpMethods" :key="m" :label="m" :value="m"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="url">
                    <el-popover placement="top-start" title="匹配规则" width="500" trigger="hover">
                        <div>
                            <p>（1）? 匹配一个字符（除过操作系统默认的文件分隔符）</p>
                            <p>（2）* 匹配0个或多个字符</p>
                            <p>（3）**匹配0个或多个目录</p>
                            <p>（4）{\d+} 将正则表达式匹配\d+</p>
                        </div>
                        <i class="el-icon-question" slot="reference"></i>
                    </el-popover>
                    <el-input v-model="permission.url" placeholder="API路径，支持Ant风格"></el-input>
                </el-form-item>
                <el-form-item label="是否上线">
                    <el-switch v-model="permission.isOnline" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
                </el-form-item>
            </el-form>
        </el-main>
    </el-container>
</template>


<script>
    const HttpMethods = [
        'GET', 'POST', 'PUT', 'DELETE'
    ];

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
                httpMethods: HttpMethods,
            };
        },

        created() {
            pages.permissionEdit = this;
        },

        methods: {
            clearData() {
                // 初始化数据
                this.permission = {};
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
                return this.$ajax.post('/permission', permission);
            },

            doUpdatePermission(permission) {
                return this.$ajax.put(`/permission/${permission.permissionId}`, permission);
            },
        }
    }
</script>
