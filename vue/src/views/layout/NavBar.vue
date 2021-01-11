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
                    <span @click="changeRealm(realm.realmId)" style="display:block;">{{ realm.name }}</span>
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

export default {
    components: {
        PasswordEdit, RealmAddEdit
    },

    data() {
        return {
            passwordDialogOpened: false,
            realmAddDialogOpened: false,

            realms: []
        }
    },

    computed: {
        ...mapState('session', ['user']),
        ...mapState('ui', ['breadcrumbs']),

        toggleMenuClass() {
            const menuHidden = this.$store.state.ui.isMenuHidden;
            return `toggle-menu fa fa-bars ${menuHidden ? 'menu-hidden' : ''}`;
        }
    },

    created() {
        pages.navBar = this;
        this.loadAdminRealms();
    },

    methods: {
        ...mapMutations('ui', ['toggleMenu']),
        ...mapActions('session', ['logout']),

        // 加载账户拥有的项目
        loadAdminRealms() {
            let realms = [{name: "doorkeeper", realmId: 100000}]
            this.realms = realms;
            this.$store.dispatch('session/changeRealm', realms[0], false)
        },

        // 切换
        changeRealm(realm) {
            this.$store.dispatch('session/changeRealm', realm, true);
        },

        // 新建realm
        realmAddDialogConfirm() {
            this.$refs.realmAddEdit.addRealm().then(() => {
                this.$message.success('新建成功');
                this.$refs.realmAddEdit.clearData();
                this.realmAddDialogOpened = false;
            }).catch(data => {
                this.$message.error('创建失败');
            });
        },

        // 重置密码
        passwordDialogConfirm() {
            this.$refs.passwordEdit.updatePassword().then(() => {
                this.$message.success('修改密码成功，您需要重新登录！');
                this.passwordDialogOpened = false;
                this.logout();
            });
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

