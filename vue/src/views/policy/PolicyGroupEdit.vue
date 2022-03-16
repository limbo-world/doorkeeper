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
  - See the License for the specific language governing permissionAggregates and
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
        <el-table :data="policyGroups" size="mini">
            <el-table-column prop="tag" label="名称"></el-table-column>
            <el-table-column>
                <template slot="header" slot-scope="scope">
                    <span>是否延伸</span>
                    <el-tooltip class="item" effect="dark" content="开启的情况下，会把角色传递给下级用户组的用户" placement="top-start">
                        <i class="el-icon-question"/>
                    </el-tooltip>
                </template>
                <template slot-scope="scope">
                    <el-switch v-model="scope.row.isExtend" active-color="#13ce66" inactive-color="#ff4949"></el-switch>
                </template>
            </el-table-column>
        </el-table>
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
        ...mapState('sessionAggregate', ['user']),
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
            return this.$ajax.get(`/admin/realm/${this.user.realm.realmId}/group`, {params: {returnType: 'tree'}}).then(response => {
                let groups = response.data;
                this.organizeGroupTag('', groups)
                this.groups = groups;
            })
        },
        organizeGroupTag(parentTag, groups) {
            if (!groups || groups.length <= 0) {
                return
            }

            for (let group of groups) {
                group.tag = parentTag + "/" + group.name;
                this.organizeGroupTag(group.tag, group.children)
            }
        },

        handleCheckChange(data, checked, indeterminate) {
            console.log(data, checked, indeterminate)
            if (checked) { // 选中 添加
                let policyGroups = [];
                for (let policyGroup of this.policyGroups) {
                    policyGroups.push({groupId: policyGroup.groupId, isExtend: policyGroup.isExtend, tag: policyGroup.tag})
                }
                // 如果已经有了，tag设置下
                let has = false;
                for (let policyGroup of policyGroups) {
                    if (data.groupId === policyGroup.groupId) {
                        has = true
                        policyGroup.tag = data.tag;
                        break
                    }
                }
                if (!has) {
                    policyGroups.push({groupId: data.groupId, isExtend: false, tag: data.tag})
                }
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
