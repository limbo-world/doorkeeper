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
    <el-container class="page-group-edit">
        <el-main>
            <el-form :model="group" label-width="80px" size="mini" class="edit-form" ref="editForm">
                <el-form-item label="名称">
                    <el-input v-model="group.name" disabled></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input type="textarea" v-model="group.description"></el-input>
                </el-form-item>
                <el-form-item label="默认添加">
                    <el-switch v-model="group.isDefault" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="updateGroup" size="mini" icon="el-icon-refresh">保存</el-button>
                </el-form-item>
            </el-form>
        </el-main>
    </el-container>
</template>


<script>

import { mapState, mapActions } from 'vuex';

export default {
    props: {
        groupId: {
            type: Number,
            default: null
        }
    },

    data: function () {
        return {
            group: {},
        }
    },

    computed: {
        ...mapState('session', ['user']),
    },

    created() {
        pages.groupEdit = this;

        this.loadGroup();
    },

    methods: {
        ...mapActions('ui', ['startProgress', 'stopProgress']),

        loadGroup() {
            this.startProgress({ speed: 'fast' });
            this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/group/${this.groupId}`).then(response => {
                this.group = response.data;
            }).finally(() => this.stopProgress());
        },

        updateGroup() {
            this.$ajax.put(`/admin/realm/${this.user.realm.realmId}/group/${this.groupId}`, this.group).then(response => {
                this.loadGroup()
            })
        },
    }
}
</script>

<style lang="scss">
</style>
