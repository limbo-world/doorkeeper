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
            <el-form :model="api" label-width="80px" size="mini" class="edit-form"
                     :rules="rules" ref="editForm">
                <el-form-item label="名称" prop="apiName" :rules="{required: true, message: '请填写名称', trigger: 'blur'}">
                    <el-input v-model="api.apiName" placeholder="api名称"></el-input>
                </el-form-item>
                <el-form-item label="类型" v-if="addMode">
                    <el-select v-model="api.apiMethod" placeholder="请求方式" class="max-width-100 margin-right-xs">
                        <el-option v-for="m in httpMethods" :key="m" :label="m" :value="m"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="url" v-if="addMode">
                    <el-input v-model="api.apiUrl" placeholder="API路径，支持Ant风格"></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input type="textarea" v-model="api.apiDescribe" placeholder="描述"></el-input>
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
            api: {
                type: Object,
                default: {}
            },
            addMode: {
                type: Boolean,
                default: false,
            }
        },

        data() {
            return {
                httpMethods: HttpMethods,
                api: {}
            };
        },

        created() {
            pages.apiEdit = this;
        },

        methods: {
            confirmEdit() {
                const loading = this.$loading();
                return new Promise((resolve, reject) => {
                    this.$refs.editForm.validate(valid => {
                        if (!valid) {
                            reject();
                            return;
                        }

                        const api = JSON.parse(JSON.stringify(this.api));
                        const prom = this.addMode ? this.doAddApi(api) : this.doUpdateApi(api);
                        prom.then(() => {
                            this.clearData();
                            resolve();
                        }).catch(reject);
                    });
                }).finally(() => loading.close())
            },

            clearData() {
                this.api = {};
                if (this.$refs.editForm) {
                    this.$refs.editForm.clearValidate();
                }
            },

            doAddApi(api) {
                return this.$ajax.post('/api', api);
            },

            doUpdateApi(api) {
                return this.$ajax.put(`/api/${api.apiId}`, api);
            },
        }
    }
</script>
