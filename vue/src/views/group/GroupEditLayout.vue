<template>

    <el-container class="app-wrapper">
        <!-- 头部 -->
        <el-header height="50px">
            <el-menu :default-active="tabIndex" class="el-menu-demo" mode="horizontal" @select="idx => {tabIndex = idx}">
                <el-menu-item :index="1">基础信息</el-menu-item>
                <el-menu-item :index="2">用户</el-menu-item>
            </el-menu>
        </el-header>

        <!-- 页面主体 -->
        <el-main class="relative">
            <group-edit v-if="tabIndex === 1" :group-id="groupId"></group-edit>
            <group-user-edit v-if="tabIndex === 2" :group-id="groupId"></group-user-edit>
        </el-main>
    </el-container>

</template>

<script>
    import GroupEdit from "@/views/group/GroupEdit";
    import GroupUserEdit from "@/views/group/GroupUserEdit";

    export default {
        components: {
            GroupEdit, GroupUserEdit
        },

        data: function () {
            return {
                groupId: null,
                tabIndex: 1,
            }
        },

        computed: {
        },
        created() {
            pages.groupEditLayout = this;

            this.groupId = this.$route.query.groupId;
        },

        methods: {

        }
    }
</script>

<style lang="scss">
</style>
