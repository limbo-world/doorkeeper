<template>
    <el-container class="menu-page">
        <el-header class="padding-top-xs" height="50px">
            <el-form :inline="true" size="mini">
                <el-form-item label="名称/编码">
                    <el-input v-model="queryForm.keyword" placeholder="输入关键字"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" @click="loadMenus" size="mini" icon="el-icon-search">查询</el-button>
                    <el-tooltip effect="dark" placement="top" content="添加一级菜单">
                        <el-button type="primary" @click="addMenu" size="mini" icon="el-icon-circle-plus">添加菜单</el-button>
                    </el-tooltip>
                    <el-button v-if="!sorting" type="primary" @click="sorting = true" size="mini" icon="el-icon-sort">修改排序</el-button>
                    <template v-else>
                        <el-button type="success" @click="saveSort" size="mini" icon="el-icon-sort">保存排序</el-button>
                        <el-button type="danger" @click="cancelSort" size="mini" icon="el-icon-circle-close">取消排序</el-button>
                    </template>
                </el-form-item>
            </el-form>
        </el-header>

        <el-main>
            <el-table :data="menus" row-key="menuCode" size="mini" :default-expand-all="true">
                <el-table-column prop="menuCode" label="编码" width="180"></el-table-column>
                <el-table-column label="图标" width="50" align="right">
                    <template slot-scope="scope">
                        <i :class="scope.row.icon"></i>
                    </template>
                </el-table-column>
                <el-table-column prop="menuName" label="名称" width="120"></el-table-column>
                <el-table-column prop="menuDesc" label="描述"></el-table-column>
                <el-table-column label="操作" width="140" align="left">
                    <template slot-scope="scope">
                        <div class="operations">
                            <div>
                                <template v-if="!scope.row.isDefault">
                                    <i class="el-icon-edit" @click="editMenu(scope.row)"></i>
                                    <i vclass="el-icon-delete" @click="deleteMenu(scope.row)"></i>
                                </template>
                                <template v-else>
                                    <i class="el-icon-view" @click="viewMenu(scope.row)"></i>
                                </template>
                                <el-tooltip v-if="scope.row.parentMenuCode == null" effect="dark" placement="top"
                                            content="在该菜单下添加子菜单">
                                    <i title="添加子菜单" class="el-icon-plus" @click="addSubMenu(scope.row)"></i>
                                </el-tooltip>
                            </div>

                            <el-button-group v-if="sorting" class="sort-btns">
                                <el-button :type="scope.row.children != null ? 'primary' : ''" icon="el-icon-arrow-up"
                                           size="mini" :disabled="scope.row.isFirst" @click="moveUp(scope.row)"></el-button>
                                <el-button :type="scope.row.children != null ? 'primary' : ''" icon="el-icon-arrow-down"
                                           size="mini" :disabled="scope.row.isLast" @click="moveDown(scope.row)"></el-button>
                            </el-button-group>
                        </div>
                    </template>
                </el-table-column>
                <!--<el-table-column label="排序" align="center" width="100">-->
                    <!--<template slot-scope="scope">-->
                        <!--<el-button-group>-->
                            <!--<el-button :type="scope.row.children != null ? 'primary' : ''" icon="el-icon-arrow-up"-->
                                       <!--circle size="mini" :disabled="scope.row.isFirst" @click="moveUp(scope.row)"></el-button>-->
                            <!--<el-button :type="scope.row.children != null ? 'primary' : ''" icon="el-icon-arrow-down"-->
                                       <!--circle size="mini" :disabled="scope.row.isLast" @click="moveDown(scope.row)"></el-button>-->
                        <!--</el-button-group>-->
                    <!--</template>-->
                <!--</el-table-column>-->
            </el-table>
        </el-main>


        <el-dialog :title="`${dialogOpenMode === 'add' ? '新增' : '修改'}菜单`"
                   :visible.sync="dialogOpened" width="50%" class="edit-dialog"
                   @close="dialogCancel(false)" ref="menuEdit">
            <menu-edit :menu="menu" :view-mode="dialogOpenMode === 'view'" ref="menuEdit"></menu-edit>

            <el-footer class="text-right">
                <el-button @click="dialogCancel(false)">取 消</el-button>
                <el-button type="primary" @click="dialogConfirm">确 定</el-button>
            </el-footer>
        </el-dialog>

    </el-container>
</template>



<script>
    import { mapActions } from 'vuex';
    import MenuEdit from './MenuEdit';

    export default {
        components: {
            MenuEdit
        },

        data() {
            return {
                queryForm: {
                    keyword: '',
                    api: '',
                },

                menus: [],
                dataLoading: false,

                sorting: false,
                originalSorts: {},

                menu: {},
                dialogOpened: false,
                dialogOpenMode: 'add',
            }
        },

        created() {
            pages.menu = this;

            this.loadMenus();
        },

        methods: {
            ...mapActions('ui', ['startProgress', 'stopProgress']),

            loadMenus() {
                if (this.sorting) {
                    this.$message.warning('请先保存或退出排序');
                    return;
                }

                this.startProgress();
                this.$ajax.get('/menu').then(response => {
                    this.menus = this.sortMenuTree(this.formatMenuTree(response.data));
                    this.originalSorts = this.parseMenuSorts(this.menus);
                }).finally(() => this.stopProgress());
            },

            formatMenuTree(menus) {
                if (!menus) {
                    return menus;
                }

                const parents = menus.filter(m => m.parentMenuCode == null);
                parents.forEach(p => {
                    p.children = [];
                });
                menus.forEach(m => {
                    if (!m.parentMenuCode) {
                        return;
                    }

                    // 暂时只打算支持二级菜单
                    const p = parents.find(p => p.menuCode === m.parentMenuCode);
                    p.children.push(m);
                });
                return parents;
            },

            sortMenuTree(menus) {
                if (!menus) {
                    return menus;
                }

                menus.sort((m1, m2) => m1.sort - m2.sort);
                for (let i = 0; i < menus.length; i++) {
                    menus[i].isFirst = i === 0;
                    menus[i].isLast = i === menus.length - 1;
                }

                menus.forEach(m => {
                    const ms = m.children;
                    if (ms) {
                        m.children = this.sortMenuTree(ms);
                    }
                });

                return menus;
            },

            /**
             * 将菜单树数据中的菜单排序解析出来
             * @param menuArray   菜单数组，可以是树形式
             * @param asArray
             *      为true以数组形式返回，格式为 [{ menuCode: 'xxx', sort: 0}, { menuCode: 'vvv', sort: 1 }]
             *      为false以对象形式返回，格式为 { 'xxx': 0, 'vvv': 1 }
             */
            parseMenuSorts(menuArray = [], asArray = false, result = null) {
                result = result ? result : asArray ? [] : {};

                menuArray.forEach(m => {
                    if (asArray) {
                        result.push({
                            menuCode: m.menuCode,
                            sort: m.sort,
                        });
                    } else {
                        result[m.menuCode] = m.sort;
                    }

                    if (m.children) {
                        this.parseMenuSorts(m.children, asArray, result);
                    }
                });

                return result;
            },

            addMenu() {
                if (this.sorting) {
                    this.$message.warning('请先保存或退出排序');
                    return;
                }

                this.menu = createNewMenu();
                this.dialogOpenMode = 'add';
                this.dialogOpened = true;
            },

            addSubMenu(menu) {
                if (this.sorting) {
                    this.$message.warning('请先保存或退出排序');
                    return;
                }

                this.menu = createNewMenu();
                this.menu.parentMenuCode = menu.menuCode;
                this.dialogOpenMode = 'add';
                this.dialogOpened = true;
            },

            editMenu(menu) {
                if (this.sorting) {
                    this.$message.warning('请先保存或退出排序');
                    return;
                }

                this.menu = menu;
                this.dialogOpenMode = 'update';
                this.dialogOpened = true;
            },

            viewMenu(menu) {
                if (this.sorting) {
                    this.$message.warning('请先保存或退出排序');
                    return;
                }

                this.menu = menu;
                this.dialogOpenMode = 'view';
                this.dialogOpened = true;
            },

            dialogCancel(refresh) {
                this.$immediate(() => this.$refs.menuEdit.clearData()).then(() => {
                    this.dialogOpened = false;
                    this.menu = {};
                    if (refresh) {
                        this.loadMenus();
                    }
                });
            },

            dialogConfirm() {
                this.$refs.menuEdit.saveMenu().then(() => {
                    this.dialogOpened = false;
                    this.menu = {};
                    this.loadMenus();
                });
            },

            deleteMenu(menu) {
                this.$ajax.delete(`/menu/${menu.menuCode}`)
                    .then(() => {
                        this.$message.success('删除成功。');
                        this.loadMenus();
                    })
            },

            moveUp(menu) {
                if (menu.isFirst) {
                    return;
                }

                const thisMenus = this.menus;
                const menus = menu.parentMenuCode ? thisMenus.find(m => m.menuCode === menu.parentMenuCode).children : thisMenus;
                const prev = menus.find(m => m.sort === menu.sort - 1);
                menu.sort = prev.sort;
                prev.sort = prev.sort + 1;

                this.menus = this.sortMenuTree(thisMenus);
            },

            moveDown(menu) {
                if (menu.isLast) {
                    return;
                }

                const thisMenus = this.menus;
                const menus = menu.parentMenuCode ? thisMenus.find(m => m.menuCode === menu.parentMenuCode).children : thisMenus;
                const prev = menus.find(m => m.sort === menu.sort + 1);
                menu.sort = prev.sort;
                prev.sort = prev.sort - 1;

                this.menus = this.sortMenuTree(thisMenus);
            },

            saveSort() {
                const newSort = this.parseMenuSorts(this.menus, true);
                const updateParam = { sorts: [] };

                // 比较，找出排序变化了的菜单
                newSort.forEach(ns => {
                    if (ns.sort !== this.originalSorts[ns.menuCode]) {
                        updateParam.sorts.push(ns);
                    }
                });

                if (updateParam.sorts.length <= 0) {
                    this.sorting = false;
                    return;
                }

                const loading = this.$loading();
                this.$ajax.put('/menu/sort', updateParam).then(() => {
                    this.originalSorts = this.parseMenuSorts(this.menus);
                    this.sorting = false;
                }).finally(() => loading.close());
            },

            cancelSort() {
                this.recoveryMenuSort(this.menus);
                this.sorting = false;
            },

            /**
             * 还原菜单排序
             */
            recoveryMenuSort(menus) {
                menus.forEach(m => {
                    m.sort = this.originalSorts[m.menuCode];

                    if (m.children) {
                        this.recoveryMenuSort(m.children);
                    }
                });
                menus.sort((m1, m2) => m1.sort - m2.sort);
            }

        }

    }

    function createNewMenu() {
        return {
            menuCode: '',
            menuName: '',
            menuDesc: '',
            icon: '',
            permissions: [],
            isAdd: true,
        };
    }
</script>



<style lang="scss">
    .menu-page {
        .edit-dialog {
            .el-dialog {
                min-width: 500px;
            }
        }

        .operations {
            margin-left: -10px;

            display: flex;
            justify-content: space-between;
            align-items: center;

            .sort-btns {
                .el-button {
                    padding: 3px 7px;

                    [class^=el-icon] {
                        margin: 0;
                    }
                }
            }
        }

    }
</style>
