<template>

    <el-container class="app-wrapper">
        <!-- 头部 -->
        <el-header height="50px">
            <el-menu :default-active="tabIndex" class="el-menu-demo" mode="horizontal" @select="idx => {tabIndex = idx}">
                <el-menu-item :index="1">基础信息</el-menu-item>
                <el-menu-item :index="2">角色组合</el-menu-item>
                <el-menu-item :index="3">用户</el-menu-item>
            </el-menu>
        </el-header>

        <!-- 页面主体 -->
        <el-main class="relative">
            <role-edit v-if="tabIndex === 1" :role-id="roleId" :client-id="clientId"></role-edit>
            <role-combine v-if="tabIndex === 2" :role-id="roleId" :client-id="clientId"></role-combine>
            <role-user-edit v-if="tabIndex === 3" :role-id="roleId"></role-user-edit>
        </el-main>
    </el-container>

</template>

<script>
    import RoleEdit from "./RoleEdit";
    import RoleCombine from "./RoleCombineEdit";
    import RoleUserEdit from "@/views/role/RoleUserEdit";

    export default {
        components: {
            RoleEdit, RoleCombine, RoleUserEdit
        },

        data: function () {
            return {
                roleId: null,
                clientId: 0,
                tabIndex: 1,
            }
        },

        computed: {
        },
        created() {
            pages.roleEditLayout = this;

            this.roleId = this.$route.query.roleId;
            this.clientId = this.$route.query.clientId;
        },

        methods: {

        }
    }
</script>

<style lang="scss">
</style>
