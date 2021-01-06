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
            <el-form :model="resource" label-width="120px" size="mini" class="edit-form" ref="editForm">
                <el-form-item label="名称" prop="name">
                    <el-input v-model="resource.name" :disabled="this.resource.resourceId"></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input type="textarea" v-model="resource.description"></el-input>
                </el-form-item>
                <el-form-item label="URI">
                    <el-row v-for="(uri, idx) in resource.uris">
                        <el-input v-model="uri.uri" style="max-width:700px;"></el-input>
                        <el-button @click="deleteUri(idx)" type="primary" size="mini" icon="el-icon-minus" circle></el-button>
                    </el-row>
                    <el-row>
                        <el-input v-model="uriString" style="max-width:700px;"></el-input>
                        <el-button @click="addUri" type="primary" size="mini" icon="el-icon-plus" circle></el-button>
                    </el-row>
                </el-form-item>
                <el-form-item label="标签">
                    注：标签名和标签值用 = 分割，如、颜色=color
                    <el-row>
                        <el-tag v-for="(tag, idx) in resource.tags" :key="idx" closable @close="deleteTag(idx)"
                                type="success" size="big" :disable-transitions="false">{{tag.k}}={{tag.v}}
                        </el-tag>
                        <el-input v-model="tagString" size="small" placeholder="输入标签名与标签值" style="width:300px;"
                                  @keyup.enter.native="addTag" @blur="addTag"></el-input>
                    </el-row>
                </el-form-item>
                <el-form-item label="是否启用">
                    <el-switch v-model="resource.isEnabled" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
                </el-form-item>
                <el-form-item>
                    <el-button v-if="!this.resource.resourceId" type="primary" @click="addResource" size="mini">新增</el-button>
                    <el-button v-if="this.resource.resourceId" type="primary" @click="updateRole" size="mini">保存</el-button>
                </el-form-item>
            </el-form>
        </el-main>
    </el-container>
</template>


<script>

    import { mapState, mapActions } from 'vuex';

    export default {
        data() {
            return {
                uriString: null,
                tagString: null,
                resource: {
                    uris: [],
                    tags: []
                }
            };
        },

        created() {
            pages.resourceEdit = this;
            this.resource.clientId = this.$route.query.clientId;
            if (this.$route.query.resourceId) {
                this.resource.resourceId = this.$route.query.resourceId;
                this.loadResource();
            }
        },

        methods: {
            ...mapActions('ui', ['startProgress', 'stopProgress']),

            // ========== uri相关 ==========
            addUri() {
                if (!this.uriString || this.uriString.length <= 0) {
                    return
                }
                // 检测是否已经存在
                for (let uri of this.resource.uris) {
                    if (uri.uri === this.uriString) {
                        return
                    }
                }
                this.resource.uris.push({uri: this.uriString})
                this.uriString = null
            },
            deleteUri(idx) {
                this.resource.uris.splice(idx, 1)
            },

            // ========== 标签相关 ==========
            addTag() {
                if (!this.tagString || this.tagString.indexOf("=") < 0) {
                    return
                }
                let split = this.tagString.split("=");
                let key = split[0];
                let value = split[1];
                if (key.length <= 0 || value <= 0) {
                    return
                }
                // 检测是否已经存在
                for (let tag of this.resource.tags) {
                    if (tag.k === key && tag.v === value) {
                        return
                    }
                }
                this.resource.tags.push({k: key, v: value})
                this.tagString = null
            },
            deleteTag(idx) {
                this.resource.tags.splice(idx, 1)
            },

            // ========== 资源相关 ==========
            loadResource() {
                this.startProgress({ speed: 'fast' });
                this.$ajax.get(`/admin/resource/${this.resource.resourceId}`).then(response => {
                    this.resource = response.data;
                }).finally(() => this.stopProgress());
            },
            addResource() {
                this.$ajax.post('/admin/resource', {...this.resource, addRealmId: true}).then(response => {
                    this.resource = response.data;
                    this.loadResource();
                })
            },
            updateResource() {
                return this.$ajax.put(`/account/${account.accountId}`, account);
            },
        }
    }
</script>
