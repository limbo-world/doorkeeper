<!--
  - Copyright 2020-2024 Limbo Team (https://github.com/limbo-world).
  -
  -   Licensed under the Apache License, Version 2.0 (the "License");
  -   you may not use this file except in compliance with the License.
  -   You may obtain a copy of the License at
  -
  -   	http://www.apache.org/licenses/LICENSE-2.0
  -
  -   Unless required by applicable law or agreed to in writing, software
  -   distributed under the License is distributed on an "AS IS" BASIS,
  -   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  -   See the License for the specific language governing permissions and
  -   limitations under the License.
  -->

<template>
    <el-container class="group-page">
        <el-header class="padding-top-xs" height="30px">
            <el-button type="primary" @click="() => {dialogOpened = true; group = {parentId: 0}}" size="mini">新增
            </el-button>
        </el-header>

        <el-main class="group-page-main">
            <div style="width: 70%;">
                <el-input placeholder="输入关键字进行过滤" v-model="filterText" style="width: 300px"></el-input>
                <el-tree class="filter-tree" ref="groupTree"
                         :data="groups"
                         :props="defaultProps"
                         default-expand-all
                         draggable
                         :filter-node-method="filterNode"
                         @node-drop="dragNode"
                >
                <span class="custom-tree-node" slot-scope="{ node, data }">
                    <span>{{ node.label }}</span>
                    <span>
                        <span>{{ node.data.isDefault ? "默认添加" : "" }}</span>
                        <el-button type="text"
                                   @click="() => {dialogOpened = true; group = {parentId: node.data.groupId}}">新增</el-button>
                        <el-button type="text" @click="toGroupEdit(node.data.groupId)">编辑</el-button>
                        <el-button type="text" size="mini" @click="() => removeGroup(node.data.groupId)">删除</el-button>
                    </span>
                </span>
                </el-tree>
            </div>
        </el-main>

        <el-dialog title="新增" :visible.sync="dialogOpened" width="50%" class="edit-dialog"
                   :before-close="preventCloseWhenProcessing">
            <el-form :model="group" label-width="80px" size="mini" class="edit-form" ref="editForm">
                <el-form-item label="名称">
                    <el-input v-model="group.name"></el-input>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input type="textarea" v-model="group.description"></el-input>
                </el-form-item>
                <el-form-item label="默认添加">
                    <el-switch v-model="group.isDefault" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
                </el-form-item>
            </el-form>
            <el-footer class="text-right">
                <el-button @click="() => {group = {}; dialogOpened = false;}" :disabled="dialogProcessing">取 消
                </el-button>
                <el-button type="primary" @click="addGroup" :loading="dialogProcessing"
                           :disabled="dialogProcessing">确 定
                </el-button>
            </el-footer>
        </el-dialog>
    </el-container>
</template>


<script>
import {mapActions, mapState} from 'vuex';

export default {
    data() {
        return {

            dialogProcessing: false,
            group: {},
            dialogOpened: false,

            groups: [],
            defaultProps: {
                children: 'children',
                label: 'name'
            },
            filterText: '',
        };
    },

    computed: {
        ...mapState('session', ['user', 'authExpEvaluator']),
    },

    created() {
        pages.group = this;
        this.loadGroup();
    },

    watch: {
        filterText(val) {
            this.$refs.groupTree.filter(val);
        }
    },

    methods: {
        ...mapActions('ui', ['startProgress', 'stopProgress']),

        filterNode(value, data) {
            if (!value) return true;
            return data.name.indexOf(value) !== -1;
        },
        loadGroup() {
            this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/group`, {params: {returnType: 'tree'}}).then(response => {
                this.groups = response.data;
            })
        },
        /**
         * 移动节点
         */
        dragNode(currentNode, enterNode, pos, event) {
            let parentId;
            if ('before' === pos || 'after' === pos) {
                parentId = enterNode.data.parentId;
            } else {
                parentId = enterNode.data.groupId;
            }
            this.$ajax.put(`/admin/realm/${this.user.realm.realmId}/group/${currentNode.data.groupId}`, {parentId: parentId}).then(response => {
                this.loadGroup();
            })
        },
        /**
         * 删除节点
         */
        removeGroup(groupId) {
            this.$confirm('确认删除, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.$ajax.delete(`/admin/realm/${this.user.realm.realmId}/group/${groupId}`).then(response => {
                    this.$message({
                        type: 'success',
                        message: '删除成功!'
                    });
                    this.loadGroup();
                })
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });
            });
        },
        addGroup() {
            this.$ajax.post(`/admin/realm/${this.user.realm.realmId}/group`, this.group).then(response => {
                this.loadGroup();
                this.dialogOpened = false;
            })
        },
        preventCloseWhenProcessing() {
            if (this.dialogProcessing) {
                return false;
            }

            this.role = {};
            this.dialogOpened = false;
        },

        toGroupEdit(groupId) {
            this.$router.push({
                path: '/group/group-edit', query: {groupId: groupId}
            })
        },

        test(v) {
            console.log(111)
            console.log(v)
        },
    }

}
</script>


<style lang="scss">
.custom-tree-node {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 14px;
    padding-right: 8px;
}
</style>
