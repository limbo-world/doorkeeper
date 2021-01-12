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
                <el-form-item label="名称">
                    <el-input v-model="role.name" disabled></el-input>
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
                <el-form-item>
                    <el-button type="primary" @click="updateRole" size="mini" icon="el-icon-refresh">保存</el-button>
                </el-form-item>
            </el-form>
        </el-main>
    </el-container>
</template>


<script>

import { mapState, mapActions } from 'vuex';

export default {
    props: {
        roleId: {
            type: Number,
            default: null
        },
        clientId: {
            type: Number,
            default: 0
        },
    },

    data: function () {
        return {
            role: {},
        }
    },

    computed: {
        ...mapState('session', ['realm']),
    },

    created() {
        pages.roleEdit = this;

        this.loadRole();
    },

    methods: {
        ...mapActions('ui', ['startProgress', 'stopProgress']),

        loadRole() {
            this.startProgress({ speed: 'fast' });
            this.$ajax.get(`/admin/realm/${this.realm.realmId}/client/${this.clientId}/role/${this.roleId}`).then(response => {
                this.role = response.data;
            }).finally(() => this.stopProgress());
        },

        updateRole() {
            this.$ajax.put(`/admin/realm/${this.realm.realmId}/client/${this.clientId}/role/${this.role.roleId}`, this.role).then(response => {
                this.loadRole()
            })
        },
    }
}
</script>

<style lang="scss">
</style>
