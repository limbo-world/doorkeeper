<template>

    <div class="side-menu">

        <!-- LOGO -->
        <div class="logo-container">
            <img v-if="!isMenuHidden" class="logo-img" src="../../assets/images/logo.svg"/>
            <img v-else class="logo-img" src="../../assets/images/logo-mini.svg"/>
        </div>

        <scroll-view class="menu-scroll-content">
            <el-menu
                mode="vertical"
                :show-timeout="200"
                :default-active="$route.path"
                :collapse="isMenuHidden"
                :collapse-transition="false"
                active-text-color="#409EFF"
                :default-openeds="['001', '002']"
                :unique-opened="false"
                :router="true"
            >
                <template v-for="menu in menus">
                    <!-- 当前菜单无子菜单，即叶子菜单，直接渲染router-link -->
                    <el-menu-item v-if="!hasChildren(menu)" :index="menu.menuCode" :route="menu.route"
                                  :class="menuHiddenClass">
                        <i class="menu-icon" :class="menu.icon"></i>
                        <span slot="title">{{menu.menuName}}</span>
                    </el-menu-item>

                    <!-- 存在子菜单时，先渲染当前菜单 -->
                    <el-submenu v-else :index="menu.menuCode" :key="menu.menuName" :class="menuHiddenClass">
                        <template slot="title">
                            <i class="menu-icon" :class="menu.icon"></i>
                            <span slot="title">{{menu.menuName}}</span>
                        </template>

                        <!-- 再循环渲染所有子菜单 -->
                        <!-- 这里不再考虑孙菜单的情况，只支持二级菜单 -->
                        <el-menu-item v-for="subMenu in menu.children" :key="subMenu.route"
                                      :index="subMenu.route" :route="{'path': subMenu.route}">
                            <i :class="'menu-icon ' + subMenu.icon"></i>
                            <span slot="title">{{subMenu.menuName}}</span>
                        </el-menu-item>
                    </el-submenu>
                </template>
            </el-menu>
        </scroll-view>
    </div>
</template>

<script>
    import { mapState, mapMutations, mapActions } from 'vuex';
    import ScrollView from '../../components/ScrollView';

    export default {
        components: {
            'scroll-view': ScrollView
        },

        computed: {
            ...mapState('ui', ['isMenuHidden']),
            ...mapState('sessionAggregate', ['menus']),

            menuHiddenClass() {
                return this.isMenuHidden ? 'menu-hidden' : '';
            }
        },

        created() {
            pages.layoutMenu = this;
            // 加载菜单信息
            this.loadMenus().then(() => this.calculateSelectedMenu());
        },

        watch: {
            $route(newValue) {
                this.calculateSelectedMenu(newValue);
            }
        },

        methods: {
            ...mapMutations('sessionAggregate', ['setSelectedMenu']),
            ...mapActions('sessionAggregate', ['loadMenus']),

            hasChildren(m) {
                return m.children && m.children.length > 0
            },

            calculateSelectedMenu(route) {
                route = route ? route : this.$route;
                const menus = this.$store.state.ui.menus;
                if (menus) {
                    const path = route.fullPath;
                    const menu = menus.map(m => [m, m.children]).flat().flat().find(m => m && m.route === path);
                    if (menu) {
                        this.$store.commit('ui/setSelectedMenu', {
                            menu: menu,
                            menus: this.menus
                        });
                    }
                }
            }
        }
    }
</script>

<style rel="stylesheet/scss" lang="scss">

    .side-menu {
        width: 100%;
        height: 100%;
        background-color: white;

        .logo-container {
            width: 100%;
            height: 100px;
            display: flex;
            align-items: center;
            justify-content: center;

            background-color: white;
            position: relative;
            z-index: 100;

            .logo-title {
                height: 50px;
                line-height: 50px;
                font-size: 25px;
                margin-left: 10px;
                color: aliceblue;
            }
            img {
                width: 70%;
            }
        }

        .menu-scroll-content {
            width: 230px;
            //height: calc(100% - 50px);
            z-index: 90;
        }
    }


    .menu-hidden {
        .side-menu {
            .logo-container {
                .logo-title {
                    display: none;
                }
            }
        }
    }

    .el-menu {
        height: 100%;
        border: none;

        .el-submenu__title, .el-menu-item {
            height: 48px;
            display: flex;
            justify-content: flex-start;
            align-items: center;

            .menu-icon {
                margin-right: 10px;
                overflow: hidden;
                vertical-align: baseline;
            }
        }

        .el-submenu,.el-menu-item {
            &.menu-hidden .menu-icon {
                margin-right: 0;
            }
            .menu-icon.fa {
                width: 14px;
                height: 14px;
                font-size: 10px;
            }
        }

        [class*=fa] {
            vertical-align: middle;
            margin-right: 5px;
            width: 24px;
            text-align: center;
            font-size: 18px;
        }
    }

</style>
