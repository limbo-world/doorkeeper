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
            <el-form :model="role" label-width="80px" size="mini" class="edit-form" ref="editForm">
                <template>
                    <el-form-item label="名称">
                        <el-input v-model="role.name"></el-input>
                    </el-form-item>
                    <el-form-item label="描述">
                        <el-input type="textarea" v-model="role.description"></el-input>
                    </el-form-item>
                    <el-form-item label="默认角色">
                        <el-switch v-model="role.isDefault" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
                    </el-form-item>
                    <el-form-item label="是否启用">
                        <el-switch v-model="role.isEnabled" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
                    </el-form-item>
                </template>
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

        created() {
            pages.roleEdit = this;
        },

        methods: {
            confirmEdit() {
                const loading = this.$loading();
                return new Promise((resolve, reject) => {
                    if ('新增' === this.openMode) {
                        this.doAddRole().then(() => {
                            this.clearData();
                            resolve();
                        }).catch(reject=> {
                            console.log(reject)
                        });
                    } else if ('修改' === this.openMode) {
                        this.doUpdateRole().then(() => {
                            this.clearData();
                            resolve();
                        }).catch(reject);
                    } else {
                        reject()
                    }
                }).finally(() => loading.close())
            },

            doAddRole() {
                return this.$ajax.post('/admin/role', {...this.role, addRealmId: true})
            },

            doUpdateRole() {
                return this.$ajax.put(`/admin/role/${this.role.roleId}`, this.role);
            },

            clearData() {
                if (this.$refs.editForm) {
                    this.$refs.editForm.clearValidate();
                }
            }
        }
    }
</script>

<style lang="scss">
</style>
