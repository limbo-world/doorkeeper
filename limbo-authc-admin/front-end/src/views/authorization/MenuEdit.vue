<template>
    <el-container v-loading="loading" class="menu-edit-page">
        <el-main>
            <el-form :model="menu" label-width="80px" size="mini" class="edit-form"
                     :rules="rules" ref="editForm" :disabled="isViewMode">
                <el-form-item label="编码">
                    <el-input v-model="menu.menuCode" :readonly="menu.id != null"></el-input>
                </el-form-item>
                <el-form-item label="名称" prop="menuName" :rules="{required: true, message: '请填写名称', trigger: 'blur'}">
                    <el-input v-model="menu.menuName"></el-input>
                </el-form-item>
                <el-form-item label="图标" prop="icon">
                    <div class="menu-icon-form-item">
                        <i :class="!menu.icon ? 'el-icon-none' : menu.icon" @click="openIconPanel"></i>
                    </div>
                </el-form-item>
                <el-form-item label="描述">
                    <el-input type="textarea" v-model="menu.menuDesc"></el-input>
                </el-form-item>
                <el-form-item label="权限列表" >
                    <div class="max-width-130">
                        <el-input v-model="permissionSearchKeyword" suffix-icon="el-icon-search"
                                  @input="searchPermissions"></el-input>
                    </div>
                </el-form-item>
                <el-form-item label=" ">
                    <!-- warn v-model中必须用一个属性，不能用属性的子属性 -->
                    <el-checkbox-group v-model="selectedPermCodeList" class="perm-checkbox-group">
                        <el-checkbox v-for="p in permissions" :key="p.permCode" :label="p.permCode">
                            <el-tooltip :content="p.permName" placement="top">
                                <span>{{p.permName}}</span>
                            </el-tooltip>
                        </el-checkbox>
                    </el-checkbox-group>
                </el-form-item>
            </el-form>
        </el-main>

        <el-dialog title="选择一个图标" :visible.sync="iconDialogOpened" append-to-body>
            <menu-icon-panel @icon="i => {menu.icon = i; iconDialogOpened = false}"></menu-icon-panel>
        </el-dialog>

    </el-container>
</template>


<script>
    import MenuIconPanel from './MenuIconPanel';

    export default {
        components: {
            MenuIconPanel
        },

        props: {
            menu: {
                type: Object,
                default() {
                    return {
                        permCodeList: []
                    }
                }
            },

            viewMode: {
                type: Boolean,
                default: false
            }
        },

        data() {
            return {
                loading: true,

                allPermissions: [],
                permissions: [],
                selectedPermCodeList: [],
                permissionSearchKeyword: '',

                iconDialogOpened: false,
            };
        },

        computed: {
            isViewMode() {
                return !!this.viewMode;
            }
        },

        watch: {
            menu: {
                immediate: true,
                handler(newValue, oldValue) {
                    const newCode = newValue && newValue.menuCode;
                    const oldCode = oldValue && oldValue.menuCode;
                    if (newCode !== oldCode || oldValue == null) {
                        this.clearData();

                        this.loading = true;
                        this.loadPermissions().then(() => {
                            if (!newValue.isAdd && newValue.menuCode) {
                                this.loadMenu(newValue.menuCode);
                            } else {
                                this.loading = false;
                            }
                        });
                    }
                }
            },
        },

        created() {
            pages.menuEdit = this;
        },

        methods: {

            // 需要加载菜单详细信息，将菜单拥有的权限也加载到
            loadMenu(menuCode) {
                this.loading = true;
                this.$ajax.get(`/menu/${menuCode}`).then(response => {
                    this.menu = response.data;
                    this.selectedPermCodeList = this.menu.permissions ? this.menu.permissions.map(p => p.permCode) : [];
                }).finally(() => this.loading = false);
            },

            loadPermissions() {
                return this.$ajax.get('/permission').then(response => {
                    this.allPermissions = response.data;
                    this.permissions = response.data;
                });
            },

            searchPermissions() {
                if (this.permissionSearchKeyword && this.permissions) {
                    this.permissions = this.allPermissions.filter(p => p.permName.indexOf(this.permissionSearchKeyword) >= 0);
                } else {
                    this.permissions = this.allPermissions;
                }
            },

            openIconPanel() {
                this.iconDialogOpened = !this.viewMode;
            },

            clearData() {
                this.selectedPermCodeList = [];

                if (this.$refs.editForm) {
                    this.$refs.editForm.clearValidate();
                }
            },

            saveMenu() {
                if (this.viewMode) {
                    return this.$immediate();
                }

                const loading = this.$loading();
                return new Promise((resolve, reject) => {
                    this.$refs.editForm.validate(valid => {
                        if (!valid) {
                            reject();
                            return;
                        }

                        const menu = JSON.parse(JSON.stringify(this.menu));
                        delete menu.permissions;
                        menu.permCodeList = this.selectedPermCodeList;
                        const prom = !menu.isAdd
                            ? this.doUpdateMenu(menu)
                            : this.doAddMenu(menu);
                        prom.then(() => {
                            this.clearData();
                            resolve();
                        }).catch(reject);
                    });
                }).finally(() => loading.close())
            },

            doAddMenu(menu) {
                delete menu.add;
                return this.$ajax.post('/menu', menu);
            },

            doUpdateMenu(menu) {
                return this.$ajax.put(`/menu/${menu.menuCode}`, menu);
            },
        }
    }
</script>


<style lang="scss">
    .menu-edit-page {
        .perm-checkbox-group {
            .el-checkbox {
                width: 140px;

                .el-checkbox__label {
                    vertical-align: bottom;
                    line-height: 28px;

                    span {
                        display: block;
                        max-width: 112px;
                        overflow: hidden;
                        text-overflow: ellipsis;
                        white-space: nowrap;
                    }
                }
            }
        }

        .menu-icon-form-item {
            i {
                display: block;
                width: 50px;
                height: 50px;
                font-size: 22px;
                text-align: center;
                line-height: 50px;
                border: 1px solid rgba(1, 1, 1, .15);
                border-radius: 5px;
                cursor: pointer;
            }
            i.el-icon-none:before {
                content: '无';
                color: rgba(1, 1, 1, .15);
            }
        }
    }

</style>
