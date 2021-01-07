<template>

    <el-container class="app-wrapper">
        <!-- 头部 -->
        <el-header height="50px">
            <el-menu :default-active="tabIndex" class="el-menu-demo" mode="horizontal" @select="idx => {tabIndex = idx}">
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
        </el-main>
    </el-container>

</template>

<script>
    import ClientEdit from "./ClientEdit";
    import RoleEdit from "@/views/role/Role";
    import Resource from "@/views/resource/Resource";
    import Policy from "@/views/policy/Policy";

    export default {
        components: {
            Policy, ClientEdit, RoleEdit, Resource
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

            this.clientId = this.$route.query.clientId;
        },

        methods: {

        }
    }
</script>

<style lang="scss">
</style>
