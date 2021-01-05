<template>

    <el-container class="app-wrapper">
        <!-- 左侧菜单 -->
        <el-aside :class="asideClass">
            <app-menu></app-menu>
        </el-aside>

        <!-- 右侧 -->
        <el-container>
            <!-- 头部 -->
            <el-header height="50px">
                <nav-bar></nav-bar>
            </el-header>

            <!-- 页面主体 -->
            <el-main class="relative">
                <progress-bar></progress-bar>
                <app-main></app-main>
            </el-main>
        </el-container>
    </el-container>

</template>

<script>
    import { mapState, mapMutations } from 'vuex';
    import AppMain from './Main';
    import Menu from './Menu';
    import NavBar from './NavBar';
    import ProgressBar from './ProgressBar';

    export default {
        components: {
            'app-main': AppMain,
            'app-menu': Menu,
            'nav-bar': NavBar,
            ProgressBar,
        },

        data: function () {
            return {
                menuLevelList: []
            }
        },

        computed: {
            ...mapState('ui', ['isMenuHidden']),

            asideClass() {
                return this.isMenuHidden ? 'menu-hidden' : '';
            }
        },


        methods: {
            ...mapMutations('ui', ['toggleMenu']),
        }
    }
</script>

<style lang="scss" scoped>
    .el-container.app-wrapper {
        height: 100%;

        .el-aside {
            width: 230px !important;
            background-color: rgb(48, 65, 86);
            -webkit-transition: width .3s;
            -moz-transition: width .3s;
            -ms-transition: width .3s;
            -o-transition: width .3s;
            transition: width .3s;
            box-shadow: 0px 2px 5px #888888;
            z-index: 100;

            &.menu-hidden {
                width: 64px !important;
            }
        }

        .el-header {
            background-color: white;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            z-index: 10;
        }

        .el-main {
            padding: 0;
        }
    }
</style>
