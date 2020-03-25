<template>
    <div v-loading="menuLoading">
        <el-input v-if="searchable" v-model="menuTreeSearchKeyword" class="max-width-300 margin-bottom-xs"
                  suffix-icon="el-icon-search" @input="$refs.tree.filter(menuTreeSearchKeyword)"></el-input>

        <el-tree :data="menuTreeData" ref="tree" show-checkbox node-key="menuCode"
                 :props="{label: 'menuName', children: 'children'}"
                 :default-checked-keys="menuTreeCheckedMenuCode" :default-expand-all="true"
                 :filter-node-method="(v, n) => !v || n.menuName.indexOf(v) !== -1">
        </el-tree>
    </div>
</template>



<script>
    export default {
        props: {
            menuCodeList: {
                type: Array,
                default() {
                    return [];
                }
            },

            selectable: {
                type: Boolean,
                default: true,
            },

            searchable: {
                type: Boolean,
                default: false,
            }
        },

        data() {
            return {
                menuTreeSearchKeyword: '',

                menuLoading: false,
                menuTreeData: [],
                menuTreeDataMap: {},
                menuTreeCheckedMenuCode: [],
            };
        },

        computed: {
            selectedMenuCodeList() {
                const $tree = this.$refs.tree;
                return $tree.getCheckedKeys().concat($tree.getHalfCheckedKeys());
            },


            _checkedMenuCode() {
                return this.menuCodeList.filter(mc => {
                    return !this.menuTreeDataMap[mc].children
                        || this.menuTreeDataMap[mc].children.length <= 0
                });
            }
        },

        watch: {
            menuCodeList: function(newValue) {
                // 过滤出被选中菜单中的没有子节点的菜单，有子节点的会自动根据子节点选中状态来勾选或半选
                this.menuTreeCheckedMenuCode = newValue.filter(mc => {
                    return !this.menuTreeDataMap[mc].children
                        || this.menuTreeDataMap[mc].children.length <= 0
                });
                this.$refreshData(this.menuTreeCheckedMenuCode);
            }
        },

        methods: {

            loadMenus() {
                this.menuLoading = true;
                return this.$ajax.get('/menu').then(response => {
                    this.menuTreeData = this.formatMenuTree(response.data);
                }).finally(() => this.menuLoading = false);
            },

            formatMenuTree(menus) {
                const parents = menus.filter(m => m.parentMenuCode == null);
                parents.forEach(p => {
                    p.children = [];
                });
                menus.forEach(m => {
                    this.menuTreeDataMap[m.menuCode] = m;

                    if (!this.selectable) {
                        m.disabled = true;
                    }

                    if (!m.parentMenuCode) {
                        return;
                    }

                    // 暂时只打算支持二级菜单
                    const p = parents.find(p => p.menuCode === m.parentMenuCode);
                    p.children.push(m);
                });
                return parents;
            },

            clearData() {
                // this.menus = [];
                // this.menuTreeData = [];
                // this.menuTreeDataMap = {};
                this.menuTreeCheckedMenuCode = [];
            },
        }
    }
</script>
