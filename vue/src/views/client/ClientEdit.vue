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
    <el-container class="page-client-edit">
        <el-main>
            <el-form :model="client" label-width="80px" size="mini" class="edit-form" ref="editForm">
                <el-form-item label="名称">
                    <el-input v-model="client.name" disabled></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input type="textarea" v-model="client.description"></el-input>
                </el-form-item>
                <el-form-item label="是否启用">
                    <el-switch v-model="client.isEnabled" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="updateClient" size="mini" icon="el-icon-refresh">保存</el-button>
                </el-form-item>
            </el-form>
        </el-main>
    </el-container>
</template>


<script>

import { mapState, mapActions } from 'vuex';

export default {
    props: {
        clientId: {
            type: Number,
            default: null
        },
    },

    data: function () {
        return {
            client: {},
        }
    },

    computed: {
        ...mapState('session', ['realm']),
    },

    created() {
        pages.clientEdit = this;

        this.loadClient();
    },

    methods: {
        ...mapActions('ui', ['startProgress', 'stopProgress']),

        loadClient() {
            this.startProgress({ speed: 'fast' });
            this.$ajax.get(`/admin/realm/${this.realm.realmId}/client/${this.clientId}`).then(response => {
                this.client = response.data;
            }).finally(() => this.stopProgress());
        },

        updateClient() {
            this.$ajax.put(`/admin/realm/${this.realm.realmId}/client/${this.clientId}`, this.client).then(response => {
                this.loadClient()
            })
        },
    }
}
</script>

<style lang="scss">
</style>
