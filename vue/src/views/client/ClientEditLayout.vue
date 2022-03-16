<template>

    <el-container class="app-wrapper">
        <!-- 头部 -->
        <el-header height="50px">
            <el-menu :default-active="tabIndex" class="el-menu-demo" mode="horizontal" @select="tabChange">
                <el-menu-item :index="1">基础信息</el-menu-item>
                <el-menu-item :index="2">角色</el-menu-item>
                <el-menu-item :index="3">资源</el-menu-item>
                <el-menu-item :index="4">策略</el-menu-item>
                <el-menu-item :index="5">权限</el-menu-item>
            </el-menu>
        </el-header>

        <!-- 页面主体 -->
        <el-main class="relative">
            <client-edit v-if="tabIndex === 1" :client-id="clientId"></client-edit>
            <role-edit v-if="tabIndex === 2" :client-id="clientId"></role-edit>
            <resource v-if="tabIndex === 3" :client-id="clientId"></resource>
            <policy v-if="tabIndex === 4" :client-id="clientId"></policy>
            <permissionAggregate v-if="tabIndex === 5" :client-id="clientId"></permissionAggregate>
        </el-main>
    </el-container>

</template>

<script>
    import ClientEdit from "./ClientEdit";
    import RoleEdit from "@/views/role/Role";
    import Resource from "@/views/resource/Resource";
    import Policy from "@/views/policy/Policy";
    import Permission from "@/views/permissionAggregate/Permission";

    export default {
        components: {
            Policy, ClientEdit, RoleEdit, Resource, Permission
        },

        data: function () {
            return {
                clientId: null,
                tabIndex: 1,
            }
        },

        computed: {
        },

        created() {
            pages.clientEditLayout = this;
            this.updateParamFromRoute(this.$route);
        },

        /**
         * 组件内变更路由参数时更新query中的参数
         */
        beforeRouteUpdate (to, from, next) {
            this.updateParamFromRoute(to);
            next();
        },

        methods: {
            /**
             * 从路由中抽取页面所需的参数
             * @param route
             */
            updateParamFromRoute(route) {
                this.clientId = route.query.clientId;

                // 需要解析为数字，因为menu的index值是数字
                let tabIdx = route.query.tab;
                tabIdx = tabIdx == null ? 1 : parseInt(tabIdx);
                this.tabIndex = tabIdx;
            },

            tabChange(newTabIndex) {
                if (this.tabIndex === newTabIndex) {
                    return;
                }

                // 当tab发生改变时才更新路由
                this.$router.push({
                    path: '/client/client-edit',
                    query: {
                        clientId: this.clientId,
                        tab: newTabIndex
                    }
                });
            }

        }
    }
</script>

<style lang="scss">
</style>
