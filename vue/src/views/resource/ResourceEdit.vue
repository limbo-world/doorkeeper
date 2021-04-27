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
    <el-container class="resource-edit-page">
        <el-main>
            <el-form :model="resource" label-width="120px" size="mini" class="edit-form" ref="editForm">
                <el-form-item label="名称" prop="name">
                    <el-input v-model="resource.name"></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input type="textarea" v-model="resource.description"></el-input>
                </el-form-item>
                <el-form-item label="URI">
                    <el-tooltip class="item" effect="dark" placement="left">
                        <div slot="content">
                            <p>Uri 支持Ant风格</p>
                            <p>1）?：匹配任何单字符</p>
                            <p>2）*：匹配0或者任意数量的字符</p>
                            <p>3）**：匹配0或者更多的目录</p>
                        </div>
                        <i class="el-icon-question"></i>
                    </el-tooltip>
                    <el-row v-for="(uri, idx) in resource.uris" :key="uri.resourceUriId" class="uri-row">
                        <el-select v-model="uri.method" placeholder="http请求方式">
                            <el-option v-for="item in $constants.httpMethod" :key="item.value" :label="item.label"
                                       :value="item.value"></el-option>
                        </el-select>
                        <el-input v-model="uri.uri" style="width:600px;"></el-input>
                        <el-button @click="deleteUri(idx)" type="primary" size="mini" icon="el-icon-minus" circle></el-button>
                    </el-row>
                    <el-row>
                        <el-select v-model="uriMethod" placeholder="http请求方式">
                            <el-option v-for="item in $constants.httpMethod" :key="item.value" :label="item.label"
                                       :value="item.value"></el-option>
                        </el-select>
                        <el-autocomplete v-model="uriString" size="small" placeholder="输入uri" style="width:600px;"
                                         @keyup.enter.native="addUri" @blur="addUri" @select="addUri" value-key="uri"
                                         :fetch-suggestions="uriSearch"></el-autocomplete>
                        <el-button @click="addUri" type="primary" size="mini" icon="el-icon-plus" circle></el-button>
                    </el-row>
                </el-form-item>
                <el-form-item label="标签">
                    注：标签名和标签值用 = 分割，如、颜色=color
                    <el-row>
                        <el-tag v-for="(tag, idx) in resource.tags" :key="tag.resourceTagId" closable @close="deleteTag(idx)"
                                type="success" size="big" :disable-transitions="false" class="tag-lab">{{tag.k}}={{tag.v}}
                        </el-tag>
                        <el-autocomplete v-model="tagString" size="small" placeholder="输入标签名与标签值" style="width:300px;"
                                  @keyup.enter.native="addTag" @blur="addTag" @select="addTag" value-key="kv"
                                  :fetch-suggestions="tagSearch"></el-autocomplete>
                    </el-row>
                </el-form-item>
                <el-form-item label="是否启用">
                    <el-switch v-model="resource.isEnabled" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
                </el-form-item>
                <el-form-item>
                    <el-button v-if="!resource.resourceId" type="primary" @click="addResource" size="mini">新增</el-button>
                    <el-button v-if="resource.resourceId" type="primary" @click="updateResource" size="mini">保存</el-button>
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
                uriMethod: null,
                tagString: null,
                resource: {
                    uris: [],
                    tags: []
                },
                tags: [],
                uris: []
            };
        },

        computed: {
            ...mapState('session', ['user', 'authExpEvaluator']),
        },

        created() {
            pages.resourceEdit = this;
            this.resource.clientId = this.$route.query.clientId;
            if (this.$route.query.resourceId) {
                this.resource.resourceId = this.$route.query.resourceId;
                this.loadResource();
            }
            this.getTags();
            this.getUris();
        },

        methods: {
            ...mapActions('ui', ['startProgress', 'stopProgress']),

            // ========== uri相关 ==========
            getUris() {
                this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/client/${this.resource.clientId}/uri`).then(response => {
                    this.uris = response.data;
                });
            },
            uriSearch(queryString, cb) {
                let uris = this.uris;
                let results = queryString ? uris.filter(uri => {
                    return (uri.uri.toLowerCase().indexOf(queryString.toLowerCase()) === 0);
                }) : uris;
                // 去重
                let uqResults = [];
                for (let uri of results) {
                    let has = false;
                    for (let hasUri of uqResults) {
                        if (hasUri.uri === uri.uri) {
                            has = true;
                            break;
                        }
                    }
                    if (!has) {
                        uqResults.push(uri);
                    }
                }
                // 调用 callback 返回建议列表的数据
                cb(uqResults);
            },
            addUri() {
                if (!this.uriString || this.uriString.length <= 0) {
                    return
                }
                // 检测是否已经存在
                for (let uri of this.resource.uris) {
                    if (uri.method === this.uriMethod && uri.uri === this.uriString) {
                        return
                    }
                }
                this.resource.uris.push({uri: this.uriString, method: this.uriMethod})
                let has = false;
                for (let uri of this.uris) {
                    if (uri.uri === this.uriString) {
                        has = true;
                        break;
                    }
                }
                if (!has) {
                    this.uris.push({uri: this.uriString, method: this.uriMethod})
                }
                // 重置数据
                this.uriMethod = null;
                this.uriString = null;
            },
            deleteUri(idx) {
                this.resource.uris.splice(idx, 1)
            },

            // ========== 标签相关 ==========
            getTags() {
                this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/client/${this.resource.clientId}/tag`).then(response => {
                    this.tags = response.data;
                });
            },
            tagSearch(queryString, cb) {
                let tags = this.tags;
                let results = queryString ? tags.filter(tag => {
                    return (tag.kv.toLowerCase().indexOf(queryString.toLowerCase()) === 0);
                }) : tags;
                // 调用 callback 返回建议列表的数据
                cb(results);
            },
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
                let has = false;
                for (let tag of this.tags) {
                    if (tag.k === key && tag.v === value) {
                        has = true;
                        break;
                    }
                }
                if (!has) {
                    this.tags.push({k: key, v: value, kv: this.tagString})
                }
                // 重置数据
                this.tagString = null
            },
            deleteTag(idx) {
                this.resource.tags.splice(idx, 1)
            },

            // ========== 资源相关 ==========
            loadResource() {
                this.startProgress({ speed: 'fast' });
                this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/client/${this.resource.clientId}/resource/${this.resource.resourceId}`).then(response => {
                    this.resource = response.data;
                }).finally(() => this.stopProgress());
            },
            addResource() {
                this.$ajax.post(`/admin/realm/${this.user.realm.realmId}/client/${this.resource.clientId}/resource`, this.resource).then(response => {
                    this.resource = response.data;
                    this.$message.success("新建成功")
                    this.loadResource();
                })
            },
            updateResource() {
                this.$ajax.put(`/admin/realm/${this.user.realm.realmId}/client/${this.resource.clientId}/resource/${this.resource.resourceId}`, this.resource).then(response => {
                    this.$message.success("更新成功")
                    this.loadResource();
                })
            },
        }
    }
</script>

<style lang="scss">
    .resource-edit-page {
        .uri-row {
            margin-bottom: 10px;
        }
        .tag-lab {
            margin-right: 10px;
        }
    }
</style>
