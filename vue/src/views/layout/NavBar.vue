<template>
    <div class="nav-bar">
        <div class="flex align-center">
            <i :class="toggleMenuClass" @click="toggleMenu"></i>
            <span class="margin-left text-3xs">你好，{{ user && user.nickname }}</span>
        </div>
        <el-dropdown>
            <span class="el-dropdown-link" style="cursor: pointer">
                {{ user && user.realm && user.realm.name }} <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
                <el-dropdown-item v-for="realm in realms" :key="realm.realmId">
                    <span @click="changeRealm(realm)" style="display:block;">{{ realm.name }}</span>
                </el-dropdown-item>
                <el-dropdown-item divided>
                    <span @click="realmAddDialogOpened = true" style="display:block;">新建域</span>
                </el-dropdown-item>
                <el-dropdown-item>
                    <span @click="passwordDialogOpened = true" style="display:block;">修改密码</span>
                </el-dropdown-item>
                <el-dropdown-item>
                    <span @click="logout" style="display:block;">退出登录</span>
                </el-dropdown-item>
            </el-dropdown-menu>
        </el-dropdown>

        <el-dialog title="新建域" :visible.sync="realmAddDialogOpened" width="50%" class="edit-dialog"
                   @close="realmAddDialogOpened = false" :append-to-body="true">
            <realm-add-edit @cancel="realmAddDialogOpened = false" ref="realmAddEdit"/>
            <span slot="footer" class="dialog-footer">
                <el-button @click="realmAddDialogOpened = false">取 消</el-button>
                <el-button type="primary" @click="realmAddDialogConfirm">确 定</el-button>
            </span>
        </el-dialog>

        <el-dialog title="修改密码" :visible.sync="passwordDialogOpened" width="50%" class="edit-dialog"
                   @close="passwordDialogOpened = false" :append-to-body="true">
            <password-edit @cancel="passwordDialogOpened = false" ref="passwordEdit"/>
            <span slot="footer" class="dialog-footer">
                <el-button @click="passwordDialogOpened = false">取 消</el-button>
                <el-button type="primary" @click="passwordDialogConfirm">确 定</el-button>
            </span>
        </el-dialog>
    </div>

</template>

<script>

import {mapState, mapMutations, mapActions} from 'vuex';
import RealmAddEdit from "@/views/layout/RealmAddEdit";
import PasswordEdit from './PasswordEdit';
import {http} from "@/libs/axios-installer";
import store from "@/libs/vuex-installer";

export default {
    components: {
        PasswordEdit, RealmAddEdit
    },

    data() {
        return {
            passwordDialogOpened: false,
            realmAddDialogOpened: false,
        }
    },

    computed: {
        ...mapState('session', ['user', 'realms']),
        ...mapState('ui', ['breadcrumbs']),

        toggleMenuClass() {
            const menuHidden = this.$store.state.ui.isMenuHidden;
            return `toggle-menu fa fa-bars ${menuHidden ? 'menu-hidden' : ''}`;
        }
    },

    created() {
        pages.navBar = this;
    },

    methods: {
        ...mapMutations('ui', ['toggleMenu']),
        ...mapActions('session', ['logout']),

        // 切换
        changeRealm(realm) {
            this.$store.dispatch('session/changeRealm', realm)
        },

        // 新建realm
        realmAddDialogConfirm() {
            const loading = this.$loading();
            this.$refs.realmAddEdit.addRealm().then(() => {
                this.$message.success('新建成功');
                this.$refs.realmAddEdit.clearData();
                this.realmAddDialogOpened = false;

                // 加载用户拥有的域
                this.$store.dispatch('session/loadRealms').then(() => {
                    const user = store.getters['session/user']
                    const realms = store.state.session.realms
                    let needChange = true;
                    // 设置当前选中的域 如果已经有选了则不需要切换了
                    if (user.realm) {
                        needChange = false;
                        // 如果已选的不在列表里面 也需要切换
                        let realmIds = realms.map(realm => realm.realmId);
                        if (realmIds.indexOf(user.realm.realmId) < 0) {
                            needChange = true;
                        }
                    }
                    if (needChange) {
                        this.$store.dispatch('session/changeRealm', realms[0])
                    }
                }).catch(reject => {
                    console.log("realms加载失败", reject)
                })
            }).catch(data => {
                this.$message.error('创建失败');
            }).finally(() => loading.close());
        },

        // 重置密码
        passwordDialogConfirm() {
            const loading = this.$loading();
            this.$refs.passwordEdit.updatePassword().then(() => {
                this.$message.success('修改密码成功，您需要重新登录！');
                this.passwordDialogOpened = false;
                this.logout();
            }).finally(() => loading.close());
        },
    }
}
</script>


<style lang="scss">
.nav-bar {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: space-between;
    align-items: center;

    .toggle-menu {
        font-size: 20px;
        transition: transform .3s;

        &.menu-hidden {
            transform: rotateZ(90deg);
        }
    }

    .breadcrumb-enter-active,
    .breadcrumb-leave-active {
        transition: all .5s;
    }

    .breadcrumb-enter,
    .breadcrumb-leave-active {
        opacity: 0;
        transform: translateX(20px);
    }

    .breadcrumb-move {
        transition: all .5s;
    }

    .breadcrumb-leave-active {
        position: absolute;
    }
}

</style>

