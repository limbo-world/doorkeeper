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
    <el-form-item label="用户组">
        <el-tree class="filter-tree" ref="groupTree"
                 :data="groups"
                 :props="defaultProps"
                 default-expand-all
                 draggable
                 node-key="groupId"
                 show-checkbox
                 check-strictly="true"
                 @check-change="handleCheckChange"
        >
        </el-tree>
    </el-form-item>
</template>


<script>

import {mapState, mapActions} from 'vuex';

export default {
    props: {
        policyId: {
            type: Number,
            default: null
        },
        clientId: {
            type: Number,
            default: 0
        },
        policyGroups: {
            type: Array,
            default: []
        }
    },

    data() {
        return {
            groups: [],
            defaultProps: {
                children: 'children',
                label: 'name',
            },
            defaultCheckedKeys: []
        };
    },
    computed: {
        ...mapState('session', ['user']),
    },
    created() {
        pages.policyGroupEdit = this;

        this.loadGroup().then(() => {
            // 初始化已选数据
            for (let policyGroup of this.policyGroups) {
                this.defaultCheckedKeys.push(policyGroup.groupId)
            }
            this.$refs.groupTree.setCheckedKeys(this.defaultCheckedKeys)
        });
    },
    methods: {
        loadGroup() {
            return this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/group`).then(response => {
                // 层级展示
                let organizeGroup = this.organizeGroup([], response.data);
                console.log(organizeGroup)
                this.groups = organizeGroup;
            })
        },
        /**
         * 组织树状数据结构
         */
        organizeGroup(nodes, groups) {
            if (!nodes) {
                nodes = [];
            }
            if (!groups || groups.length <= 0) {
                return nodes;
            }
            for (let group of groups) {
                group.children = [];
            }
            let remain = [];
            for (let group of groups) {
                if (group.parentId === 0) { // 父节点放在根部
                    group.tag = "/" + group.name;
                    nodes.push(group);
                } else {
                    let node = this.findNode(group, nodes);
                    if (node) {
                        group.tag = node.tag + "/" + group.name;
                        node.children.push(group);
                    } else {
                        remain.push(group)
                    }
                }
            }
            return this.organizeGroup(nodes, remain);
        },
        findNode(node, nodes) {
            if (!nodes || nodes.length <= 0) {
                return null;
            }
            for (let n of nodes) {
                if (node.parentId === n.groupId) {
                    return n;
                } else {
                    let findNode = this.findNode(node, n.children);
                    if (findNode) {
                        return findNode;
                    }
                }
            }
        },


        handleCheckChange(data, checked, indeterminate) {
            console.log(data, checked, indeterminate)
            if (checked) { // 选中 添加
                let policyGroups = [];
                policyGroups.push({groupId: data.groupId})
                this.policyGroups = policyGroups;
            } else { // 取消选中 删除
                for (let i = 0; i < this.policyGroups.length; i++) {
                    if (data.groupId === this.policyGroups[i].groupId) {
                        this.policyGroups.splice(i, 1)
                        break
                    }
                }
            }
            this.$emit('bind-policy-groups', this.policyGroups)
        }
    }
}
</script>

<style lang="scss">
.policy-user-edit-page {
    .el-transfer {
        .el-transfer-panel {
            width: 350px;

            .el-transfer-panel__item {
                margin-left: 0;
                display: block !important;
            }
        }
    }
}
</style>
