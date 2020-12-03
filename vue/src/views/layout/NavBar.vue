<template>
    <div class="nav-bar">
        <div class="flex align-center">
            <i :class="toggleMenuClass" @click="toggleMenu"></i>
            <span class="margin-left text-3xs">当前项目：{{user && user.account.currentProjectName}}</span>
        </div>
        <el-dropdown>
            <span class="el-dropdown-link" style="cursor: pointer">
                你好，{{user && user.account.nickname}} <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
                <el-dropdown-item v-for="project in accountProjects" :key="project.projectId">
                    <span @click="changeProject(project.projectId)" style="display:block;">{{project.projectName}}</span>
                </el-dropdown-item>
                <el-dropdown-item divided>
                    <span @click="passwordDialogOpened = true" style="display:block;">修改密码</span>
                </el-dropdown-item>
                <el-dropdown-item>
                    <span @click="logout" style="display:block;">退出登录</span>
                </el-dropdown-item>
            </el-dropdown-menu>
        </el-dropdown>


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

    import { mapState, mapMutations, mapActions } from 'vuex';
    import PasswordEdit from './PasswordEdit';

    export default {
        components: {
            PasswordEdit
        },

        data() {
            return {
                passwordDialogOpened: false,

                accountProjects: []
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
            this.loadAccountProjects();
        },

        methods: {
            ...mapMutations('ui', ['toggleMenu']),
            ...mapActions('session', ['logout']),

            // 加载账户拥有的项目
            loadAccountProjects() {
                this.$ajax.get(`/session/project`).then(res => {
                    this.accountProjects = res.data;
                })
            },

            // 切换项目
            changeProject(projectId) {
                this.$store.dispatch('session/changeProject', projectId);
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

